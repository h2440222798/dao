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
 * Bazi relationship profile.
 */
@TableName(value = "user_bazi_relation")
@Data
public class UserBaziRelation implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long baziProfileId;

    private String currentCity;

    private String relationshipStatus;

    private String careerStage;

    private String focusQuestion;

    private String relationJson;

    private String aiSummary;

    private String relationshipAdvice;

    private String marriageAdvice;

    private String careerAdvice;

    private String wealthAdvice;

    private String luckyCities;

    private String luckyDirections;

    private String fortunateElements;

    private String scoreJson;

    private Date analyzedAt;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
