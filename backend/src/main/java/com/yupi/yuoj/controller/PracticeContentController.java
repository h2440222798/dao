package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yuoj.annotation.AuthCheck;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.constant.UserConstant;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.entity.PracticeContent;
import com.yupi.yuoj.service.PracticeContentService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 修行内容接口
 */
@RestController
@RequestMapping("/practiceContent")
public class PracticeContentController {

    @Resource
    private PracticeContentService practiceContentService;

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> addPracticeContent(@RequestBody PracticeContent practiceContent) {
        if (practiceContent == null || practiceContent.getId() == null || practiceContent.getId().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PracticeContent oldPracticeContent = practiceContentService.getById(practiceContent.getId());
        ThrowUtils.throwIf(oldPracticeContent != null, ErrorCode.PARAMS_ERROR, "内容 id 已存在");
        boolean result = practiceContentService.save(practiceContent);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(practiceContent.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deletePracticeContentById(@RequestParam String id) {
        ThrowUtils.throwIf(id == null || id.trim().isEmpty(), ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(practiceContentService.removeById(id.trim()));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePracticeContent(@RequestBody PracticeContent practiceContent) {
        if (practiceContent == null || practiceContent.getId() == null || practiceContent.getId().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PracticeContent oldPracticeContent = practiceContentService.getById(practiceContent.getId());
        ThrowUtils.throwIf(oldPracticeContent == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(practiceContentService.updateById(practiceContent));
    }

    @GetMapping("/get")
    public BaseResponse<PracticeContent> getPracticeContentById(@RequestParam String id) {
        ThrowUtils.throwIf(id == null || id.trim().isEmpty(), ErrorCode.PARAMS_ERROR);
        PracticeContent practiceContent = practiceContentService.getById(id.trim());
        ThrowUtils.throwIf(practiceContent == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(practiceContent);
    }

    @GetMapping("/list")
    public BaseResponse<java.util.List<PracticeContent>> listPracticeContent(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String wuxing) {
        QueryWrapper<PracticeContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(type != null && !type.trim().isEmpty(), "type", type);
        queryWrapper.eq(wuxing != null && !wuxing.trim().isEmpty(), "wuxing", wuxing);
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(practiceContentService.list(queryWrapper));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<PracticeContent>> listPracticeContentByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String wuxing) {
        ThrowUtils.throwIf(pageSize > 50, ErrorCode.PARAMS_ERROR);
        QueryWrapper<PracticeContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(type != null && !type.trim().isEmpty(), "type", type);
        queryWrapper.eq(wuxing != null && !wuxing.trim().isEmpty(), "wuxing", wuxing);
        queryWrapper.orderByAsc("sort").orderByAsc("createTime");
        return ResultUtils.success(practiceContentService.page(new Page<>(current, pageSize), queryWrapper));
    }
}
