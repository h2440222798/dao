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
import com.yupi.yuoj.model.entity.DishFavorite;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.service.DishFavoriteService;
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
 * 菜品收藏接口
 */
@RestController
@RequestMapping("/dish_favorite")
public class DishFavoriteController {

    @Resource
    private DishFavoriteService dishFavoriteService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addDishFavorite(@RequestBody DishFavorite dishFavorite, HttpServletRequest request) {
        if (dishFavorite == null || dishFavorite.getDishid() == null || dishFavorite.getDishid() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<DishFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dishId", dishFavorite.getDishid());
        queryWrapper.eq("userId", loginUser.getId());
        ThrowUtils.throwIf(dishFavoriteService.count(queryWrapper) > 0, ErrorCode.OPERATION_ERROR, "已收藏该菜品");
        dishFavorite.setId(null);
        dishFavorite.setUserid(loginUser.getId());
        boolean result = dishFavoriteService.save(dishFavorite);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(dishFavorite.getId());
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteDishFavorite(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DishFavorite oldFavorite = dishFavoriteService.getById(deleteRequest.getId());
        ThrowUtils.throwIf(oldFavorite == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (!loginUser.getId().equals(oldFavorite.getUserid()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(dishFavoriteService.removeById(deleteRequest.getId()));
    }

    @GetMapping("/my/list/page")
    public BaseResponse<Page<DishFavorite>> listMyFavoriteByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize, HttpServletRequest request) {
        ThrowUtils.throwIf(pageSize > 50, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<DishFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.orderByDesc("createTime");
        return ResultUtils.success(dishFavoriteService.page(new Page<>(current, pageSize), queryWrapper));
    }

    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<DishFavorite>> listAllFavoriteByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) Long dishId,
            @RequestParam(required = false) Long userId) {
        ThrowUtils.throwIf(pageSize > 50, ErrorCode.PARAMS_ERROR);
        QueryWrapper<DishFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(dishId != null && dishId > 0, "dishId", dishId);
        queryWrapper.eq(userId != null && userId > 0, "userId", userId);
        queryWrapper.orderByDesc("createTime");
        return ResultUtils.success(dishFavoriteService.page(new Page<>(current, pageSize), queryWrapper));
    }
}

