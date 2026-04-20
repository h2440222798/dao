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
import com.yupi.yuoj.model.entity.DiaryComment;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.service.DiaryCommentService;
import com.yupi.yuoj.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日记评论接口
 */
@RestController
@RequestMapping("/diary_comment")
public class DiaryCommentController {

    @Resource
    private DiaryCommentService diaryCommentService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addDiaryComment(@RequestBody DiaryComment diaryComment, HttpServletRequest request) {
        if (diaryComment == null || diaryComment.getDiaryid() == null || diaryComment.getDiaryid() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (diaryComment.getContent() == null || diaryComment.getContent().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
        }
        User loginUser = userService.getLoginUser(request);
        diaryComment.setId(null);
        diaryComment.setUserid(loginUser.getId());
        boolean result = diaryCommentService.save(diaryComment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(diaryComment.getId());
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteDiaryComment(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DiaryComment oldComment = diaryCommentService.getById(deleteRequest.getId());
        ThrowUtils.throwIf(oldComment == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (!loginUser.getId().equals(oldComment.getUserid()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(diaryCommentService.removeById(deleteRequest.getId()));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateDiaryComment(@RequestBody DiaryComment diaryComment) {
        if (diaryComment == null || diaryComment.getId() == null || diaryComment.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DiaryComment oldComment = diaryCommentService.getById(diaryComment.getId());
        ThrowUtils.throwIf(oldComment == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(diaryCommentService.updateById(diaryComment));
    }

    @GetMapping("/list/by_diary")
    public BaseResponse<Page<DiaryComment>> listCommentByDiaryId(@RequestParam long diaryId,
            @RequestParam(defaultValue = "1") long current, @RequestParam(defaultValue = "10") long pageSize) {
        ThrowUtils.throwIf(diaryId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(pageSize > 50, ErrorCode.PARAMS_ERROR);
        QueryWrapper<DiaryComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("diaryId", diaryId);
        queryWrapper.orderByDesc("createTime");
        return ResultUtils.success(diaryCommentService.page(new Page<>(current, pageSize), queryWrapper));
    }
}

