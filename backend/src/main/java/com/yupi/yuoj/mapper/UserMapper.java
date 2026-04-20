package com.yupi.yuoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.yuoj.model.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * 用户数据库操作
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 更新用户五行体质分数
     */
    int updateConstitutionByUserId(@Param("userId") Long userId, @Param("wood") Integer wood, @Param("fire") Integer fire,
            @Param("earth") Integer earth, @Param("metal") Integer metal, @Param("water") Integer water);
}




