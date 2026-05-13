package com.yupi.yuoj.model.vo;

import java.io.Serializable;
import lombok.Data;

/**
 * Bazi relation analysis view object.
 */
@Data
public class BaziRelationVO implements Serializable {

    private Long id;

    private Long userId;

    private Long baziProfileId;

    private String currentCity;

    private String relationshipStatus;

    private String careerStage;

    private String focusQuestion;

    private Object relation;

    private Object ai;

    private String aiSummary;

    private String relationshipAdvice;

    private String marriageAdvice;

    private String careerAdvice;

    private String wealthAdvice;

    private Object luckyCities;

    private Object luckyDirections;

    private Object fortunateElements;

    private Object scores;

    private String analyzedAt;

    private String updateTime;

    private static final long serialVersionUID = 1L;
}
