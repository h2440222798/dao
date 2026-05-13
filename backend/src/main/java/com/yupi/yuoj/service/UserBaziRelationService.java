package com.yupi.yuoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yuoj.model.dto.bazi.BaziRelationAnalyzeRequest;
import com.yupi.yuoj.model.entity.UserBaziRelation;
import com.yupi.yuoj.model.vo.BaziRelationVO;

/**
 * Bazi relationship service.
 */
public interface UserBaziRelationService extends IService<UserBaziRelation> {

    BaziRelationVO getMyRelation(Long userId);

    BaziRelationVO analyzeAndSave(Long userId, BaziRelationAnalyzeRequest request);
}
