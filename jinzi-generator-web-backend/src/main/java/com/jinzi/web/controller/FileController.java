package com.jinzi.web.controller;

import cn.hutool.core.io.FileUtil;
import com.jinzi.web.annotation.AuthCheck;
import com.jinzi.web.common.BaseResponse;
import com.jinzi.web.common.ErrorCode;
import com.jinzi.web.common.ResultUtils;
import com.jinzi.web.constant.FileConstant;
import com.jinzi.web.constant.UserConstant;
import com.jinzi.web.exception.BusinessException;
import com.jinzi.web.manager.CosManager;
import com.jinzi.web.model.dto.file.UploadFileRequest;
import com.jinzi.web.model.entity.User;
import com.jinzi.web.model.enums.FileUploadBizEnum;
import com.jinzi.web.model.enums.ImageStatusEnum;
import com.jinzi.web.model.vo.ImageVo;
import com.jinzi.web.model.vo.UserVO;
import com.jinzi.web.service.GeneratorService;
import com.jinzi.web.service.UserService;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * 文件接口
 *
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    final long ONE_M = 1024 * 1024L;

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private GeneratorService generatorService;


    /**
     * 文件上传
     *
     * @param multipartFile 文件信息
     * @param uploadFileRequest 文件上传请求
     * @param request 请求信息
     * @return 文件地址
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile, UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
        UserVO loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return ResultUtils.success(filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }
    /**
     * 上传文件
     *
     * @param multipartFile     多部分文件
     * @param uploadFileRequest 上传文件请求
     * @param request           请求
     * @return {@link BaseResponse}<{@link ImageVo}>
     */
    @PostMapping("/upload/two")
    public BaseResponse<ImageVo> uploadFileTwo(@RequestPart("file") MultipartFile multipartFile, UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        ImageVo imageVo = new ImageVo();
        if (fileUploadBizEnum == null) {
            return uploadError(imageVo, multipartFile, "上传失败,情重试.");
        }
        String result = validFile(multipartFile, fileUploadBizEnum);
        if (!"success".equals(result)) {
            return uploadError(imageVo, multipartFile, result);
        }
        UserVO loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        File file = null;

        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            imageVo.setName(multipartFile.getOriginalFilename());
            imageVo.setUid(RandomStringUtils.randomAlphanumeric(8));
            imageVo.setStatus(ImageStatusEnum.SUCCESS.getValue());
            imageVo.setUrl(FileConstant.COS_HOST + filepath);
            // 返回可访问地址
            return ResultUtils.success(imageVo);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            return uploadError(imageVo, multipartFile, "上传失败,情重试");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }
    private BaseResponse<ImageVo> uploadError(ImageVo imageVo, MultipartFile multipartFile, String message) {
        imageVo.setName(multipartFile.getOriginalFilename());
        imageVo.setUid(RandomStringUtils.randomAlphanumeric(8));
        imageVo.setStatus(ImageStatusEnum.ERROR.getValue());
        return ResultUtils.error(imageVo, ErrorCode.OPERATION_ERROR, message);
    }
    /**
     * 校验文件
     *
     * @param multipartFile 文件信息
     * @param fileUploadBizEnum 业务类型
     */
    private String validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                return "文件大小不能超过 1M";
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp","jiff").contains(fileSuffix)) {
                return "文件类型错误";
            }
        }
        return "success";
    }
    /**
     * 测试文件上传
     *
     * @param multipartFile 文件
     * @return 是否上传成功
     *  流程： 先接受用户上传单位文件 指定上传路径 然后调用 cosManager.putObject 方法上传到COS对象存储，上传成功后会返回一个文件的Key
     *  其实就是文件的路径，便于访问和下载文件
     */
    @PostMapping("/test/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)  // 添加管理员权限
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        // 文件目录：根据业务、用户来划分
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s", filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            return ResultUtils.success(filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }
    /**
     * 测试文件下载
     *
     * @param filepath 文件相对路径
     * @param response 响应信息
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)  // 这里指定的是只有管理员才可以执行此方法
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头，这里设置 utf-8解决中文乱码问题
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }
}
