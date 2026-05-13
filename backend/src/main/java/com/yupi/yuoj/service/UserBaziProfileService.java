package com.yupi.yuoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yuoj.model.dto.bazi.BaziAnalyzeRequest;
import com.yupi.yuoj.model.entity.UserBaziProfile;
import com.yupi.yuoj.model.vo.BaziProfileVO;

/**
 * User Bazi profile service.
 */
public interface UserBaziProfileService extends IService<UserBaziProfile> {

    /**
     * Get the latest profile for a user.
     */
    BaziProfileVO getMyProfile(Long userId);

    /**
     * Analyze and save a user's Bazi profile.
     */
    BaziProfileVO analyzeAndSave(Long userId, BaziAnalyzeRequest request);
}
