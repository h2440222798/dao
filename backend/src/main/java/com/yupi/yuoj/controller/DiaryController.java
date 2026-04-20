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
import com.yupi.yuoj.model.entity.Diary;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.model.vo.UserVO;
import com.yupi.yuoj.service.DiaryService;
import com.yupi.yuoj.service.UserService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 抗炎日记接口
 */
@RestController
@RequestMapping("/diary")
public class DiaryController {

    @Resource
    private DiaryService diaryService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addDiary(@RequestBody Diary diary, HttpServletRequest request) {
        if (diary == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        diary.setId(null);
        diary.setUserid(loginUser.getId());
        if (diary.getLikes() == null) {
            diary.setLikes(0);
        }
        if (diary.getIsapproved() == null) {
            diary.setIsapproved(userService.isAdmin(loginUser) ? 1 : 0);
        }
        boolean result = diaryService.save(diary);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(diary.getId());
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteDiary(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Diary oldDiary = diaryService.getById(deleteRequest.getId());
        ThrowUtils.throwIf(oldDiary == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (!loginUser.getId().equals(oldDiary.getUserid()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(diaryService.removeById(deleteRequest.getId()));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateDiary(@RequestBody Diary diary) {
        if (diary == null || diary.getId() == null || diary.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Diary oldDiary = diaryService.getById(diary.getId());
        ThrowUtils.throwIf(oldDiary == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(diaryService.updateById(diary));
    }

    @GetMapping("/get")
    public BaseResponse<Diary> getDiaryById(@RequestParam long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Diary diary = diaryService.getById(id);
        ThrowUtils.throwIf(diary == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(diary);
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<Diary>> listDiaryByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) Long userId) {
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Diary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userId != null && userId > 0, "userId", userId);
        queryWrapper.eq("isApproved", 1);
        queryWrapper.orderByDesc("createTime");
        return ResultUtils.success(diaryService.page(new Page<>(current, pageSize), queryWrapper));
    }

    @PostMapping("/author/map")
    public BaseResponse<Map<Long, UserVO>> getDiaryAuthorMap(@RequestBody List<Long> diaryIds) {
        ThrowUtils.throwIf(diaryIds == null || diaryIds.isEmpty(), ErrorCode.PARAMS_ERROR);
        List<Diary> diaryList = diaryService.listByIds(diaryIds);
        Map<Long, Long> diaryUserIdMap = diaryList.stream().collect(Collectors.toMap(Diary::getId, Diary::getUserid,
                (existValue, newValue) -> existValue, LinkedHashMap::new));
        Set<Long> userIds = diaryUserIdMap.values().stream().filter(userId -> userId != null && userId > 0)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.getUserVO(userService.listByIds(userIds)).stream()
                .collect(Collectors.toMap(UserVO::getId, userVO -> userVO));
        Map<Long, UserVO> resultMap = new LinkedHashMap<>();
        diaryUserIdMap.forEach((diaryId, userId) -> resultMap.put(diaryId, userVOMap.get(userId)));
        return ResultUtils.success(resultMap);
    }

    @GetMapping("/user/list/page")
    public BaseResponse<Page<Diary>> listUserDiaryBySourceDiary(@RequestParam long diaryId,
            @RequestParam(defaultValue = "1") long current, @RequestParam(defaultValue = "10") long pageSize,
            HttpServletRequest request) {
        ThrowUtils.throwIf(diaryId <= 0 || pageSize > 50, ErrorCode.PARAMS_ERROR);
        Diary sourceDiary = diaryService.getById(diaryId);
        ThrowUtils.throwIf(sourceDiary == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUserPermitNull(request);
        boolean canViewAll = loginUser != null
                && (loginUser.getId().equals(sourceDiary.getUserid()) || userService.isAdmin(loginUser));
        QueryWrapper<Diary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", sourceDiary.getUserid());
        queryWrapper.eq(!canViewAll, "isApproved", 1);
        queryWrapper.orderByDesc("createTime");
        return ResultUtils.success(diaryService.page(new Page<>(current, pageSize), queryWrapper));
    }

    @GetMapping("/my/list/page")
    public BaseResponse<Page<Diary>> listMyDiaryByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize, HttpServletRequest request) {
        ThrowUtils.throwIf(pageSize > 50, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<Diary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.orderByDesc("createTime");
        return ResultUtils.success(diaryService.page(new Page<>(current, pageSize), queryWrapper));
    }

    @GetMapping("/pending/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Diary>> listPendingDiaryByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        ThrowUtils.throwIf(pageSize > 50, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Diary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isApproved", 0);
        queryWrapper.orderByAsc("createTime");
        return ResultUtils.success(diaryService.page(new Page<>(current, pageSize), queryWrapper));
    }

    @PostMapping("/approve")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> approveDiary(@RequestParam long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Diary diary = diaryService.getById(id);
        ThrowUtils.throwIf(diary == null, ErrorCode.NOT_FOUND_ERROR);
        diary.setIsapproved(1);
        return ResultUtils.success(diaryService.updateById(diary));
    }

    @PostMapping("/like")
    public BaseResponse<Boolean> likeDiary(@RequestParam long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Diary diary = diaryService.getById(id);
        ThrowUtils.throwIf(diary == null, ErrorCode.NOT_FOUND_ERROR);
        diary.setLikes((diary.getLikes() == null ? 0 : diary.getLikes()) + 1);
        return ResultUtils.success(diaryService.updateById(diary));
    }
}
