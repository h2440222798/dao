package com.yupi.yuoj.model.vo;

import java.io.Serializable;
import lombok.Data;

/**
 * Bazi profile view object.
 */
@Data
public class BaziProfileVO implements Serializable {

    private Long id;

    private Long userId;

    private String gender;

    private String birthDate;

    private String birthTime;

    private String birthPlace;

    private Boolean useTrueSolarTime;

    private String calendarType;

    private String yearPillar;

    private String monthPillar;

    private String dayPillar;

    private String hourPillar;

    private String dayMaster;

    private String dayMasterElement;

    private Object bazi;

    private Object today;

    private Object ai;

    private String aiAnalysis;

    private String constitutionSummary;

    private String healthAdvice;

    private Integer wood;

    private Integer fire;

    private Integer earth;

    private Integer metal;

    private Integer water;

    private String analyzedAt;

    private String updateTime;

    private static final long serialVersionUID = 1L;
}
