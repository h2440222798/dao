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
import com.yupi.yuoj.model.entity.WuxingTopic;
import com.yupi.yuoj.service.WuxingTopicService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 五行专题接口
 */
@RestController
@RequestMapping("/wuxingTopic")
public class WuxingTopicController {

    @Resource
    private WuxingTopicService wuxingTopicService;

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addWuxingTopic(@RequestBody WuxingTopic wuxingTopic) {
        if (wuxingTopic == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        wuxingTopic.setId(null);
        boolean result = wuxingTopicService.save(wuxingTopic);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(wuxingTopic.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteWuxingTopic(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(wuxingTopicService.removeById(deleteRequest.getId()));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateWuxingTopic(@RequestBody WuxingTopic wuxingTopic) {
        if (wuxingTopic == null || wuxingTopic.getId() == null || wuxingTopic.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        WuxingTopic oldWuxingTopic = wuxingTopicService.getById(wuxingTopic.getId());
        ThrowUtils.throwIf(oldWuxingTopic == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(wuxingTopicService.updateById(wuxingTopic));
    }

    @GetMapping("/get")
    public BaseResponse<WuxingTopic> getWuxingTopicById(@RequestParam long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        WuxingTopic wuxingTopic = wuxingTopicService.getById(id);
        ThrowUtils.throwIf(wuxingTopic == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(wuxingTopic);
    }

    @GetMapping("/get/by/key")
    public BaseResponse<WuxingTopic> getWuxingTopicByKey(@RequestParam String topicKey) {
        ThrowUtils.throwIf(topicKey == null || topicKey.trim().isEmpty(), ErrorCode.PARAMS_ERROR);
        QueryWrapper<WuxingTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topicKey", topicKey.trim());
        WuxingTopic wuxingTopic = wuxingTopicService.getOne(queryWrapper);
        ThrowUtils.throwIf(wuxingTopic == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(wuxingTopic);
    }

    @GetMapping("/list")
    public BaseResponse<java.util.List<WuxingTopic>> listWuxingTopic() {
        QueryWrapper<WuxingTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(wuxingTopicService.list(queryWrapper));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<WuxingTopic>> listWuxingTopicByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<WuxingTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(wuxingTopicService.page(new Page<>(current, pageSize), queryWrapper));
    }
}
