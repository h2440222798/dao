package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yuoj.annotation.AuthCheck;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.DeleteRequest;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.constant.UserConstant;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.model.entity.UserCheckin;
import com.yupi.yuoj.service.UserCheckinService;
import com.yupi.yuoj.service.UserService;
import java.util.Objects;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户修行打卡接口
 */
@RestController
@RequestMapping("/userCheckin")
public class UserCheckinController {

    @Resource
    private UserCheckinService userCheckinService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addUserCheckin(@RequestBody UserCheckin userCheckin, HttpServletRequest request) {
        if (userCheckin == null || userCheckin.getRecorddate() == null || userCheckin.getRecorddate().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        userCheckin.setId(null);
        userCheckin.setUserid(loginUser.getId());
        boolean result = userCheckinService.save(userCheckin);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(userCheckin.getId());
    }

    @PostMapping("/save")
    public BaseResponse<Long> saveOrUpdateMyCheckin(@RequestBody UserCheckin userCheckin, HttpServletRequest request) {
        if (userCheckin == null || userCheckin.getRecorddate() == null || userCheckin.getRecorddate().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserCheckin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.eq("recordDate", userCheckin.getRecorddate().trim());
        UserCheckin oldUserCheckin = userCheckinService.getOne(queryWrapper);
        userCheckin.setUserid(loginUser.getId());
        if (oldUserCheckin != null) {
            userCheckin.setId(oldUserCheckin.getId());
            boolean result = userCheckinService.updateById(userCheckin);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            return ResultUtils.success(userCheckin.getId());
        }
        userCheckin.setId(null);
        boolean result = userCheckinService.save(userCheckin);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(userCheckin.getId());
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserCheckin(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        UserCheckin oldUserCheckin = userCheckinService.getById(deleteRequest.getId());
        ThrowUtils.throwIf(oldUserCheckin == null, ErrorCode.NOT_FOUND_ERROR);
        boolean isAdmin = userService.isAdmin(loginUser);
        if (!isAdmin && !Objects.equals(oldUserCheckin.getUserid(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(userCheckinService.removeById(deleteRequest.getId()));
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateUserCheckin(@RequestBody UserCheckin userCheckin, HttpServletRequest request) {
        if (userCheckin == null || userCheckin.getId() == null || userCheckin.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        UserCheckin oldUserCheckin = userCheckinService.getById(userCheckin.getId());
        ThrowUtils.throwIf(oldUserCheckin == null, ErrorCode.NOT_FOUND_ERROR);
        boolean isAdmin = userService.isAdmin(loginUser);
        if (!isAdmin && !Objects.equals(oldUserCheckin.getUserid(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (!isAdmin) {
            userCheckin.setUserid(loginUser.getId());
        }
        return ResultUtils.success(userCheckinService.updateById(userCheckin));
    }

    @GetMapping("/get")
    public BaseResponse<UserCheckin> getUserCheckinById(@RequestParam long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        UserCheckin userCheckin = userCheckinService.getById(id);
        ThrowUtils.throwIf(userCheckin == null, ErrorCode.NOT_FOUND_ERROR);
        boolean isAdmin = userService.isAdmin(loginUser);
        if (!isAdmin && !Objects.equals(userCheckin.getUserid(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(userCheckin);
    }

    @GetMapping("/my/get/by/date")
    public BaseResponse<UserCheckin> getMyUserCheckinByDate(@RequestParam String recordDate, HttpServletRequest request) {
        ThrowUtils.throwIf(recordDate == null || recordDate.trim().isEmpty(), ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserCheckin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.eq("recordDate", recordDate.trim());
        return ResultUtils.success(userCheckinService.getOne(queryWrapper));
    }

    @GetMapping("/my/list")
    public BaseResponse<java.util.List<UserCheckin>> listMyUserCheckin(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserCheckin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.orderByDesc("recordDate").orderByDesc("createTime");
        return ResultUtils.success(userCheckinService.list(queryWrapper));
    }

    @PostMapping("/my/list/page")
    public BaseResponse<Page<UserCheckin>> listMyUserCheckinByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize,
            HttpServletRequest request) {
        ThrowUtils.throwIf(pageSize > 50, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserCheckin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.orderByDesc("recordDate").orderByDesc("createTime");
        return ResultUtils.success(userCheckinService.page(new Page<>(current, pageSize), queryWrapper));
    }

    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserCheckin>> listUserCheckinByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String recordDate) {
        ThrowUtils.throwIf(pageSize > 50, ErrorCode.PARAMS_ERROR);
        QueryWrapper<UserCheckin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userId != null && userId > 0, "userId", userId);
        queryWrapper.eq(recordDate != null && !recordDate.trim().isEmpty(), "recordDate", recordDate);
        queryWrapper.orderByDesc("recordDate").orderByDesc("createTime");
        return ResultUtils.success(userCheckinService.page(new Page<>(current, pageSize), queryWrapper));
    }
}
