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
import com.yupi.yuoj.model.entity.CognitionReading;
import com.yupi.yuoj.service.CognitionReadingService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认知修行阅读接口
 */
@RestController
@RequestMapping("/cognitionReading")
public class CognitionReadingController {

    @Resource
    private CognitionReadingService cognitionReadingService;

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addCognitionReading(@RequestBody CognitionReading cognitionReading) {
        if (cognitionReading == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        cognitionReading.setId(null);
        boolean result = cognitionReadingService.save(cognitionReading);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(cognitionReading.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteCognitionReading(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(cognitionReadingService.removeById(deleteRequest.getId()));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateCognitionReading(@RequestBody CognitionReading cognitionReading) {
        if (cognitionReading == null || cognitionReading.getId() == null || cognitionReading.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        CognitionReading oldCognitionReading = cognitionReadingService.getById(cognitionReading.getId());
        ThrowUtils.throwIf(oldCognitionReading == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(cognitionReadingService.updateById(cognitionReading));
    }

    @GetMapping("/get")
    public BaseResponse<CognitionReading> getCognitionReadingById(@RequestParam long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        CognitionReading cognitionReading = cognitionReadingService.getById(id);
        ThrowUtils.throwIf(cognitionReading == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(cognitionReading);
    }

    @GetMapping("/list")
    public BaseResponse<java.util.List<CognitionReading>> listCognitionReading() {
        QueryWrapper<CognitionReading> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(cognitionReadingService.list(queryWrapper));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<CognitionReading>> listCognitionReadingByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<CognitionReading> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(cognitionReadingService.page(new Page<>(current, pageSize), queryWrapper));
    }
}
