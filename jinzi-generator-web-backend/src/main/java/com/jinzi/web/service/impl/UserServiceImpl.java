package com.jinzi.web.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinzi.web.common.ErrorCode;
import com.jinzi.web.constant.CommonConstant;
import com.jinzi.web.constant.UserAccountStatusEnum;
import com.jinzi.web.exception.BusinessException;
import com.jinzi.web.mapper.UserMapper;
import com.jinzi.web.model.dto.user.*;
import com.jinzi.web.model.entity.User;
import com.jinzi.web.model.enums.UserRoleEnum;
import com.jinzi.web.model.vo.LoginUserVO;
import com.jinzi.web.model.vo.UserVO;
import com.jinzi.web.service.UserService;
import com.jinzi.web.utils.RedissonLockUtil;
import com.jinzi.web.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.jinzi.web.constant.EmailConstant.CAPTCHA_CACHE_KEY;
import static com.jinzi.web.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 *
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "jinzi";

    @Resource
    private RedissonLockUtil redissonLockUtil;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private UserMapper userMapper;


    /**
     * 用户账号密码注册
     * @param userRegisterRequest 注册请求信息
     * @return 返回注册之后的用户Id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String userName = userRegisterRequest.getUserName();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String invitationCode = userRegisterRequest.getInvitationCode();

        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userName.length() > 40) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "昵称过长");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        //  5. 账户不包含特殊字符
        // 匹配由数字、小写字母、大写字母组成的字符串,且字符串的长度至少为1个字符
        String pattern = "[0-9a-zA-Z]+";
        if (!userAccount.matches(pattern)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号由数字、小写字母、大写字母组成");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 这里设置分布式锁，防止多个用户在不同的服务器上注册相同账号密码的用户，保证唯一性
        String redissonLock = ("userRegister_" + userAccount).intern();
        return redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            User invitationCodeUser = null;
            if (StringUtils.isNotBlank(invitationCode)) {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.eq(User::getInvitationCode, invitationCode);
                // 可能出现重复invitationCode,查出的不是一条
                invitationCodeUser = this.getOne(userLambdaQueryWrapper);
                if (invitationCodeUser == null) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "该邀请码无效");
                }
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserName(userName);
            if (invitationCodeUser != null) {
                user.setBalance(100);
                this.addWalletBalance(invitationCodeUser.getId(), 100);
            }
            // 生成随机的8位数 作为邀请码
            user.setInvitationCode(generateRandomString(8));
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }, "注册账号失败");
    }

    /**
     * 用户电子邮件注册
     *
     * @param userEmailRegisterRequest 用户电子邮件注册请求
     * @return long
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long userEmailRegister(UserEmailRegisterRequest userEmailRegisterRequest) {
        String emailAccount = userEmailRegisterRequest.getEmailAccount();
        // 验证码
        String captcha = userEmailRegisterRequest.getCaptcha();
        String userName = userEmailRegisterRequest.getUserName();
        String invitationCode = userEmailRegisterRequest.getInvitationCode();
        if (StringUtils.isAnyBlank(emailAccount, captcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userName.length() > 40) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "昵称过长");
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailPattern, emailAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不合法的邮箱地址！");
        }
        // 在redis  缓存中得到用户的验证码，这里的验证码是一个时效值，过一分钟就会时效，没有必要存储在MySQL数据库中，在redis中设置验证码过期时间即可
        String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码已过期,请重新获取");
        }
        captcha = captcha.trim();
        if (!cacheCaptcha.equals(captcha)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码输入有误");
        }
        String redissonLock = ("userEmailRegister_" + emailAccount).intern();
        return redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", emailAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            User invitationCodeUser = null;
            if (StringUtils.isNotBlank(invitationCode)) {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.eq(User::getInvitationCode, invitationCode);
                // 可能出现重复invitationCode,查出的不是一条
                invitationCodeUser = this.getOne(userLambdaQueryWrapper);
                if (invitationCodeUser == null) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "该邀请码无效");
                }
            }
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(emailAccount);
            user.setUserName(userName);
            user.setEmail(emailAccount);
            if (invitationCodeUser != null) {
                user.setBalance(100);
                this.addWalletBalance(invitationCodeUser.getId(), 100);
            }
            user.setInvitationCode(generateRandomString(8));
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }, "邮箱账号注册失败");
    }


    /**
     * 用户 账号密码登录
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request 为了获得用户的登录态
     * @return 返回脱敏 LoginUserVO
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 用户电子邮件登录
     *
     * @param userEmailLoginRequest 用户电子邮件登录请求
     * @param request               要求
     * @return {@link UserVO}
     */
    @Override
    public UserVO userEmailLogin(UserEmailLoginRequest userEmailLoginRequest, HttpServletRequest request) {
        String emailAccount = userEmailLoginRequest.getEmailAccount();
        String captcha = userEmailLoginRequest.getCaptcha();

        if (StringUtils.isAnyBlank(emailAccount, captcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailPattern, emailAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不合法的邮箱地址！");
        }
        String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码已过期,请重新获取");
        }
        captcha = captcha.trim();
        if (!cacheCaptcha.equals(captcha)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码输入有误");
        }
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", emailAccount);
        User user = userMapper.selectOne(queryWrapper);

        // 用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该邮箱未绑定账号，请先绑定账号");
        }

        if (user.getStatus().equals(UserAccountStatusEnum.BAN.getValue())) {
            throw new BusinessException(ErrorCode.PROHIBITED, "账号已封禁");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, userVO);
        return userVO;
    }

    /**
     * 绑定电子邮件
     *
     * @param userEmailLoginRequest 用户电子邮件登录请求
     * @param request               要求
     * @return
     */
    @Override
    public UserVO userBindEmail(UserBindEmailRequest userEmailLoginRequest, HttpServletRequest request) {
        String emailAccount = userEmailLoginRequest.getEmailAccount();
        String captcha = userEmailLoginRequest.getCaptcha();
        if (StringUtils.isAnyBlank(emailAccount, captcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailPattern, emailAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不合法的邮箱地址！");
        }
        String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码已过期,请重新获取");
        }
        captcha = captcha.trim();
        if (!cacheCaptcha.equals(captcha)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码输入有误");
        }
        // 查询用户是否绑定该邮箱
        User loginUser = this.getLoginUser(request);
        if (loginUser.getEmail() != null && emailAccount.equals(loginUser.getEmail())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该账号已绑定此邮箱,请更换新的邮箱！");
        }
        // 查询邮箱是否已经绑定
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", emailAccount);
        User user = this.getOne(queryWrapper);
        if (user != null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "此邮箱已被绑定,请更换新的邮箱！");
        }
        user = new User();
        user.setId(loginUser.getId());
        user.setEmail(emailAccount);
        boolean bindEmailResult = this.updateById(user);
        if (!bindEmailResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "邮箱绑定失败,请稍后再试！");
        }
        loginUser.setEmail(emailAccount);
        UserVO retUser = new UserVO();
        BeanUtils.copyProperties(retUser,loginUser);
        return retUser;
    }

    /**
     * 解绑电子邮件
     *
     * @param userUnBindEmailRequest 用户取消绑定电子邮件请求
     * @param request                要求
     * @return
     */
    @Override
    public UserVO userUnBindEmail(UserUnBindEmailRequest userUnBindEmailRequest, HttpServletRequest request) {
        String emailAccount = userUnBindEmailRequest.getEmailAccount();
        String captcha = userUnBindEmailRequest.getCaptcha();
        if (StringUtils.isAnyBlank(emailAccount, captcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailPattern, emailAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不合法的邮箱地址！");
        }
        String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码已过期,请重新获取");
        }
        captcha = captcha.trim();
        if (!cacheCaptcha.equals(captcha)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码输入有误");
        }
        // 查询用户是否绑定该邮箱
        User loginUser = this.getLoginUser(request);
        if (loginUser.getEmail() == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该账号未绑定邮箱");
        }
        if (!emailAccount.equals(loginUser.getEmail())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该账号未绑定此邮箱");
        }
        User user = new User();
        user.setId(loginUser.getId());
        user.setEmail("");
        boolean bindEmailResult = this.updateById(user);
        if (!bindEmailResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "邮箱解绑失败,请稍后再试！");
        }
        loginUser.setEmail(null);
        UserVO retUser = new UserVO();
        BeanUtils.copyProperties(retUser,loginUser);
        return retUser;
    }
    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = new User();
        Class<?> clazz = userObj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        List<String> fieldList = new ArrayList<>();
        for (Field field : fields) {
            // 设置为可访问，以便访问私有字段
            field.setAccessible(true);
            fieldList.add(field.getName());
        }
        if(!fieldList.contains("userPassword")){
            currentUser.setId(((UserVO) userObj).getId());
            currentUser.setUserName(((UserVO) userObj).getUserName());
            currentUser.setEmail(((UserVO) userObj).getEmail());
            currentUser.setUserAccount(((UserVO) userObj).getUserAccount());
            currentUser.setGender(((UserVO) userObj).getGender());
            currentUser.setUserAvatar(((UserVO) userObj).getUserAvatar());
            currentUser.setUserProfile(((UserVO) userObj).getUserProfile());
            currentUser.setUserRole(((UserVO) userObj).getUserRole());
            currentUser.setBalance(((UserVO) userObj).getBalance());
            currentUser.setInvitationCode(((UserVO) userObj).getInvitationCode());
            currentUser.setCreateTime(((UserVO) userObj).getCreateTime());
            currentUser.setUpdateTime(((UserVO) userObj).getCreateTime());
        }else{
            currentUser = (User) userObj;
        }
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);

        User currentUser = new User();
        Class<?> clazz = userObj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        List<String> fieldList = new ArrayList<>();
        for (Field field : fields) {
            // 设置为可访问，以便访问私有字段
            field.setAccessible(true);
            fieldList.add(field.getName());
        }
        if(!fieldList.contains("userPassword")){
            currentUser.setId(((UserVO) userObj).getId());
            currentUser.setUserName(((UserVO) userObj).getUserName());
            currentUser.setEmail(((UserVO) userObj).getEmail());
            currentUser.setUserAccount(((UserVO) userObj).getUserAccount());
            currentUser.setGender(((UserVO) userObj).getGender());
            currentUser.setUserAvatar(((UserVO) userObj).getUserAvatar());
            currentUser.setUserProfile(((UserVO) userObj).getUserProfile());
            currentUser.setUserRole(((UserVO) userObj).getUserRole());
            currentUser.setBalance(((UserVO) userObj).getBalance());
            currentUser.setInvitationCode(((UserVO) userObj).getInvitationCode());
            currentUser.setCreateTime(((UserVO) userObj).getCreateTime());
            currentUser.setUpdateTime(((UserVO) userObj).getCreateTime());
        }else{
            currentUser = (User) userObj;
        }

        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userObj,userVO);
        if(userVO.getUserRole().equals("user")){
            return false;
        }
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }


    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * 整合查询参数
     *
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 添加余额
     * @param userId    用户id
     * @param addPoints 添加点
     * @return
     */
    @Override
    public boolean addWalletBalance(Long userId, Integer addPoints) {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, userId);
        userLambdaUpdateWrapper.setSql("balance = balance + " + addPoints);
        return this.update(userLambdaUpdateWrapper);
    }

    /**
     * 生成随机字符串
     *
     * @param length 长
     * @return {@link String}
     */
    public String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    /**
     * 校验用户信息
     * @param user 接口信息
     * @param add  是否为创建校验
     */
    @Override
    public void validUser(User user, boolean add) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = user.getUserAccount();
        String userPassword = user.getUserPassword();
        Integer balance = user.getBalance();

        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(userAccount, userPassword)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            // 添加用户生成8位邀请码
            user.setInvitationCode(generateRandomString(8));
        }
        //  5. 账户不包含特殊字符
        // 匹配由数字、小写字母、大写字母组成的字符串,且字符串的长度至少为1个字符
        String pattern = "[0-9a-zA-Z]+";
        if (StringUtils.isNotBlank(userAccount) && !userAccount.matches(pattern)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号由数字、小写字母、大写字母组成");
        }
        if (ObjectUtils.isNotEmpty(balance) && balance < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "钱包余额不能为负数");
        }
        if (StringUtils.isNotBlank(userPassword)) {
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            user.setUserPassword(encryptPassword);
        }
        // 账户不能重复
        if (StringUtils.isNotBlank(userAccount)) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
        }
    }
    @Override
    public boolean reduceWalletBalance(Long userId, Integer reduceScore) {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, userId);
        userLambdaUpdateWrapper.setSql("balance = balance - " + reduceScore);
        return this.update(userLambdaUpdateWrapper);
    }

}
