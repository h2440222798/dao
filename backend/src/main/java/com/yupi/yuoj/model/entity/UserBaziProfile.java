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
 * User Bazi analysis profile.
 */
@TableName(value = "user_bazi_profile")
@Data
public class UserBaziProfile implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String gender;

    private String birthDate;

    private String birthTime;

    private String birthPlace;

    private Integer useTrueSolarTime;

    private String calendarType;

    private String yearPillar;

    private String monthPillar;

    private String dayPillar;

    private String hourPillar;

    private String dayMaster;

    private String dayMasterElement;

    private String baziJson;

    private String todayJson;

    private String aiJson;

    private String aiAnalysis;

    private String constitutionSummary;

    private String healthAdvice;

    private Integer wood;

    private Integer fire;

    private Integer earth;

    private Integer metal;

    private Integer water;

    private Date analyzedAt;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
