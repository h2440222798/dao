package com.yupi.yuoj.esdao;

import com.yupi.yuoj.model.dto.post.PostEsDTO;
import java.util.List;

/**
 * 帖子 ES 操作（ES 未启用，此接口暂不使用）
 */
public interface PostEsDao {

    List<PostEsDTO> findByUserId(Long userId);
}