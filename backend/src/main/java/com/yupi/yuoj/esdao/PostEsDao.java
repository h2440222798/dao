package com.yupi.yuoj.esdao;

import com.yupi.yuoj.model.dto.post.PostEsDTO;
import java.util.Collections;
import java.util.List;

/**
 * 帖子 ES 操作（ES 已禁用，所有方法为空实现）
 */
public interface PostEsDao {

    default List<PostEsDTO> findByUserId(Long userId) {
        return Collections.emptyList();
    }

    default void saveAll(List<PostEsDTO> list) {
        // ES 已禁用，不执行任何操作
    }
}