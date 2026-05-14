package com.yupi.yuoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * User divination record.
 */
@TableName(value = "user_divination_record")
@Data
public class UserDivinationRecord implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String question;

    private String dateText;

    private Integer signNo;

    private String signTitle;

    private String signLevel;

    private String hexagramName;

    private String changedHexagramName;

    private String lowerTrigram;

    private String upperTrigram;

    private String changedLowerTrigram;

    private String changedUpperTrigram;

    private String linesJson;

    private String movingLinesJson;

    private String extraInfo;

    private String requestJson;

    private String aiJson;

    private String signText;

    private String overall;

    private String healthAnalysis;

    private String relationshipAnalysis;

    private String careerAnalysis;

    private String wealthAnalysis;

    private String luckAnalysis;

    private String liuyaoAnalysis;

    private String comprehensiveAnalysis;

    private String actionPlanJson;

    private String riskNotice;

    private Date analyzedAt;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
