package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.mapper.UserBaziRelationMapper;
import com.yupi.yuoj.model.dto.bazi.BaziRelationAnalyzeRequest;
import com.yupi.yuoj.model.entity.UserBaziProfile;
import com.yupi.yuoj.model.entity.UserBaziRelation;
import com.yupi.yuoj.model.vo.BaziRelationVO;
import com.yupi.yuoj.service.BaziRelationStrategyService;
import com.yupi.yuoj.service.DeepSeekBaziRelationService;
import com.yupi.yuoj.service.UserBaziProfileService;
import com.yupi.yuoj.service.UserBaziRelationService;
import com.yupi.yuoj.service.UserService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bazi relationship service implementation.
 */
@Service
public class UserBaziRelationServiceImpl extends ServiceImpl<UserBaziRelationMapper, UserBaziRelation>
        implements UserBaziRelationService {

    @Resource
    private UserBaziProfileService userBaziProfileService;

    @Resource
    private BaziRelationStrategyService baziRelationStrategyService;

    @Resource
    private DeepSeekBaziRelationService deepSeekBaziRelationService;

    @Resource
    private UserService userService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public BaziRelationVO getMyRelation(Long userId) {
        UserBaziRelation relation = getLatestRelation(userId);
        return toVO(relation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaziRelationVO analyzeAndSave(Long userId, BaziRelationAnalyzeRequest request) {
        validateRequest(request);
        UserBaziProfile profile = userBaziProfileService.getById(getLatestProfileId(userId));
        if (profile == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请先完成八字分析");
        }
        Map<String, Object> relationProfile = baziRelationStrategyService.buildRelationProfile(profile,
                request.getCurrentCity(), request.getRelationshipStatus(), request.getCareerStage(),
                request.getFocusQuestion());
        Map<String, Object> aiResult = deepSeekBaziRelationService.analyze(request, relationProfile);
        Map<String, Integer> scores = extractScores(aiResult, relationProfile);

        UserBaziRelation relation = getLatestRelation(userId);
        if (relation == null) {
            relation = new UserBaziRelation();
            relation.setUserId(userId);
        }
        fill(relation, profile.getId(), request, relationProfile, aiResult, scores);
        boolean success = relation.getId() == null ? save(relation) : updateById(relation);
        if (!success) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存关系画像失败");
        }
        return toVO(relation);
    }

    private void validateRequest(BaziRelationAnalyzeRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.length(request.getFocusQuestion()) > 1000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "focusQuestion 过长");
        }
    }

    private Long getLatestProfileId(Long userId) {
        UserBaziProfile profile = userBaziProfileService.getOne(new QueryWrapper<UserBaziProfile>()
                .eq("userId", userId)
                .orderByDesc("updateTime")
                .last("limit 1"));
        return profile == null ? null : profile.getId();
    }

    private UserBaziRelation getLatestRelation(Long userId) {
        QueryWrapper<UserBaziRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.orderByDesc("updateTime").last("limit 1");
        return getOne(queryWrapper);
    }

    private void fill(UserBaziRelation relation, Long profileId, BaziRelationAnalyzeRequest request,
            Map<String, Object> relationProfile, Map<String, Object> aiResult, Map<String, Integer> scores) {
        relation.setBaziProfileId(profileId);
        relation.setCurrentCity(StringUtils.defaultIfBlank(request.getCurrentCity(), ""));
        relation.setRelationshipStatus(StringUtils.defaultIfBlank(request.getRelationshipStatus(), "unknown"));
        relation.setCareerStage(StringUtils.defaultIfBlank(request.getCareerStage(), "unknown"));
        relation.setFocusQuestion(StringUtils.defaultIfBlank(request.getFocusQuestion(), ""));
        relation.setRelationJson(writeJson(relationProfile));
        relation.setAiSummary(StringUtils.defaultIfBlank(stringValue(aiResult.get("relationSummary")), ""));
        relation.setRelationshipAdvice(StringUtils.defaultIfBlank(stringValue(aiResult.get("relationshipAdvice")), ""));
        relation.setMarriageAdvice(StringUtils.defaultIfBlank(stringValue(aiResult.get("marriageAdvice")), ""));
        relation.setCareerAdvice(StringUtils.defaultIfBlank(stringValue(aiResult.get("careerAdvice")), ""));
        relation.setWealthAdvice(StringUtils.defaultIfBlank(stringValue(aiResult.get("wealthAdvice")), ""));
        relation.setLuckyCities(writeJson(aiResult.get("luckyCities")));
        relation.setLuckyDirections(writeJson(aiResult.get("luckyDirections")));
        relation.setFortunateElements(writeJson(aiResult.get("fortunateElements")));
        relation.setScoreJson(writeJson(scores));
        relation.setAnalyzedAt(new Date());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> extractScores(Map<String, Object> aiResult, Map<String, Object> relationProfile) {
        Map<String, Integer> scores = new LinkedHashMap<>();
        Object raw = aiResult.get("scores");
        if (raw instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) raw;
            scores.put("relationship", toInt(map.get("relationship")));
            scores.put("marriage", toInt(map.get("marriage")));
            scores.put("career", toInt(map.get("career")));
            scores.put("wealth", toInt(map.get("wealth")));
        }
        int total = scores.values().stream().mapToInt(Integer::intValue).sum();
        if (total != 100) {
            scores.put("relationship", numberFrom(relationProfile, "relationshipScore"));
            scores.put("marriage", numberFrom(relationProfile, "marriageScore"));
            scores.put("career", numberFrom(relationProfile, "careerScore"));
            scores.put("wealth", numberFrom(relationProfile, "wealthScore"));
            normalize(scores);
        }
        return scores;
    }

    private void normalize(Map<String, Integer> scores) {
        String[] keys = {"relationship", "marriage", "career", "wealth"};
        int total = 0;
        String maxKey = "relationship";
        for (String key : keys) {
            int value = Math.max(0, Math.min(100, scores.getOrDefault(key, 25)));
            scores.put(key, value);
            total += value;
            if (value > scores.getOrDefault(maxKey, 0)) {
                maxKey = key;
            }
        }
        if (total == 0) {
            for (String key : keys) {
                scores.put(key, 25);
            }
            return;
        }
        if (total != 100) {
            scores.put(maxKey, Math.max(0, Math.min(100, scores.get(maxKey) + 100 - total)));
        }
    }

    private int numberFrom(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return toInt(value);
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        try {
            return (int) Math.round(Double.parseDouble(String.valueOf(value)));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "关系数据序列化失败");
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

    private BaziRelationVO toVO(UserBaziRelation relation) {
        if (relation == null) {
            return null;
        }
        BaziRelationVO vo = new BaziRelationVO();
        vo.setId(relation.getId());
        vo.setUserId(relation.getUserId());
        vo.setBaziProfileId(relation.getBaziProfileId());
        vo.setCurrentCity(relation.getCurrentCity());
        vo.setRelationshipStatus(relation.getRelationshipStatus());
        vo.setCareerStage(relation.getCareerStage());
        vo.setFocusQuestion(relation.getFocusQuestion());
        vo.setRelation(readJson(relation.getRelationJson()));
        vo.setAi(mapAi(relation));
        vo.setAiSummary(relation.getAiSummary());
        vo.setRelationshipAdvice(relation.getRelationshipAdvice());
        vo.setMarriageAdvice(relation.getMarriageAdvice());
        vo.setCareerAdvice(relation.getCareerAdvice());
        vo.setWealthAdvice(relation.getWealthAdvice());
        vo.setLuckyCities(readJson(relation.getLuckyCities()));
        vo.setLuckyDirections(readJson(relation.getLuckyDirections()));
        vo.setFortunateElements(readJson(relation.getFortunateElements()));
        vo.setScores(readJson(relation.getScoreJson()));
        vo.setAnalyzedAt(formatDate(relation.getAnalyzedAt()));
        vo.setUpdateTime(formatDate(relation.getUpdateTime()));
        return vo;
    }

    private Object mapAi(UserBaziRelation relation) {
        Map<String, Object> ai = new LinkedHashMap<>();
        ai.put("summary", relation.getAiSummary());
        ai.put("relationshipAdvice", relation.getRelationshipAdvice());
        ai.put("marriageAdvice", relation.getMarriageAdvice());
        ai.put("careerAdvice", relation.getCareerAdvice());
        ai.put("wealthAdvice", relation.getWealthAdvice());
        return ai;
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
