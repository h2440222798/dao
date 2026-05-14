package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.mapper.UserDivinationRecordMapper;
import com.yupi.yuoj.model.dto.divination.DivinationAnalyzeRequest;
import com.yupi.yuoj.model.entity.UserDivinationRecord;
import com.yupi.yuoj.service.DeepSeekDivinationService;
import com.yupi.yuoj.service.UserDivinationRecordService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * User divination record service implementation.
 */
@Service
public class UserDivinationRecordServiceImpl
        extends ServiceImpl<UserDivinationRecordMapper, UserDivinationRecord>
        implements UserDivinationRecordService {

    @Resource
    private DeepSeekDivinationService deepSeekDivinationService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Map<String, Object> analyzeAndSave(Long userId, DivinationAnalyzeRequest request) {
        validate(userId, request);
        Map<String, Object> aiResult = deepSeekDivinationService.analyze(request);
        UserDivinationRecord record = buildRecord(userId, request, aiResult);
        saveRecord(record);
        Map<String, Object> response = new LinkedHashMap<>(aiResult);
        response.put("recordId", record.getId());
        response.put("savedAt", formatDate(record.getAnalyzedAt()));
        return response;
    }

    @Override
    public List<Map<String, Object>> listMyRecords(Long userId, int limit) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int safeLimit = Math.max(1, Math.min(limit, 30));
        List<UserDivinationRecord> records = list(new QueryWrapper<UserDivinationRecord>()
                .eq("userId", userId)
                .orderByDesc("updateTime")
                .last("limit " + safeLimit));
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserDivinationRecord record : records) {
            result.add(toSummary(record));
        }
        return result;
    }

    private void validate(Long userId, DivinationAnalyzeRequest request) {
        if (userId == null || userId <= 0 || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.length(request.getQuestion()) > 800) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "问事内容过长");
        }
        if (StringUtils.length(request.getExtraInfo()) > 800) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "补充背景过长");
        }
        if (request.getLines() == null || request.getLines().size() != 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "六爻数据不完整");
        }
    }

    public void saveRecord(UserDivinationRecord record) {
        boolean success = save(record);
        if (!success) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存求签记录失败");
        }
    }

    @SuppressWarnings("unchecked")
    private UserDivinationRecord buildRecord(Long userId, DivinationAnalyzeRequest request,
            Map<String, Object> aiResult) {
        UserDivinationRecord record = new UserDivinationRecord();
        record.setUserId(userId);
        record.setQuestion(StringUtils.defaultIfBlank(request.getQuestion(), ""));
        record.setDateText(StringUtils.defaultIfBlank(request.getDate(), ""));
        record.setSignNo(request.getSignNo());
        record.setSignTitle(StringUtils.defaultIfBlank(request.getSignTitle(), ""));
        record.setSignLevel(StringUtils.defaultIfBlank(request.getSignLevel(), ""));
        record.setHexagramName(StringUtils.defaultIfBlank(request.getHexagramName(), ""));
        record.setChangedHexagramName(StringUtils.defaultIfBlank(request.getChangedHexagramName(), ""));
        record.setLowerTrigram(StringUtils.defaultIfBlank(request.getLowerTrigram(), ""));
        record.setUpperTrigram(StringUtils.defaultIfBlank(request.getUpperTrigram(), ""));
        record.setChangedLowerTrigram(StringUtils.defaultIfBlank(request.getChangedLowerTrigram(), ""));
        record.setChangedUpperTrigram(StringUtils.defaultIfBlank(request.getChangedUpperTrigram(), ""));
        record.setLinesJson(writeJson(request.getLines()));
        record.setMovingLinesJson(writeJson(request.getMovingLines()));
        record.setExtraInfo(StringUtils.defaultIfBlank(request.getExtraInfo(), ""));
        record.setRequestJson(writeJson(request));
        record.setAiJson(writeJson(aiResult));
        record.setSignText(stringValue(aiResult.get("signText")));
        record.setOverall(stringValue(aiResult.get("overall")));
        Object categoriesObject = aiResult.get("categories");
        if (categoriesObject instanceof Map) {
            Map<String, Object> categories = (Map<String, Object>) categoriesObject;
            record.setHealthAnalysis(writeJson(categories.get("health")));
            record.setRelationshipAnalysis(writeJson(categories.get("relationship")));
            record.setCareerAnalysis(writeJson(categories.get("career")));
            record.setWealthAnalysis(writeJson(categories.get("wealth")));
            record.setLuckAnalysis(writeJson(categories.get("luck")));
        }
        record.setLiuyaoAnalysis(writeJson(aiResult.get("liuyao")));
        record.setComprehensiveAnalysis(writeJson(aiResult.get("comprehensive")));
        record.setActionPlanJson(writeJson(aiResult.get("actionPlan")));
        record.setRiskNotice(stringValue(aiResult.get("riskNotice")));
        record.setAnalyzedAt(new Date());
        return record;
    }

    private Map<String, Object> toSummary(UserDivinationRecord record) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("id", record.getId());
        summary.put("question", record.getQuestion());
        summary.put("date", record.getDateText());
        summary.put("signNo", record.getSignNo());
        summary.put("signTitle", record.getSignTitle());
        summary.put("signLevel", record.getSignLevel());
        summary.put("hexagramName", record.getHexagramName());
        summary.put("changedHexagramName", record.getChangedHexagramName());
        summary.put("overall", record.getOverall());
        summary.put("signText", record.getSignText());
        summary.put("riskNotice", record.getRiskNotice());
        summary.put("categories", readCategoryMap(record));
        summary.put("liuyao", readJson(record.getLiuyaoAnalysis()));
        summary.put("comprehensive", readJson(record.getComprehensiveAnalysis()));
        summary.put("actionPlan", readJson(record.getActionPlanJson()));
        summary.put("savedAt", formatDate(record.getAnalyzedAt()));
        return summary;
    }

    private Map<String, Object> readCategoryMap(UserDivinationRecord record) {
        Map<String, Object> categories = new LinkedHashMap<>();
        categories.put("health", readJson(record.getHealthAnalysis()));
        categories.put("relationship", readJson(record.getRelationshipAnalysis()));
        categories.put("career", readJson(record.getCareerAnalysis()));
        categories.put("wealth", readJson(record.getWealthAnalysis()));
        categories.put("luck", readJson(record.getLuckAnalysis()));
        return categories;
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "求签数据序列化失败");
        }
    }

    private Object readJson(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return objectMapper.readValue(value, new TypeReference<Object>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
