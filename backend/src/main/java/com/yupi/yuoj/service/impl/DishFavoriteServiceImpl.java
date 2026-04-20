package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yuoj.model.entity.DishFavorite;
import com.yupi.yuoj.service.DishFavoriteService;
import com.yupi.yuoj.mapper.DishFavoriteMapper;
import org.springframework.stereotype.Service;

/**
* @author 24402
* @description 针对表【dish_favorite(菜品收藏关联表)】的数据库操作Service实现
* @createDate 2026-04-14 15:02:32
*/
@Service
public class DishFavoriteServiceImpl extends ServiceImpl<DishFavoriteMapper, DishFavorite>
    implements DishFavoriteService{

}




