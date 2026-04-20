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
import com.yupi.yuoj.model.entity.PracticeModule;
import com.yupi.yuoj.service.PracticeModuleService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 修行模块接口
 */
@RestController
@RequestMapping("/practiceModule")
public class PracticeModuleController {

    @Resource
    private PracticeModuleService practiceModuleService;

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addPracticeModule(@RequestBody PracticeModule practiceModule) {
        if (practiceModule == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        practiceModule.setId(null);
        boolean result = practiceModuleService.save(practiceModule);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(practiceModule.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deletePracticeModule(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(practiceModuleService.removeById(deleteRequest.getId()));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePracticeModule(@RequestBody PracticeModule practiceModule) {
        if (practiceModule == null || practiceModule.getId() == null || practiceModule.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PracticeModule oldPracticeModule = practiceModuleService.getById(practiceModule.getId());
        ThrowUtils.throwIf(oldPracticeModule == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(practiceModuleService.updateById(practiceModule));
    }

    @GetMapping("/get")
    public BaseResponse<PracticeModule> getPracticeModuleById(@RequestParam long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        PracticeModule practiceModule = practiceModuleService.getById(id);
        ThrowUtils.throwIf(practiceModule == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(practiceModule);
    }

    @GetMapping("/get/by/type")
    public BaseResponse<PracticeModule> getPracticeModuleByType(@RequestParam String type) {
        ThrowUtils.throwIf(type == null || type.trim().isEmpty(), ErrorCode.PARAMS_ERROR);
        QueryWrapper<PracticeModule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type.trim());
        PracticeModule practiceModule = practiceModuleService.getOne(queryWrapper);
        ThrowUtils.throwIf(practiceModule == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(practiceModule);
    }

    @GetMapping("/list")
    public BaseResponse<java.util.List<PracticeModule>> listPracticeModule() {
        QueryWrapper<PracticeModule> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(practiceModuleService.list(queryWrapper));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<PracticeModule>> listPracticeModuleByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<PracticeModule> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(practiceModuleService.page(new Page<>(current, pageSize), queryWrapper));
    }
}
