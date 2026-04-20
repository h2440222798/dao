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
import com.yupi.yuoj.model.entity.CognitionRoutine;
import com.yupi.yuoj.service.CognitionRoutineService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认知修行节律接口
 */
@RestController
@RequestMapping("/cognitionRoutine")
public class CognitionRoutineController {

    @Resource
    private CognitionRoutineService cognitionRoutineService;

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addCognitionRoutine(@RequestBody CognitionRoutine cognitionRoutine) {
        if (cognitionRoutine == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        cognitionRoutine.setId(null);
        boolean result = cognitionRoutineService.save(cognitionRoutine);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(cognitionRoutine.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteCognitionRoutine(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(cognitionRoutineService.removeById(deleteRequest.getId()));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateCognitionRoutine(@RequestBody CognitionRoutine cognitionRoutine) {
        if (cognitionRoutine == null || cognitionRoutine.getId() == null || cognitionRoutine.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        CognitionRoutine oldCognitionRoutine = cognitionRoutineService.getById(cognitionRoutine.getId());
        ThrowUtils.throwIf(oldCognitionRoutine == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(cognitionRoutineService.updateById(cognitionRoutine));
    }

    @GetMapping("/get")
    public BaseResponse<CognitionRoutine> getCognitionRoutineById(@RequestParam long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        CognitionRoutine cognitionRoutine = cognitionRoutineService.getById(id);
        ThrowUtils.throwIf(cognitionRoutine == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(cognitionRoutine);
    }

    @GetMapping("/list")
    public BaseResponse<java.util.List<CognitionRoutine>> listCognitionRoutine() {
        QueryWrapper<CognitionRoutine> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(cognitionRoutineService.list(queryWrapper));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<CognitionRoutine>> listCognitionRoutineByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<CognitionRoutine> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(cognitionRoutineService.page(new Page<>(current, pageSize), queryWrapper));
    }
}
