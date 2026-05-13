package com.yupi.yuoj.service.impl;

import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.model.dto.bazi.BaziAnalyzeRequest;
import com.yupi.yuoj.service.BaziCalculatorService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Bazi calculator based on the solar-term calendar.
 */
@Service
public class BaziCalculatorServiceImpl implements BaziCalculatorService {

    private static final String[] STEMS = {
            "\u7532", "\u4e59", "\u4e19", "\u4e01", "\u620a",
            "\u5df1", "\u5e9a", "\u8f9b", "\u58ec", "\u7678"
    };

    private static final String[] STEM_PINYIN = {
            "jia", "yi", "bing", "ding", "wu", "ji", "geng", "xin", "ren", "gui"
    };

    private static final String[] BRANCHES = {
            "\u5b50", "\u4e11", "\u5bc5", "\u536f", "\u8fb0", "\u5df3",
            "\u5348", "\u672a", "\u7533", "\u9149", "\u620c", "\u4ea5"
    };

    private static final String[] BRANCH_PINYIN = {
            "zi", "chou", "yin", "mao", "chen", "si", "wu", "wei", "shen", "you", "xu", "hai"
    };

    private static final String[] STEM_ELEMENTS = {
            "wood", "wood", "fire", "fire", "earth", "earth", "metal", "metal", "water", "water"
    };

    private static final String[] BRANCH_ELEMENTS = {
            "water", "earth", "wood", "wood", "earth", "fire",
            "fire", "earth", "metal", "metal", "earth", "water"
    };

    private static final String[] YIN_YANG = {
            "yang", "yin", "yang", "yin", "yang", "yin", "yang", "yin", "yang", "yin"
    };

    private static final String[][] HIDDEN_STEMS = {
            {"\u7678"},
            {"\u5df1", "\u7678", "\u8f9b"},
            {"\u7532", "\u4e19", "\u620a"},
            {"\u4e59"},
            {"\u620a", "\u4e59", "\u7678"},
            {"\u4e19", "\u620a", "\u5e9a"},
            {"\u4e01", "\u5df1"},
            {"\u5df1", "\u4e01", "\u4e59"},
            {"\u5e9a", "\u58ec", "\u620a"},
            {"\u8f9b"},
            {"\u620a", "\u8f9b", "\u4e01"},
            {"\u58ec", "\u7532"}
    };

    private static final String[] ELEMENT_ORDER = {"wood", "fire", "earth", "metal", "water"};

    private static final Map<String, String> ELEMENT_LABELS = new LinkedHashMap<>();

    private static final Map<String, String> ELEMENT_ORGANS = new LinkedHashMap<>();

    private static final Map<String, String> ELEMENT_COLORS = new LinkedHashMap<>();

    private static final int[][] CLASH_PAIRS = {
            {0, 6}, {1, 7}, {2, 8}, {3, 9}, {4, 10}, {5, 11}
    };

    static {
        ELEMENT_LABELS.put("wood", "\u6728");
        ELEMENT_LABELS.put("fire", "\u706b");
        ELEMENT_LABELS.put("earth", "\u571f");
        ELEMENT_LABELS.put("metal", "\u91d1");
        ELEMENT_LABELS.put("water", "\u6c34");

        ELEMENT_ORGANS.put("wood", "\u809d\u80c6");
        ELEMENT_ORGANS.put("fire", "\u5fc3\u5c0f\u80a0");
        ELEMENT_ORGANS.put("earth", "\u813e\u80c3");
        ELEMENT_ORGANS.put("metal", "\u80ba\u5927\u80a0");
        ELEMENT_ORGANS.put("water", "\u80be\u8180\u80f1");

        ELEMENT_COLORS.put("wood", "#43b36b");
        ELEMENT_COLORS.put("fire", "#ff5c4d");
        ELEMENT_COLORS.put("earth", "#c9903f");
        ELEMENT_COLORS.put("metal", "#d8dde7");
        ELEMENT_COLORS.put("water", "#3478f6");
    }

    @Override
    public Map<String, Object> buildNatalChart(BaziAnalyzeRequest request) {
        LocalDate birthDate = parseDate(request.getBirthDate());
        LocalTime birthTime = parseTime(request.getBirthTime());

        int pillarYear = resolvePillarYear(birthDate);
        int yearStemIndex = cycleIndex(pillarYear - 4, 10);
        int yearBranchIndex = cycleIndex(pillarYear - 4, 12);
        int yearCycleIndex = cycleIndex(pillarYear - 4, 60);

        MonthInfo monthInfo = resolveMonthInfo(birthDate, yearStemIndex);
        int dayCycleIndex = cycleIndex((int) (julianDayNumber(birthDate) + 49), 60);
        int dayStemIndex = dayCycleIndex % 10;
        int dayBranchIndex = dayCycleIndex % 12;

        int hourBranchIndex = ((birthTime.getHour() + 1) / 2) % 12;
        int hourStemStart = resolveHourStemStart(dayStemIndex);
        int hourStemIndex = cycleIndex(hourStemStart + hourBranchIndex, 10);
        int hourCycleIndex = cycleIndex(hourStemIndex - hourBranchIndex, 60);

        List<Map<String, Object>> pillars = new ArrayList<>();
        pillars.add(buildPillar("year", "\u5e74\u67f1", yearStemIndex, yearBranchIndex, yearCycleIndex, dayStemIndex));
        pillars.add(buildPillar("month", "\u6708\u67f1", monthInfo.stemIndex, monthInfo.branchIndex,
                cycleIndex(monthInfo.stemIndex - monthInfo.branchIndex, 60), dayStemIndex));
        pillars.add(buildPillar("day", "\u65e5\u67f1", dayStemIndex, dayBranchIndex, dayCycleIndex, dayStemIndex));
        pillars.add(buildPillar("hour", "\u65f6\u67f1", hourStemIndex, hourBranchIndex, hourCycleIndex, dayStemIndex));

        Map<String, Integer> scores = normalizeScores(calculateRawScores(pillars, monthInfo.branchIndex));

        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("birth", buildBirthMap(request, birthDate, birthTime));
        chart.put("pillarYear", pillarYear);
        chart.put("pillars", pillars);
        chart.put("yearPillar", pillarName(yearStemIndex, yearBranchIndex));
        chart.put("monthPillar", pillarName(monthInfo.stemIndex, monthInfo.branchIndex));
        chart.put("dayPillar", pillarName(dayStemIndex, dayBranchIndex));
        chart.put("hourPillar", pillarName(hourStemIndex, hourBranchIndex));
        chart.put("dayMaster", buildDayMaster(dayStemIndex));
        chart.put("monthSeason", buildMonthSeason(monthInfo));
        chart.put("elementScores", scores);
        chart.put("interactions", buildNatalInteractions(pillars));
        chart.put("calculationNote",
                "Solar calendar Bazi with solar-term month boundaries. True solar time is recorded but not geocoded without longitude.");
        return chart;
    }

    @Override
    public Map<String, Object> buildTodayFlow(LocalDate today, Map<String, Object> natalChart) {
        int pillarYear = resolvePillarYear(today);
        int yearStemIndex = cycleIndex(pillarYear - 4, 10);
        int yearBranchIndex = cycleIndex(pillarYear - 4, 12);
        MonthInfo monthInfo = resolveMonthInfo(today, yearStemIndex);
        int dayCycleIndex = cycleIndex((int) (julianDayNumber(today) + 49), 60);
        int dayStemIndex = dayCycleIndex % 10;
        int dayBranchIndex = dayCycleIndex % 12;
        int clashBranch = getOppositeBranch(dayBranchIndex);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> natalPillars = (List<Map<String, Object>>) natalChart.get("pillars");
        List<Map<String, Object>> clashes = new ArrayList<>();
        for (Map<String, Object> pillar : natalPillars) {
            int branchIndex = Integer.parseInt(String.valueOf(pillar.get("branchIndex")));
            if (branchIndex == clashBranch) {
                Map<String, Object> clash = new LinkedHashMap<>();
                clash.put("target", pillar.get("label"));
                clash.put("targetBranch", pillar.get("branch"));
                clash.put("todayBranch", BRANCHES[dayBranchIndex]);
                clash.put("description", BRANCHES[dayBranchIndex] + BRANCHES[clashBranch] + "\u76f8\u51b2");
                clashes.add(clash);
            }
        }

        Map<String, Object> todayMap = new LinkedHashMap<>();
        todayMap.put("date", today.toString());
        todayMap.put("yearPillar", pillarName(yearStemIndex, yearBranchIndex));
        todayMap.put("monthPillar", pillarName(monthInfo.stemIndex, monthInfo.branchIndex));
        todayMap.put("dayPillar", pillarName(dayStemIndex, dayBranchIndex));
        todayMap.put("dayStem", STEMS[dayStemIndex]);
        todayMap.put("dayBranch", BRANCHES[dayBranchIndex]);
        todayMap.put("dayElement", STEM_ELEMENTS[dayStemIndex]);
        todayMap.put("dayElementLabel", ELEMENT_LABELS.get(STEM_ELEMENTS[dayStemIndex]));
        todayMap.put("clashBranch", BRANCHES[clashBranch]);
        todayMap.put("clashAnimal", branchAnimal(clashBranch));
        todayMap.put("natalClashes", clashes);
        todayMap.put("dayOfficer", resolveDayOfficer(monthInfo.branchIndex, dayBranchIndex));
        todayMap.put("energyHint", buildTodayHint(clashes, dayStemIndex));
        return todayMap;
    }

    private Map<String, Object> buildBirthMap(BaziAnalyzeRequest request, LocalDate birthDate, LocalTime birthTime) {
        Map<String, Object> birth = new LinkedHashMap<>();
        birth.put("gender", StringUtils.defaultIfBlank(request.getGender(), "unknown"));
        birth.put("birthDate", birthDate.toString());
        birth.put("birthTime", birthTime.toString());
        birth.put("birthPlace", StringUtils.defaultIfBlank(request.getBirthPlace(), ""));
        birth.put("calendarType", StringUtils.defaultIfBlank(request.getCalendarType(), "solar"));
        birth.put("useTrueSolarTime", Boolean.TRUE.equals(request.getUseTrueSolarTime()));
        birth.put("bodySignals", StringUtils.defaultIfBlank(request.getBodySignals(), ""));
        return birth;
    }

    private Map<String, Object> buildPillar(String key, String label, int stemIndex, int branchIndex, int cycleIndex,
            int dayStemIndex) {
        Map<String, Object> pillar = new LinkedHashMap<>();
        String stem = STEMS[stemIndex];
        String branch = BRANCHES[branchIndex];
        pillar.put("key", key);
        pillar.put("label", label);
        pillar.put("pillar", stem + branch);
        pillar.put("stem", stem);
        pillar.put("stemPinyin", STEM_PINYIN[stemIndex]);
        pillar.put("stemIndex", stemIndex);
        pillar.put("stemElement", STEM_ELEMENTS[stemIndex]);
        pillar.put("stemElementLabel", ELEMENT_LABELS.get(STEM_ELEMENTS[stemIndex]));
        pillar.put("yinYang", YIN_YANG[stemIndex]);
        pillar.put("branch", branch);
        pillar.put("branchPinyin", BRANCH_PINYIN[branchIndex]);
        pillar.put("branchIndex", branchIndex);
        pillar.put("branchElement", BRANCH_ELEMENTS[branchIndex]);
        pillar.put("branchElementLabel", ELEMENT_LABELS.get(BRANCH_ELEMENTS[branchIndex]));
        pillar.put("hiddenStems", Arrays.asList(HIDDEN_STEMS[branchIndex]));
        pillar.put("tenGod", resolveTenGod(dayStemIndex, stemIndex));
        pillar.put("branchAnimal", branchAnimal(branchIndex));
        pillar.put("cycleIndex", cycleIndex);
        return pillar;
    }

    private Map<String, Object> buildDayMaster(int dayStemIndex) {
        Map<String, Object> dayMaster = new LinkedHashMap<>();
        String element = STEM_ELEMENTS[dayStemIndex];
        dayMaster.put("stem", STEMS[dayStemIndex]);
        dayMaster.put("pinyin", STEM_PINYIN[dayStemIndex]);
        dayMaster.put("element", element);
        dayMaster.put("elementLabel", ELEMENT_LABELS.get(element));
        dayMaster.put("yinYang", YIN_YANG[dayStemIndex]);
        dayMaster.put("organFocus", ELEMENT_ORGANS.get(element));
        dayMaster.put("color", ELEMENT_COLORS.get(element));
        return dayMaster;
    }

    private Map<String, Object> buildMonthSeason(MonthInfo monthInfo) {
        Map<String, Object> season = new LinkedHashMap<>();
        String element = BRANCH_ELEMENTS[monthInfo.branchIndex];
        season.put("solarTerm", monthInfo.termName);
        season.put("monthOrdinal", monthInfo.ordinalFromYin);
        season.put("branch", BRANCHES[monthInfo.branchIndex]);
        season.put("element", element);
        season.put("elementLabel", ELEMENT_LABELS.get(element));
        season.put("organFocus", ELEMENT_ORGANS.get(element));
        return season;
    }

    private Map<String, Integer> calculateRawScores(List<Map<String, Object>> pillars, int monthBranchIndex) {
        Map<String, Integer> raw = new LinkedHashMap<>();
        for (String element : ELEMENT_ORDER) {
            raw.put(element, 0);
        }
        for (Map<String, Object> pillar : pillars) {
            addScore(raw, String.valueOf(pillar.get("stemElement")), 12);
            addScore(raw, String.valueOf(pillar.get("branchElement")), 10);
            @SuppressWarnings("unchecked")
            List<String> hiddenStems = (List<String>) pillar.get("hiddenStems");
            int hiddenWeight = 5;
            for (String hiddenStem : hiddenStems) {
                int stemIndex = indexOf(STEMS, hiddenStem);
                if (stemIndex >= 0) {
                    addScore(raw, STEM_ELEMENTS[stemIndex], hiddenWeight);
                    hiddenWeight = Math.max(2, hiddenWeight - 2);
                }
            }
        }
        addScore(raw, BRANCH_ELEMENTS[monthBranchIndex], 8);
        return raw;
    }

    private Map<String, Integer> normalizeScores(Map<String, Integer> raw) {
        int total = raw.values().stream().mapToInt(Integer::intValue).sum();
        if (total <= 0) {
            Map<String, Integer> balanced = new LinkedHashMap<>();
            for (String element : ELEMENT_ORDER) {
                balanced.put(element, 20);
            }
            return balanced;
        }
        Map<String, Integer> result = new LinkedHashMap<>();
        int assigned = 0;
        String maxElement = ELEMENT_ORDER[0];
        for (String element : ELEMENT_ORDER) {
            int score = (int) Math.round(raw.get(element) * 100.0 / total);
            result.put(element, score);
            assigned += score;
            if (raw.get(element) > raw.get(maxElement)) {
                maxElement = element;
            }
        }
        result.put(maxElement, result.get(maxElement) + (100 - assigned));
        return result;
    }

    private Map<String, Object> buildNatalInteractions(List<Map<String, Object>> pillars) {
        List<Map<String, Object>> clashes = new ArrayList<>();
        for (int i = 0; i < pillars.size(); i++) {
            for (int j = i + 1; j < pillars.size(); j++) {
                int a = Integer.parseInt(String.valueOf(pillars.get(i).get("branchIndex")));
                int b = Integer.parseInt(String.valueOf(pillars.get(j).get("branchIndex")));
                if (isClash(a, b)) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("from", pillars.get(i).get("label"));
                    item.put("to", pillars.get(j).get("label"));
                    item.put("branches", BRANCHES[a] + BRANCHES[b]);
                    item.put("description", BRANCHES[a] + BRANCHES[b] + "\u76f8\u51b2");
                    clashes.add(item);
                }
            }
        }
        Map<String, Object> interactions = new LinkedHashMap<>();
        interactions.put("clashes", clashes);
        interactions.put("note", clashes.isEmpty() ? "\u539f\u5c40\u5730\u652f\u51b2\u52a8\u8f83\u5c11" : "\u539f\u5c40\u6709\u5730\u652f\u51b2\u52a8\uff0c\u5b9c\u770b\u5177\u4f53\u5bab\u4f4d");
        return interactions;
    }

    private String buildTodayHint(List<Map<String, Object>> clashes, int dayStemIndex) {
        String elementLabel = ELEMENT_LABELS.get(STEM_ELEMENTS[dayStemIndex]);
        if (clashes.isEmpty()) {
            return "\u4eca\u65e5" + elementLabel + "\u6c14\u5165\u5c40\uff0c\u4e0e\u672c\u547d\u51b2\u52a8\u8f83\u5c11\uff0c\u5b9c\u7a33\u5b9a\u4f5c\u606f\u3001\u987a\u52bf\u884c\u4e8b\u3002";
        }
        return "\u4eca\u65e5\u5730\u652f\u51b2\u5230\u672c\u547d" + clashes.size()
                + "\u5904\uff0c\u5b9c\u51cf\u5c11\u6025\u8e81\u51b3\u7b56\uff0c\u591a\u7559\u8eab\u4f53\u4f59\u91cf\u3002";
    }

    private String resolveTenGod(int dayStemIndex, int targetStemIndex) {
        String dayElement = STEM_ELEMENTS[dayStemIndex];
        String targetElement = STEM_ELEMENTS[targetStemIndex];
        boolean samePolarity = YIN_YANG[dayStemIndex].equals(YIN_YANG[targetStemIndex]);
        if (dayElement.equals(targetElement)) {
            return samePolarity ? "\u6bd4\u80a9" : "\u52ab\u8d22";
        }
        if (generates(dayElement, targetElement)) {
            return samePolarity ? "\u98df\u795e" : "\u4f24\u5b98";
        }
        if (controls(dayElement, targetElement)) {
            return samePolarity ? "\u504f\u8d22" : "\u6b63\u8d22";
        }
        if (controls(targetElement, dayElement)) {
            return samePolarity ? "\u4e03\u6740" : "\u6b63\u5b98";
        }
        if (generates(targetElement, dayElement)) {
            return samePolarity ? "\u504f\u5370" : "\u6b63\u5370";
        }
        return "";
    }

    private boolean generates(String from, String to) {
        return ("wood".equals(from) && "fire".equals(to))
                || ("fire".equals(from) && "earth".equals(to))
                || ("earth".equals(from) && "metal".equals(to))
                || ("metal".equals(from) && "water".equals(to))
                || ("water".equals(from) && "wood".equals(to));
    }

    private boolean controls(String from, String to) {
        return ("wood".equals(from) && "earth".equals(to))
                || ("earth".equals(from) && "water".equals(to))
                || ("water".equals(from) && "fire".equals(to))
                || ("fire".equals(from) && "metal".equals(to))
                || ("metal".equals(from) && "wood".equals(to));
    }

    private int resolvePillarYear(LocalDate date) {
        int lichunDay = solarTermDay(date.getYear(), "lichun");
        LocalDate lichun = LocalDate.of(date.getYear(), 2, lichunDay);
        return date.isBefore(lichun) ? date.getYear() - 1 : date.getYear();
    }

    private MonthInfo resolveMonthInfo(LocalDate date, int yearStemIndex) {
        int year = date.getYear();
        List<MonthBoundary> boundaries = Arrays.asList(
                new MonthBoundary(LocalDate.of(year, 1, solarTermDay(year, "xiaohan")), 12, "\u5c0f\u5bd2"),
                new MonthBoundary(LocalDate.of(year, 2, solarTermDay(year, "lichun")), 1, "\u7acb\u6625"),
                new MonthBoundary(LocalDate.of(year, 3, solarTermDay(year, "jingzhe")), 2, "\u60ca\u86f0"),
                new MonthBoundary(LocalDate.of(year, 4, solarTermDay(year, "qingming")), 3, "\u6e05\u660e"),
                new MonthBoundary(LocalDate.of(year, 5, solarTermDay(year, "lixia")), 4, "\u7acb\u590f"),
                new MonthBoundary(LocalDate.of(year, 6, solarTermDay(year, "mangzhong")), 5, "\u8292\u79cd"),
                new MonthBoundary(LocalDate.of(year, 7, solarTermDay(year, "xiaoshu")), 6, "\u5c0f\u6691"),
                new MonthBoundary(LocalDate.of(year, 8, solarTermDay(year, "liqiu")), 7, "\u7acb\u79cb"),
                new MonthBoundary(LocalDate.of(year, 9, solarTermDay(year, "bailu")), 8, "\u767d\u9732"),
                new MonthBoundary(LocalDate.of(year, 10, solarTermDay(year, "hanlu")), 9, "\u5bd2\u9732"),
                new MonthBoundary(LocalDate.of(year, 11, solarTermDay(year, "lidong")), 10, "\u7acb\u51ac"),
                new MonthBoundary(LocalDate.of(year, 12, solarTermDay(year, "daxue")), 11, "\u5927\u96ea")
        );
        MonthBoundary chosen = new MonthBoundary(LocalDate.of(year - 1, 12, solarTermDay(year - 1, "daxue")),
                11, "\u5927\u96ea");
        for (MonthBoundary boundary : boundaries) {
            if (!date.isBefore(boundary.date)) {
                chosen = boundary;
            }
        }
        int branchIndex = (chosen.ordinalFromYin + 1) % 12;
        int stemIndex = cycleIndex(resolveMonthStemStart(yearStemIndex) + chosen.ordinalFromYin - 1, 10);
        return new MonthInfo(chosen.ordinalFromYin, stemIndex, branchIndex, chosen.termName);
    }

    private int solarTermDay(int fullYear, String term) {
        int year = fullYear % 100;
        double c = solarTermConstant(fullYear, term);
        return (int) Math.floor(year * 0.2422 + c) - (int) Math.floor((year - 1) / 4.0);
    }

    private double solarTermConstant(int fullYear, String term) {
        boolean twentyFirst = fullYear >= 2000;
        Map<String, Double> constants = new LinkedHashMap<>();
        if (twentyFirst) {
            constants.put("xiaohan", 5.4055);
            constants.put("lichun", 3.87);
            constants.put("jingzhe", 5.63);
            constants.put("qingming", 4.81);
            constants.put("lixia", 5.52);
            constants.put("mangzhong", 5.678);
            constants.put("xiaoshu", 7.108);
            constants.put("liqiu", 7.5);
            constants.put("bailu", 7.646);
            constants.put("hanlu", 8.318);
            constants.put("lidong", 7.438);
            constants.put("daxue", 7.18);
        } else {
            constants.put("xiaohan", 6.11);
            constants.put("lichun", 4.6295);
            constants.put("jingzhe", 6.3826);
            constants.put("qingming", 5.59);
            constants.put("lixia", 6.318);
            constants.put("mangzhong", 6.5);
            constants.put("xiaoshu", 7.928);
            constants.put("liqiu", 8.35);
            constants.put("bailu", 8.44);
            constants.put("hanlu", 9.098);
            constants.put("lidong", 8.218);
            constants.put("daxue", 7.9);
        }
        return constants.getOrDefault(term, 4.0);
    }

    private int resolveMonthStemStart(int yearStemIndex) {
        switch (yearStemIndex) {
            case 0:
            case 5:
                return 2;
            case 1:
            case 6:
                return 4;
            case 2:
            case 7:
                return 6;
            case 3:
            case 8:
                return 8;
            default:
                return 0;
        }
    }

    private int resolveHourStemStart(int dayStemIndex) {
        switch (dayStemIndex) {
            case 0:
            case 5:
                return 0;
            case 1:
            case 6:
                return 2;
            case 2:
            case 7:
                return 4;
            case 3:
            case 8:
                return 6;
            default:
                return 8;
        }
    }

    private long julianDayNumber(LocalDate date) {
        int a = (14 - date.getMonthValue()) / 12;
        int y = date.getYear() + 4800 - a;
        int m = date.getMonthValue() + 12 * a - 3;
        return date.getDayOfMonth() + (153L * m + 2) / 5 + 365L * y + y / 4 - y / 100 + y / 400 - 32045;
    }

    private String pillarName(int stemIndex, int branchIndex) {
        return STEMS[stemIndex] + BRANCHES[branchIndex];
    }

    private int getOppositeBranch(int branchIndex) {
        for (int[] pair : CLASH_PAIRS) {
            if (pair[0] == branchIndex) {
                return pair[1];
            }
            if (pair[1] == branchIndex) {
                return pair[0];
            }
        }
        return cycleIndex(branchIndex + 6, 12);
    }

    private boolean isClash(int a, int b) {
        return getOppositeBranch(a) == b;
    }

    private String resolveDayOfficer(int monthBranchIndex, int dayBranchIndex) {
        String[] officers = {
                "\u5efa", "\u9664", "\u6ee1", "\u5e73", "\u5b9a", "\u6267",
                "\u7834", "\u5371", "\u6210", "\u6536", "\u5f00", "\u95ed"
        };
        return officers[cycleIndex(dayBranchIndex - monthBranchIndex, 12)];
    }

    private String branchAnimal(int branchIndex) {
        String[] animals = {
                "\u9f20", "\u725b", "\u864e", "\u5154", "\u9f99", "\u86c7",
                "\u9a6c", "\u7f8a", "\u7334", "\u9e21", "\u72d7", "\u732a"
        };
        return animals[branchIndex];
    }

    private void addScore(Map<String, Integer> scores, String element, int value) {
        scores.put(element, scores.getOrDefault(element, 0) + value);
    }

    private int indexOf(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    private int cycleIndex(int value, int size) {
        int result = value % size;
        return result < 0 ? result + size : result;
    }

    private LocalDate parseDate(String value) {
        if (StringUtils.isBlank(value)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "birthDate is required");
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "birthDate must be yyyy-MM-dd");
        }
    }

    private LocalTime parseTime(String value) {
        if (StringUtils.isBlank(value)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "birthTime is required");
        }
        try {
            return LocalTime.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "birthTime must be HH:mm");
        }
    }

    private static class MonthBoundary {
        private final LocalDate date;
        private final int ordinalFromYin;
        private final String termName;

        private MonthBoundary(LocalDate date, int ordinalFromYin, String termName) {
            this.date = date;
            this.ordinalFromYin = ordinalFromYin;
            this.termName = termName;
        }
    }

    private static class MonthInfo {
        private final int ordinalFromYin;
        private final int stemIndex;
        private final int branchIndex;
        private final String termName;

        private MonthInfo(int ordinalFromYin, int stemIndex, int branchIndex, String termName) {
            this.ordinalFromYin = ordinalFromYin;
            this.stemIndex = stemIndex;
            this.branchIndex = branchIndex;
            this.termName = termName;
        }
    }
}
