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
import com.yupi.yuoj.model.entity.CognitionOrigin;
import com.yupi.yuoj.service.CognitionOriginService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认知修行来源接口
 */
@RestController
@RequestMapping("/cognitionOrigin")
public class CognitionOriginController {

    @Resource
    private CognitionOriginService cognitionOriginService;

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addCognitionOrigin(@RequestBody CognitionOrigin cognitionOrigin) {
        if (cognitionOrigin == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        cognitionOrigin.setId(null);
        boolean result = cognitionOriginService.save(cognitionOrigin);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(cognitionOrigin.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteCognitionOrigin(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(cognitionOriginService.removeById(deleteRequest.getId()));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateCognitionOrigin(@RequestBody CognitionOrigin cognitionOrigin) {
        if (cognitionOrigin == null || cognitionOrigin.getId() == null || cognitionOrigin.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        CognitionOrigin oldCognitionOrigin = cognitionOriginService.getById(cognitionOrigin.getId());
        ThrowUtils.throwIf(oldCognitionOrigin == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(cognitionOriginService.updateById(cognitionOrigin));
    }

    @GetMapping("/get")
    public BaseResponse<CognitionOrigin> getCognitionOriginById(@RequestParam long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        CognitionOrigin cognitionOrigin = cognitionOriginService.getById(id);
        ThrowUtils.throwIf(cognitionOrigin == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(cognitionOrigin);
    }

    @GetMapping("/list")
    public BaseResponse<java.util.List<CognitionOrigin>> listCognitionOrigin() {
        QueryWrapper<CognitionOrigin> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(cognitionOriginService.list(queryWrapper));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<CognitionOrigin>> listCognitionOriginByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<CognitionOrigin> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(cognitionOriginService.page(new Page<>(current, pageSize), queryWrapper));
    }
}
