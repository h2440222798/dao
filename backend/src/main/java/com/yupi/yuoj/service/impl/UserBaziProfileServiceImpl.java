package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.mapper.UserBaziProfileMapper;
import com.yupi.yuoj.model.dto.bazi.BaziAnalyzeRequest;
import com.yupi.yuoj.model.entity.UserBaziProfile;
import com.yupi.yuoj.model.vo.BaziProfileVO;
import com.yupi.yuoj.service.BaziCalculatorService;
import com.yupi.yuoj.service.DeepSeekBaziService;
import com.yupi.yuoj.service.UserBaziProfileService;
import com.yupi.yuoj.service.UserService;
import java.time.LocalDate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Bazi profile service implementation.
 */
@Service
public class UserBaziProfileServiceImpl extends ServiceImpl<UserBaziProfileMapper, UserBaziProfile>
        implements UserBaziProfileService {

    @Resource
    private BaziCalculatorService baziCalculatorService;

    @Resource
    private DeepSeekBaziService deepSeekBaziService;

    @Resource
    private UserService userService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public BaziProfileVO getMyProfile(Long userId) {
        UserBaziProfile profile = getLatestProfile(userId);
        return toVO(profile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaziProfileVO analyzeAndSave(Long userId, BaziAnalyzeRequest request) {
        validateRequest(request);
        Map<String, Object> natalChart = baziCalculatorService.buildNatalChart(request);
        Map<String, Object> todayFlow = baziCalculatorService.buildTodayFlow(LocalDate.now(), natalChart);
        Map<String, Object> aiResult = deepSeekBaziService.analyze(request, natalChart, todayFlow);

        Map<String, Integer> scores = extractScores(aiResult, natalChart);
        UserBaziProfile profile = getLatestProfile(userId);
        if (profile == null) {
            profile = new UserBaziProfile();
            profile.setUserId(userId);
        }
        fillProfile(profile, request, natalChart, todayFlow, aiResult, scores);

        boolean success = profile.getId() == null ? save(profile) : updateById(profile);
        if (!success) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Failed to save Bazi profile");
        }
        userService.updateConstitution(userId, scores.get("wood"), scores.get("fire"), scores.get("earth"),
                scores.get("metal"), scores.get("water"));
        return toVO(profile);
    }

    private void validateRequest(BaziAnalyzeRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(request.getBirthDate()) || StringUtils.isBlank(request.getBirthTime())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "birthDate and birthTime are required");
        }
        if (StringUtils.length(request.getBodySignals()) > 1200) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "bodySignals is too long");
        }
    }

    private UserBaziProfile getLatestProfile(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserBaziProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.orderByDesc("updateTime").last("limit 1");
        return getOne(queryWrapper);
    }

    private void fillProfile(UserBaziProfile profile, BaziAnalyzeRequest request, Map<String, Object> natalChart,
            Map<String, Object> todayFlow, Map<String, Object> aiResult, Map<String, Integer> scores) {
        profile.setGender(StringUtils.defaultIfBlank(request.getGender(), "unknown"));
        profile.setBirthDate(request.getBirthDate().trim());
        profile.setBirthTime(request.getBirthTime().trim());
        profile.setBirthPlace(StringUtils.defaultIfBlank(request.getBirthPlace(), ""));
        profile.setUseTrueSolarTime(Boolean.TRUE.equals(request.getUseTrueSolarTime()) ? 1 : 0);
        profile.setCalendarType(StringUtils.defaultIfBlank(request.getCalendarType(), "solar"));
        profile.setYearPillar(String.valueOf(natalChart.get("yearPillar")));
        profile.setMonthPillar(String.valueOf(natalChart.get("monthPillar")));
        profile.setDayPillar(String.valueOf(natalChart.get("dayPillar")));
        profile.setHourPillar(String.valueOf(natalChart.get("hourPillar")));
        @SuppressWarnings("unchecked")
        Map<String, Object> dayMaster = (Map<String, Object>) natalChart.get("dayMaster");
        profile.setDayMaster(String.valueOf(dayMaster.get("stem")));
        profile.setDayMasterElement(String.valueOf(dayMaster.get("element")));
        profile.setBaziJson(writeJson(natalChart));
        profile.setTodayJson(writeJson(todayFlow));
        profile.setAiJson(writeJson(aiResult));
        profile.setAiAnalysis(StringUtils.defaultIfBlank(String.valueOf(aiResult.get("aiAnalysis")), ""));
        profile.setConstitutionSummary(StringUtils.defaultIfBlank(String.valueOf(aiResult.get("constitutionSummary")), ""));
        profile.setHealthAdvice(StringUtils.defaultIfBlank(String.valueOf(aiResult.get("healthAdvice")), ""));
        profile.setWood(scores.get("wood"));
        profile.setFire(scores.get("fire"));
        profile.setEarth(scores.get("earth"));
        profile.setMetal(scores.get("metal"));
        profile.setWater(scores.get("water"));
        profile.setAnalyzedAt(new Date());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> extractScores(Map<String, Object> aiResult, Map<String, Object> natalChart) {
        Object scoreObject = aiResult.get("scores");
        Map<String, Integer> scores = new LinkedHashMap<>();
        if (scoreObject instanceof Map) {
            Map<String, Object> raw = (Map<String, Object>) scoreObject;
            scores.put("wood", toInt(raw.get("wood")));
            scores.put("fire", toInt(raw.get("fire")));
            scores.put("earth", toInt(raw.get("earth")));
            scores.put("metal", toInt(raw.get("metal")));
            scores.put("water", toInt(raw.get("water")));
        }
        int total = scores.values().stream().mapToInt(Integer::intValue).sum();
        if (total != 100) {
            Object chartScores = natalChart.get("elementScores");
            if (chartScores instanceof Map) {
                Map<String, Object> raw = (Map<String, Object>) chartScores;
                scores.put("wood", toInt(raw.get("wood")));
                scores.put("fire", toInt(raw.get("fire")));
                scores.put("earth", toInt(raw.get("earth")));
                scores.put("metal", toInt(raw.get("metal")));
                scores.put("water", toInt(raw.get("water")));
            }
        }
        normalizeScores(scores);
        return scores;
    }

    private void normalizeScores(Map<String, Integer> scores) {
        String[] keys = {"wood", "fire", "earth", "metal", "water"};
        int total = 0;
        String maxKey = "wood";
        for (String key : keys) {
            int value = Math.max(0, Math.min(100, scores.getOrDefault(key, 20)));
            scores.put(key, value);
            total += value;
            if (value > scores.getOrDefault(maxKey, 0)) {
                maxKey = key;
            }
        }
        if (total == 0) {
            for (String key : keys) {
                scores.put(key, 20);
            }
            return;
        }
        if (total != 100) {
            scores.put(maxKey, Math.max(0, Math.min(100, scores.get(maxKey) + 100 - total)));
        }
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
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to serialize Bazi data");
        }
    }

    private Object readJson(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return objectMapper.readValue(value, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private BaziProfileVO toVO(UserBaziProfile profile) {
        if (profile == null) {
            return null;
        }
        BaziProfileVO vo = new BaziProfileVO();
        vo.setId(profile.getId());
        vo.setUserId(profile.getUserId());
        vo.setGender(profile.getGender());
        vo.setBirthDate(profile.getBirthDate());
        vo.setBirthTime(profile.getBirthTime());
        vo.setBirthPlace(profile.getBirthPlace());
        vo.setUseTrueSolarTime(profile.getUseTrueSolarTime() != null && profile.getUseTrueSolarTime() == 1);
        vo.setCalendarType(profile.getCalendarType());
        vo.setYearPillar(profile.getYearPillar());
        vo.setMonthPillar(profile.getMonthPillar());
        vo.setDayPillar(profile.getDayPillar());
        vo.setHourPillar(profile.getHourPillar());
        vo.setDayMaster(profile.getDayMaster());
        vo.setDayMasterElement(profile.getDayMasterElement());
        vo.setBazi(readJson(profile.getBaziJson()));
        vo.setToday(readJson(profile.getTodayJson()));
        vo.setAi(readJson(profile.getAiJson()));
        vo.setAiAnalysis(profile.getAiAnalysis());
        vo.setConstitutionSummary(profile.getConstitutionSummary());
        vo.setHealthAdvice(profile.getHealthAdvice());
        vo.setWood(profile.getWood());
        vo.setFire(profile.getFire());
        vo.setEarth(profile.getEarth());
        vo.setMetal(profile.getMetal());
        vo.setWater(profile.getWater());
        vo.setAnalyzedAt(formatDate(profile.getAnalyzedAt()));
        vo.setUpdateTime(formatDate(profile.getUpdateTime()));
        return vo;
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
