package com.jinzi.web.controller;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.jinzi.web.annotation.AuthCheck;
import com.jinzi.web.common.BaseResponse;
import com.jinzi.web.common.DeleteRequest;
import com.jinzi.web.common.ErrorCode;
import com.jinzi.web.common.ResultUtils;
import com.jinzi.web.config.EmailConfig;
import com.jinzi.web.exception.BusinessException;
import com.jinzi.web.exception.ThrowUtils;
import com.jinzi.web.model.dto.user.*;
import com.jinzi.web.model.entity.User;
import com.jinzi.web.model.vo.LoginUserVO;
import com.jinzi.web.model.vo.UserVO;
import com.jinzi.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.jinzi.web.constant.EmailConstant.*;
import static com.jinzi.web.constant.UserConstant.ADMIN_ROLE;
import static com.jinzi.web.service.impl.UserServiceImpl.SALT;
import static com.jinzi.web.utils.EmailUtil.buildEmailContent;

/**
 * 用户接口
 *
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private EmailConfig emailConfig;
    @Resource
    private UserService userService;
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }

    /**
     * 用户电子邮件注册
     *
     * @param userEmailRegisterRequest 用户电子邮件注册请求
     * @return {@link BaseResponse}<{@link UserVO}>
     */
    @PostMapping("/email/register")
    public BaseResponse<Long> userEmailRegister(@RequestBody UserEmailRegisterRequest userEmailRegisterRequest) {
        if (userEmailRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userEmailRegister(userEmailRegisterRequest);
        redisTemplate.delete(CAPTCHA_CACHE_KEY + userEmailRegisterRequest.getEmailAccount());
        return ResultUtils.success(result);
    }

    /**
     * 获取验证码
     *
     * @param emailAccount 电子邮件帐户
     * @return {@link BaseResponse}<{@link String}>
     */
    @GetMapping("/getCaptcha")
    public BaseResponse<Boolean> getCaptcha(String emailAccount) {
        if (StringUtils.isBlank(emailAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailPattern, emailAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不合法的邮箱地址！");
        }
        String captcha = RandomUtil.randomNumbers(6);
        try {
            sendEmail(emailAccount, captcha);
            redisTemplate.opsForValue().set(CAPTCHA_CACHE_KEY + emailAccount, captcha, 5, TimeUnit.MINUTES);
            return ResultUtils.success(true);
        } catch (Exception e) {
            log.error("【发送验证码失败】" + e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码获取失败");
        }
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }
    /**
     * 用户电子邮件登录
     *
     * @param userEmailLoginRequest 用户登录请求
     * @param request               请求
     * @return {@link BaseResponse}<{@link User}>
     */
    @PostMapping("/email/login")
    public BaseResponse<UserVO> userEmailLogin(@RequestBody UserEmailLoginRequest userEmailLoginRequest, HttpServletRequest request) {
        if (userEmailLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO user = userService.userEmailLogin(userEmailLoginRequest, request);
        redisTemplate.delete(CAPTCHA_CACHE_KEY + userEmailLoginRequest.getEmailAccount());
        return ResultUtils.success(user);
    }

    /**
     * 用户绑定电子邮件
     *
     * @param request              请求
     * @param userBindEmailRequest 用户绑定电子邮件请求
     * @return {@link BaseResponse}<{@link UserVO}>
     */
    @PostMapping("/bind/login")
    public BaseResponse<UserVO> userBindEmail(@RequestBody UserBindEmailRequest userBindEmailRequest, HttpServletRequest request) {
        if (userBindEmailRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO user = userService.userBindEmail(userBindEmailRequest, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户取消绑定电子邮件
     *
     * @param request                请求
     * @param userUnBindEmailRequest 用户取消绑定电子邮件请求
     * @return {@link BaseResponse}<{@link UserVO}>
     */
    @PostMapping("/unbindEmail")
    public BaseResponse<UserVO> userUnBindEmail(@RequestBody UserUnBindEmailRequest userUnBindEmailRequest, HttpServletRequest request) {
        if (userUnBindEmailRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO user = userService.userUnBindEmail(userUnBindEmailRequest, request);
        redisTemplate.delete(CAPTCHA_CACHE_KEY + userUnBindEmailRequest.getEmailAccount());
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }


    /**
     * 创建用户
     *
     * @param userAddRequest
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        String defaultPassword = "12345678";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<UserVO> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (ObjectUtils.anyNull(userUpdateRequest, userUpdateRequest.getId()) || userUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 管理员才能操作
        boolean adminOperation = ObjectUtils.isNotEmpty(userUpdateRequest.getBalance())
                || StringUtils.isNoneBlank(userUpdateRequest.getUserRole())
                || StringUtils.isNoneBlank(userUpdateRequest.getUserPassword());
        // 校验是否登录
        User loginUser = userService.getLoginUser(request);
        // 处理管理员业务,不是管理员抛异常
        if (adminOperation && !loginUser.getUserRole().equals(ADMIN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        if (!loginUser.getUserRole().equals(ADMIN_ROLE) && !userUpdateRequest.getId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "只有本人或管理员可以修改");
        }

        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        // 参数校验
        userService.validUser(user, false);

        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, user.getId());

        boolean result = userService.update(user, userLambdaUpdateWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新失败");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userService.getById(user.getId()), userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @return
     */
    @GetMapping("/list/page")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest) {
        User userQuery = new User();
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        BeanUtils.copyProperties(userQueryRequest, userQuery);

        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String gender = userQueryRequest.getGender();
        String userRole = userQueryRequest.getUserRole();
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName)
                .eq(StringUtils.isNotBlank(userAccount), "userAccount", userAccount)
                .eq(StringUtils.isNotBlank(gender), "gender", gender)
                .eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        Page<User> userPage = userService.page(new Page<>(current, pageSize), queryWrapper);
        Page<UserVO> userVoPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVoPage.setRecords(userVOList);
        return ResultUtils.success(userVoPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }



    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest, HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 发送邮件
     * @param emailAccount
     * @param captcha
     * @throws MessagingException
     */
    private void sendEmail(String emailAccount, String captcha) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        // 邮箱发送内容组成
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(EMAIL_SUBJECT);
        helper.setText(buildEmailContent(EMAIL_HTML_CONTENT_PATH, captcha), true);
        helper.setTo(emailAccount);
        helper.setFrom(EMAIL_TITLE + '<' + emailConfig.getEmailFrom() + '>');
        mailSender.send(message);
    }
}
