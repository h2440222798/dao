package com.yupi.yuoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 抗炎日记表
 * @TableName diary
 */
@TableName(value ="diary")
@Data
public class Diary {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 id
     */
    private Long userid;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 图片列表(JSON数组)
     */
    private Object images;

    /**
     * 标签列表(JSON数组)
     */
    private Object tags;

    /**
     * 今日饮食评分
     */
    private Integer dietscore;

    /**
     * 身体状态-精力
     */
    private Integer energy;

    /**
     * 身体状态-情绪
     */
    private Integer mood;

    /**
     * 身体状态-睡眠
     */
    private Integer sleep;

    /**
     * 身体状态-消化
     */
    private Integer digestion;

    /**
     * 今日践行(JSON数组)
     */
    private Object practices;

    /**
     * 华夏智慧感悟
     */
    private String wisdominsight;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 是否审核通过
     */
    private Integer isapproved;

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
        Diary other = (Diary) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserid() == null ? other.getUserid() == null : this.getUserid().equals(other.getUserid()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getImages() == null ? other.getImages() == null : this.getImages().equals(other.getImages()))
            && (this.getTags() == null ? other.getTags() == null : this.getTags().equals(other.getTags()))
            && (this.getDietscore() == null ? other.getDietscore() == null : this.getDietscore().equals(other.getDietscore()))
            && (this.getEnergy() == null ? other.getEnergy() == null : this.getEnergy().equals(other.getEnergy()))
            && (this.getMood() == null ? other.getMood() == null : this.getMood().equals(other.getMood()))
            && (this.getSleep() == null ? other.getSleep() == null : this.getSleep().equals(other.getSleep()))
            && (this.getDigestion() == null ? other.getDigestion() == null : this.getDigestion().equals(other.getDigestion()))
            && (this.getPractices() == null ? other.getPractices() == null : this.getPractices().equals(other.getPractices()))
            && (this.getWisdominsight() == null ? other.getWisdominsight() == null : this.getWisdominsight().equals(other.getWisdominsight()))
            && (this.getLikes() == null ? other.getLikes() == null : this.getLikes().equals(other.getLikes()))
            && (this.getIsapproved() == null ? other.getIsapproved() == null : this.getIsapproved().equals(other.getIsapproved()))
            && (this.getCreatetime() == null ? other.getCreatetime() == null : this.getCreatetime().equals(other.getCreatetime()))
            && (this.getUpdatetime() == null ? other.getUpdatetime() == null : this.getUpdatetime().equals(other.getUpdatetime()))
            && (this.getIsdelete() == null ? other.getIsdelete() == null : this.getIsdelete().equals(other.getIsdelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserid() == null) ? 0 : getUserid().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getImages() == null) ? 0 : getImages().hashCode());
        result = prime * result + ((getTags() == null) ? 0 : getTags().hashCode());
        result = prime * result + ((getDietscore() == null) ? 0 : getDietscore().hashCode());
        result = prime * result + ((getEnergy() == null) ? 0 : getEnergy().hashCode());
        result = prime * result + ((getMood() == null) ? 0 : getMood().hashCode());
        result = prime * result + ((getSleep() == null) ? 0 : getSleep().hashCode());
        result = prime * result + ((getDigestion() == null) ? 0 : getDigestion().hashCode());
        result = prime * result + ((getPractices() == null) ? 0 : getPractices().hashCode());
        result = prime * result + ((getWisdominsight() == null) ? 0 : getWisdominsight().hashCode());
        result = prime * result + ((getLikes() == null) ? 0 : getLikes().hashCode());
        result = prime * result + ((getIsapproved() == null) ? 0 : getIsapproved().hashCode());
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
        sb.append(", userid=").append(userid);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", images=").append(images);
        sb.append(", tags=").append(tags);
        sb.append(", dietscore=").append(dietscore);
        sb.append(", energy=").append(energy);
        sb.append(", mood=").append(mood);
        sb.append(", sleep=").append(sleep);
        sb.append(", digestion=").append(digestion);
        sb.append(", practices=").append(practices);
        sb.append(", wisdominsight=").append(wisdominsight);
        sb.append(", likes=").append(likes);
        sb.append(", isapproved=").append(isapproved);
        sb.append(", createtime=").append(createtime);
        sb.append(", updatetime=").append(updatetime);
        sb.append(", isdelete=").append(isdelete);
        sb.append("]");
        return sb.toString();
    }
}