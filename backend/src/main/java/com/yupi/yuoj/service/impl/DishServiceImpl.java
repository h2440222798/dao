package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yuoj.model.entity.Dish;
import com.yupi.yuoj.service.DishService;
import com.yupi.yuoj.mapper.DishMapper;
import org.springframework.stereotype.Service;

/**
* @author 24402
* @description 针对表【dish(菜品表)】的数据库操作Service实现
* @createDate 2026-04-14 15:02:32
*/
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService{

}




