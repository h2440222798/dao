package com.yupi.yuoj.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.model.entity.UserBaziProfile;
import com.yupi.yuoj.service.BaziRelationStrategyService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Build structured relationship profile from existing natal chart.
 */
@Service
public class BaziRelationStrategyServiceImpl implements BaziRelationStrategyService {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Map<String, Object> buildRelationProfile(UserBaziProfile profile, String currentCity,
            String relationshipStatus, String careerStage, String focusQuestion) {
        if (profile == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请先生成八字档案");
        }
        Map<String, Object> natal = readJson(profile.getBaziJson());
        Map<String, Object> scores = castMap(natal.get("elementScores"));
        Map<String, Object> dayMaster = castMap(natal.get("dayMaster"));
        Map<String, Object> monthSeason = castMap(natal.get("monthSeason"));
        List<Map<String, Object>> pillars = castList(natal.get("pillars"));

        String dominant = dominantElement(scores);
        String cityText = StringUtils.defaultIfBlank(currentCity, "");
        String relationship = StringUtils.defaultIfBlank(relationshipStatus, "unknown");
        String career = StringUtils.defaultIfBlank(careerStage, "unknown");
        String focus = StringUtils.defaultIfBlank(focusQuestion, "");

        Map<String, Object> relation = new LinkedHashMap<>();
        relation.put("city", cityText);
        relation.put("relationshipStatus", relationship);
        relation.put("careerStage", career);
        relation.put("focusQuestion", focus);
        relation.put("natalSummary", buildNatalSummary(profile, dayMaster, monthSeason, scores, pillars));
        relation.put("personalityAxes", buildPersonalityAxes(scores, dayMaster, monthSeason));
        relation.put("relationshipPattern", buildRelationshipPattern(scores, relationship));
        relation.put("marriagePattern", buildMarriagePattern(scores, relationship));
        relation.put("careerPattern", buildCareerPattern(scores, career));
        relation.put("wealthPattern", buildWealthPattern(scores, career));
        relation.put("luckyCities", buildLuckyCities(dominant, cityText));
        relation.put("luckyDirections", buildLuckyDirections(dominant));
        relation.put("fortunateElements", buildFortunateElements(scores));
        relation.put("supportivePeople", buildSupportivePeople(scores));
        relation.put("pitfalls", buildPitfalls(scores));
        relation.put("relationshipScore", calcScore(scores, 0.55, 0.2, 0.25));
        relation.put("marriageScore", calcScore(scores, 0.5, 0.15, 0.35));
        relation.put("careerScore", calcScore(scores, 0.3, 0.45, 0.25));
        relation.put("wealthScore", calcScore(scores, 0.2, 0.5, 0.3));
        relation.put("dominantElement", dominant);
        return relation;
    }

    private Map<String, Object> buildNatalSummary(UserBaziProfile profile, Map<String, Object> dayMaster,
            Map<String, Object> monthSeason, Map<String, Object> scores, List<Map<String, Object>> pillars) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("dayMaster", profile.getDayMaster());
        summary.put("dayMasterElement", profile.getDayMasterElement());
        summary.put("monthSeason", monthSeason.get("elementLabel"));
        summary.put("monthTerm", monthSeason.get("solarTerm"));
        summary.put("balance", scores);
        summary.put("pillars", pillars);
        summary.put("organFocus", dayMaster.get("organFocus"));
        return summary;
    }

    private Map<String, Object> buildPersonalityAxes(Map<String, Object> scores, Map<String, Object> dayMaster,
            Map<String, Object> monthSeason) {
        Map<String, Object> axes = new LinkedHashMap<>();
        axes.put("expressive", number(scores, "fire"));
        axes.put("stability", number(scores, "earth"));
        axes.put("logic", number(scores, "metal"));
        axes.put("intuition", number(scores, "water"));
        axes.put("initiative", number(scores, "wood"));
        axes.put("seasonMood", monthSeason.get("elementLabel"));
        axes.put("dayMasterColor", dayMaster.get("color"));
        return axes;
    }

    private Map<String, Object> buildRelationshipPattern(Map<String, Object> scores, String relationshipStatus) {
        Map<String, Object> pattern = new LinkedHashMap<>();
        int wood = number(scores, "wood");
        int fire = number(scores, "fire");
        int earth = number(scores, "earth");
        int metal = number(scores, "metal");
        int water = number(scores, "water");
        pattern.put("relationshipStyle", wood >= fire ? "慢热型" : "主动型");
        pattern.put("communicationStyle", metal >= wood ? "讲原则" : "讲感受");
        pattern.put("emotionNeed", water >= earth ? "需要安全感和理解" : "需要稳定回应");
        pattern.put("conflictRisk", Math.max(20, 100 - earth - metal / 2));
        pattern.put("statusHint", relationshipStatus);
        return pattern;
    }

    private Map<String, Object> buildMarriagePattern(Map<String, Object> scores, String relationshipStatus) {
        Map<String, Object> pattern = new LinkedHashMap<>();
        int earth = number(scores, "earth");
        int water = number(scores, "water");
        int metal = number(scores, "metal");
        pattern.put("marriageTiming", earth + water >= 45 ? "更适合稳定后成婚" : "更适合在事业稍稳后推进");
        pattern.put("partnerNeed", metal >= water ? "讲规则、责任感强的伴侣" : "温和包容型伴侣");
        pattern.put("stabilityIndex", clamp(earth + metal / 2));
        pattern.put("statusHint", relationshipStatus);
        return pattern;
    }

    private Map<String, Object> buildCareerPattern(Map<String, Object> scores, String careerStage) {
        Map<String, Object> pattern = new LinkedHashMap<>();
        int wood = number(scores, "wood");
        int fire = number(scores, "fire");
        int earth = number(scores, "earth");
        int metal = number(scores, "metal");
        pattern.put("careerStyle", wood >= metal ? "适合开拓型成长" : "适合规则型发展");
        pattern.put("leadershipPotential", clamp(fire + earth));
        pattern.put("execution", clamp(metal + earth));
        pattern.put("stageHint", careerStage);
        return pattern;
    }

    private Map<String, Object> buildWealthPattern(Map<String, Object> scores, String careerStage) {
        Map<String, Object> pattern = new LinkedHashMap<>();
        int earth = number(scores, "earth");
        int metal = number(scores, "metal");
        int water = number(scores, "water");
        pattern.put("wealthStyle", earth >= 20 ? "稳财" : "流动财");
        pattern.put("wealthMethod", metal >= water ? "靠专业、规则和系统赚钱" : "靠信息差、流动性和资源整合赚钱");
        pattern.put("riskControl", clamp(earth + metal));
        pattern.put("stageHint", careerStage);
        return pattern;
    }

    private List<Map<String, Object>> buildLuckyCities(String dominant, String currentCity) {
        List<Map<String, Object>> cities = new ArrayList<>();
        String[] templates;
        switch (dominant) {
            case "wood":
                templates = new String[]{"杭州", "广州", "南宁", "福州", "昆明"};
                break;
            case "fire":
                templates = new String[]{"深圳", "成都", "重庆", "长沙", "武汉"};
                break;
            case "earth":
                templates = new String[]{"西安", "郑州", "合肥", "济南", "南昌"};
                break;
            case "metal":
                templates = new String[]{"上海", "苏州", "天津", "青岛", "大连"};
                break;
            default:
                templates = new String[]{"哈尔滨", "青岛", "厦门", "海口", "宁波"};
        }
        for (int i = 0; i < templates.length; i++) {
            Map<String, Object> city = new LinkedHashMap<>();
            city.put("name", templates[i]);
            city.put("score", 96 - i * 4);
            city.put("reason", "契合" + dominant + "气与流动格局");
            city.put("compareToCurrent", StringUtils.isBlank(currentCity) ? "" : currentCity + " -> " + templates[i]);
            cities.add(city);
        }
        return cities;
    }

    private List<String> buildLuckyDirections(String dominant) {
        List<String> directions = new ArrayList<>();
        switch (dominant) {
            case "wood":
                directions.add("东");
                directions.add("东南");
                break;
            case "fire":
                directions.add("南");
                directions.add("东南");
                break;
            case "earth":
                directions.add("中");
                directions.add("西南");
                break;
            case "metal":
                directions.add("西");
                directions.add("西北");
                break;
            default:
                directions.add("北");
                directions.add("西北");
        }
        return directions;
    }

    private List<String> buildFortunateElements(Map<String, Object> scores) {
        List<String> elements = new ArrayList<>();
        elements.add(topElement(scores));
        elements.add("平衡元素：土");
        elements.add("辅助元素：" + secondElement(scores));
        return elements;
    }

    private List<String> buildSupportivePeople(Map<String, Object> scores) {
        List<String> people = new ArrayList<>();
        if (number(scores, "wood") >= number(scores, "metal")) {
            people.add("主动型、能带资源的人");
        } else {
            people.add("守规则、讲流程的人");
        }
        if (number(scores, "water") >= number(scores, "fire")) {
            people.add("情绪稳定、善沟通的人");
        } else {
            people.add("执行力强、说到做到的人");
        }
        return people;
    }

    private List<String> buildPitfalls(Map<String, Object> scores) {
        List<String> pitfalls = new ArrayList<>();
        if (number(scores, "wood") > 30) {
            pitfalls.add("情绪和决策过快，容易急");
        }
        if (number(scores, "fire") > 25) {
            pitfalls.add("容易上火、急躁、关系推进过猛");
        }
        if (number(scores, "metal") > 25) {
            pitfalls.add("标准高，容易挑剔与压抑表达");
        }
        if (pitfalls.isEmpty()) {
            pitfalls.add("整体较平衡，重点是保持节律");
        }
        return pitfalls;
    }

    private int calcScore(Map<String, Object> scores, double woodWeight, double fireWeight, double earthWeight) {
        int wood = number(scores, "wood");
        int fire = number(scores, "fire");
        int earth = number(scores, "earth");
        int metal = number(scores, "metal");
        int water = number(scores, "water");
        int score = (int) Math.round(wood * woodWeight + fire * fireWeight + earth * earthWeight
                + metal * 0.1 + water * 0.1);
        return clamp(score);
    }

    private String dominantElement(Map<String, Object> scores) {
        String dominant = "earth";
        int max = Integer.MIN_VALUE;
        for (String key : new String[]{"wood", "fire", "earth", "metal", "water"}) {
            int value = number(scores, key);
            if (value > max) {
                max = value;
                dominant = key;
            }
        }
        return dominant;
    }

    private String topElement(Map<String, Object> scores) {
        return dominantElement(scores);
    }

    private String secondElement(Map<String, Object> scores) {
        String first = dominantElement(scores);
        String second = "earth";
        int max = Integer.MIN_VALUE;
        for (String key : new String[]{"wood", "fire", "earth", "metal", "water"}) {
            if (key.equals(first)) {
                continue;
            }
            int value = number(scores, key);
            if (value > max) {
                max = value;
                second = key;
            }
        }
        return second;
    }

    private int number(Map<String, Object> map, String key) {
        Object value = map == null ? null : map.get(key);
        if (value == null) {
            return 0;
        }
        try {
            return (int) Math.round(Double.parseDouble(String.valueOf(value)));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private Map<String, Object> readJson(String json) {
        if (StringUtils.isBlank(json)) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "八字档案解析失败");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return new LinkedHashMap<>();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> castList(Object value) {
        if (value instanceof List) {
            return (List<Map<String, Object>>) value;
        }
        return new ArrayList<>();
    }
}
