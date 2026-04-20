package com.yupi.yuoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 五行专题表
 * @TableName wuxing_topic
 */
@TableName(value ="wuxing_topic")
@Data
public class WuxingTopic {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * wood/fire/earth/metal/water
     */
    private String topickey;

    /**
     * 五行名称
     */
    private String name;

    /**
     * 对应脏腑
     */
    private String organ;

    /**
     * 对应季节
     */
    private String season;

    /**
     * 展示色值
     */
    private String color;

    /**
     * 简介
     */
    private String intro;

    /**
     * 饮食重点
     */
    private Object dietfocus;

    /**
     * 饮食方案
     */
    private Object dietrecipes;

    /**
     * 情绪重点
     */
    private Object emotionfocus;

    /**
     * 情绪调理步骤
     */
    private Object emotionsteps;

    /**
     * 身体重点
     */
    private Object bodyfocus;

    /**
     * 对应功法
     */
    private Object bodyexercise;

    /**
     * 修行重点
     */
    private Object practicefocus;

    /**
     * 季节提示
     */
    private Object seasonaltips;

    /**
     * 日常建议
     */
    private Object dailyadvice;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 更新时间
     */
    private Date updatetime;

    /**
     * 是否删除
     */
    private Integer isdelete;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        WuxingTopic other = (WuxingTopic) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTopickey() == null ? other.getTopickey() == null : this.getTopickey().equals(other.getTopickey()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getOrgan() == null ? other.getOrgan() == null : this.getOrgan().equals(other.getOrgan()))
            && (this.getSeason() == null ? other.getSeason() == null : this.getSeason().equals(other.getSeason()))
            && (this.getColor() == null ? other.getColor() == null : this.getColor().equals(other.getColor()))
            && (this.getIntro() == null ? other.getIntro() == null : this.getIntro().equals(other.getIntro()))
            && (this.getDietfocus() == null ? other.getDietfocus() == null : this.getDietfocus().equals(other.getDietfocus()))
            && (this.getDietrecipes() == null ? other.getDietrecipes() == null : this.getDietrecipes().equals(other.getDietrecipes()))
            && (this.getEmotionfocus() == null ? other.getEmotionfocus() == null : this.getEmotionfocus().equals(other.getEmotionfocus()))
            && (this.getEmotionsteps() == null ? other.getEmotionsteps() == null : this.getEmotionsteps().equals(other.getEmotionsteps()))
            && (this.getBodyfocus() == null ? other.getBodyfocus() == null : this.getBodyfocus().equals(other.getBodyfocus()))
            && (this.getBodyexercise() == null ? other.getBodyexercise() == null : this.getBodyexercise().equals(other.getBodyexercise()))
            && (this.getPracticefocus() == null ? other.getPracticefocus() == null : this.getPracticefocus().equals(other.getPracticefocus()))
            && (this.getSeasonaltips() == null ? other.getSeasonaltips() == null : this.getSeasonaltips().equals(other.getSeasonaltips()))
            && (this.getDailyadvice() == null ? other.getDailyadvice() == null : this.getDailyadvice().equals(other.getDailyadvice()))
            && (this.getSort() == null ? other.getSort() == null : this.getSort().equals(other.getSort()))
            && (this.getCreatetime() == null ? other.getCreatetime() == null : this.getCreatetime().equals(other.getCreatetime()))
            && (this.getUpdatetime() == null ? other.getUpdatetime() == null : this.getUpdatetime().equals(other.getUpdatetime()))
            && (this.getIsdelete() == null ? other.getIsdelete() == null : this.getIsdelete().equals(other.getIsdelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTopickey() == null) ? 0 : getTopickey().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getOrgan() == null) ? 0 : getOrgan().hashCode());
        result = prime * result + ((getSeason() == null) ? 0 : getSeason().hashCode());
        result = prime * result + ((getColor() == null) ? 0 : getColor().hashCode());
        result = prime * result + ((getIntro() == null) ? 0 : getIntro().hashCode());
        result = prime * result + ((getDietfocus() == null) ? 0 : getDietfocus().hashCode());
        result = prime * result + ((getDietrecipes() == null) ? 0 : getDietrecipes().hashCode());
        result = prime * result + ((getEmotionfocus() == null) ? 0 : getEmotionfocus().hashCode());
        result = prime * result + ((getEmotionsteps() == null) ? 0 : getEmotionsteps().hashCode());
        result = prime * result + ((getBodyfocus() == null) ? 0 : getBodyfocus().hashCode());
        result = prime * result + ((getBodyexercise() == null) ? 0 : getBodyexercise().hashCode());
        result = prime * result + ((getPracticefocus() == null) ? 0 : getPracticefocus().hashCode());
        result = prime * result + ((getSeasonaltips() == null) ? 0 : getSeasonaltips().hashCode());
        result = prime * result + ((getDailyadvice() == null) ? 0 : getDailyadvice().hashCode());
        result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
        result = prime * result + ((getCreatetime() == null) ? 0 : getCreatetime().hashCode());
        result = prime * result + ((getUpdatetime() == null) ? 0 : getUpdatetime().hashCode());
        result = prime * result + ((getIsdelete() == null) ? 0 : getIsdelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", topickey=").append(topickey);
        sb.append(", name=").append(name);
        sb.append(", organ=").append(organ);
        sb.append(", season=").append(season);
        sb.append(", color=").append(color);
        sb.append(", intro=").append(intro);
        sb.append(", dietfocus=").append(dietfocus);
        sb.append(", dietrecipes=").append(dietrecipes);
        sb.append(", emotionfocus=").append(emotionfocus);
        sb.append(", emotionsteps=").append(emotionsteps);
        sb.append(", bodyfocus=").append(bodyfocus);
        sb.append(", bodyexercise=").append(bodyexercise);
        sb.append(", practicefocus=").append(practicefocus);
        sb.append(", seasonaltips=").append(seasonaltips);
        sb.append(", dailyadvice=").append(dailyadvice);
        sb.append(", sort=").append(sort);
        sb.append(", createtime=").append(createtime);
        sb.append(", updatetime=").append(updatetime);
        sb.append(", isdelete=").append(isdelete);
        sb.append("]");
        return sb.toString();
    }
}