package com.yupi.yuoj.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 修行内容表
 * @TableName practice_content
 */
@TableName(value ="practice_content")
@Data
public class PracticeContent {
    /**
     * 前端内容 id
     */
    @TableId
    private String id;

    /**
     * 模块类型
     */
    private String type;

    /**
     * 标题
     */
    private String title;

    /**
     * 对应五行
     */
    private String wuxing;

    /**
     * 时长
     */
    private String duration;

    /**
     * 难度
     */
    private String difficulty;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 适合人群
     */
    private Object suitablecrowd;

    /**
     * 步骤
     */
    private Object steps;

    /**
     * 注意事项
     */
    private Object tips;

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
        PracticeContent other = (PracticeContent) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getWuxing() == null ? other.getWuxing() == null : this.getWuxing().equals(other.getWuxing()))
            && (this.getDuration() == null ? other.getDuration() == null : this.getDuration().equals(other.getDuration()))
            && (this.getDifficulty() == null ? other.getDifficulty() == null : this.getDifficulty().equals(other.getDifficulty()))
            && (this.getSummary() == null ? other.getSummary() == null : this.getSummary().equals(other.getSummary()))
            && (this.getSuitablecrowd() == null ? other.getSuitablecrowd() == null : this.getSuitablecrowd().equals(other.getSuitablecrowd()))
            && (this.getSteps() == null ? other.getSteps() == null : this.getSteps().equals(other.getSteps()))
            && (this.getTips() == null ? other.getTips() == null : this.getTips().equals(other.getTips()))
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
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getWuxing() == null) ? 0 : getWuxing().hashCode());
        result = prime * result + ((getDuration() == null) ? 0 : getDuration().hashCode());
        result = prime * result + ((getDifficulty() == null) ? 0 : getDifficulty().hashCode());
        result = prime * result + ((getSummary() == null) ? 0 : getSummary().hashCode());
        result = prime * result + ((getSuitablecrowd() == null) ? 0 : getSuitablecrowd().hashCode());
        result = prime * result + ((getSteps() == null) ? 0 : getSteps().hashCode());
        result = prime * result + ((getTips() == null) ? 0 : getTips().hashCode());
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
        sb.append(", type=").append(type);
        sb.append(", title=").append(title);
        sb.append(", wuxing=").append(wuxing);
        sb.append(", duration=").append(duration);
        sb.append(", difficulty=").append(difficulty);
        sb.append(", summary=").append(summary);
        sb.append(", suitablecrowd=").append(suitablecrowd);
        sb.append(", steps=").append(steps);
        sb.append(", tips=").append(tips);
        sb.append(", sort=").append(sort);
        sb.append(", createtime=").append(createtime);
        sb.append(", updatetime=").append(updatetime);
        sb.append(", isdelete=").append(isdelete);
        sb.append("]");
        return sb.toString();
    }
}