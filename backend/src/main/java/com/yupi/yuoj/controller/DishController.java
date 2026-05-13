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
import com.yupi.yuoj.model.entity.Dish;
import com.yupi.yuoj.service.DishService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜品接口
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addDish(@RequestBody Dish dish) {
        if (dish == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        dish.setId(null);
        if (dish.getLikes() == null) {
            dish.setLikes(0);
        }
        if (dish.getFavorites() == null) {
            dish.setFavorites(0);
        }
        boolean result = dishService.save(dish);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(dish.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteDish(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(dishService.removeById(deleteRequest.getId()));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateDish(@RequestBody Dish dish) {
        if (dish == null || dish.getId() == null || dish.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Dish oldDish = dishService.getById(dish.getId());
        ThrowUtils.throwIf(oldDish == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(dishService.updateById(dish));
    }

    @GetMapping("/get")
    public BaseResponse<Dish> getDishById(@RequestParam long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Dish dish = dishService.getById(id);
        ThrowUtils.throwIf(dish == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(dish);
    }

    @RequestMapping(value = "/list/page", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseResponse<Page<Dish>> listDishByPage(@RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) String category) {
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0);
        queryWrapper.eq(category != null && !category.trim().isEmpty(), "category", category);
        queryWrapper.orderByDesc("createTime");
        return ResultUtils.success(dishService.page(new Page<>(current, pageSize), queryWrapper));
    }

    @PostMapping("/like")
    public BaseResponse<Boolean> likeDish(@RequestParam long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Dish dish = dishService.getById(id);
        ThrowUtils.throwIf(dish == null, ErrorCode.NOT_FOUND_ERROR);
        dish.setLikes((dish.getLikes() == null ? 0 : dish.getLikes()) + 1);
        return ResultUtils.success(dishService.updateById(dish));
    }
}
