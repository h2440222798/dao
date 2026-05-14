package com.yupi.yuoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yuoj.model.dto.divination.DivinationAnalyzeRequest;
import com.yupi.yuoj.model.entity.UserDivinationRecord;
import java.util.List;
import java.util.Map;

/**
 * User divination record service.
 */
public interface UserDivinationRecordService extends IService<UserDivinationRecord> {

    /**
     * Analyze a divination request and persist the result.
     */
    Map<String, Object> analyzeAndSave(Long userId, DivinationAnalyzeRequest request);

    /**
     * List latest records for current user.
     */
    List<Map<String, Object>> listMyRecords(Long userId, int limit);

    /**
     * Get one record detail for current user.
     */
    Map<String, Object> getMyRecordDetail(Long userId, Long recordId);
}
