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
import com.yupi.yuoj.model.entity.CognitionIntegrationStep;
import com.yupi.yuoj.service.CognitionIntegrationStepService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认知整合步骤接口
 */
@RestController
@RequestMapping("/cognitionIntegrationStep")
public class CognitionIntegrationStepController {

    @Resource
    private CognitionIntegrationStepService cognitionIntegrationStepService;

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addCognitionIntegrationStep(@RequestBody CognitionIntegrationStep step) {
        if (step == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        step.setId(null);
        boolean result = cognitionIntegrationStepService.save(step);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(step.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteCognitionIntegrationStep(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(cognitionIntegrationStepService.removeById(deleteRequest.getId()));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateCognitionIntegrationStep(@RequestBody CognitionIntegrationStep step) {
        if (step == null || step.getId() == null || step.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        CognitionIntegrationStep oldStep = cognitionIntegrationStepService.getById(step.getId());
        ThrowUtils.throwIf(oldStep == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(cognitionIntegrationStepService.updateById(step));
    }

    @GetMapping("/get")
    public BaseResponse<CognitionIntegrationStep> getCognitionIntegrationStepById(@RequestParam long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        CognitionIntegrationStep step = cognitionIntegrationStepService.getById(id);
        ThrowUtils.throwIf(step == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(step);
    }

    @GetMapping("/list")
    public BaseResponse<java.util.List<CognitionIntegrationStep>> listCognitionIntegrationStep() {
        QueryWrapper<CognitionIntegrationStep> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(cognitionIntegrationStepService.list(queryWrapper));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<CognitionIntegrationStep>> listCognitionIntegrationStepByPage(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<CognitionIntegrationStep> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(cognitionIntegrationStepService.page(new Page<>(current, pageSize), queryWrapper));
    }
}
