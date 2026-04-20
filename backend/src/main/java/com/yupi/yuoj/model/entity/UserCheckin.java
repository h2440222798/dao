package com.yupi.yuoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户修行打卡表
 * @TableName user_checkin
 */
@TableName(value ="user_checkin")
@Data
public class UserCheckin {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 id
     */
    private Long userid;

    /**
     * 打卡日期，兼容前端 zh-CN 日期格式
     */
    private String recorddate;

    /**
     * 清淡抗炎饮食
     */
    private Integer dietdone;

    /**
     * 完成修行练习
     */
    private Integer practicedone;

    /**
     * 情绪稳定
     */
    private Integer moodstable;

    /**
     * 按时休息
     */
    private Integer restenough;

    /**
     * 修行类型数组
     */
    private Object practicetypes;

    /**
     * 睡眠质量
     */
    private Integer sleepscore;

    /**
     * 情绪状态
     */
    private Integer moodscore;

    /**
     * 五行平衡评分
     */
    private Integer balancescore;

    /**
     * 完成项数量
     */
    private Integer completedcount;

    /**
     * 打卡备注
     */
    private String note;

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
        UserCheckin other = (UserCheckin) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserid() == null ? other.getUserid() == null : this.getUserid().equals(other.getUserid()))
            && (this.getRecorddate() == null ? other.getRecorddate() == null : this.getRecorddate().equals(other.getRecorddate()))
            && (this.getDietdone() == null ? other.getDietdone() == null : this.getDietdone().equals(other.getDietdone()))
            && (this.getPracticedone() == null ? other.getPracticedone() == null : this.getPracticedone().equals(other.getPracticedone()))
            && (this.getMoodstable() == null ? other.getMoodstable() == null : this.getMoodstable().equals(other.getMoodstable()))
            && (this.getRestenough() == null ? other.getRestenough() == null : this.getRestenough().equals(other.getRestenough()))
            && (this.getPracticetypes() == null ? other.getPracticetypes() == null : this.getPracticetypes().equals(other.getPracticetypes()))
            && (this.getSleepscore() == null ? other.getSleepscore() == null : this.getSleepscore().equals(other.getSleepscore()))
            && (this.getMoodscore() == null ? other.getMoodscore() == null : this.getMoodscore().equals(other.getMoodscore()))
            && (this.getBalancescore() == null ? other.getBalancescore() == null : this.getBalancescore().equals(other.getBalancescore()))
            && (this.getCompletedcount() == null ? other.getCompletedcount() == null : this.getCompletedcount().equals(other.getCompletedcount()))
            && (this.getNote() == null ? other.getNote() == null : this.getNote().equals(other.getNote()))
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
        result = prime * result + ((getRecorddate() == null) ? 0 : getRecorddate().hashCode());
        result = prime * result + ((getDietdone() == null) ? 0 : getDietdone().hashCode());
        result = prime * result + ((getPracticedone() == null) ? 0 : getPracticedone().hashCode());
        result = prime * result + ((getMoodstable() == null) ? 0 : getMoodstable().hashCode());
        result = prime * result + ((getRestenough() == null) ? 0 : getRestenough().hashCode());
        result = prime * result + ((getPracticetypes() == null) ? 0 : getPracticetypes().hashCode());
        result = prime * result + ((getSleepscore() == null) ? 0 : getSleepscore().hashCode());
        result = prime * result + ((getMoodscore() == null) ? 0 : getMoodscore().hashCode());
        result = prime * result + ((getBalancescore() == null) ? 0 : getBalancescore().hashCode());
        result = prime * result + ((getCompletedcount() == null) ? 0 : getCompletedcount().hashCode());
        result = prime * result + ((getNote() == null) ? 0 : getNote().hashCode());
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
        sb.append(", recorddate=").append(recorddate);
        sb.append(", dietdone=").append(dietdone);
        sb.append(", practicedone=").append(practicedone);
        sb.append(", moodstable=").append(moodstable);
        sb.append(", restenough=").append(restenough);
        sb.append(", practicetypes=").append(practicetypes);
        sb.append(", sleepscore=").append(sleepscore);
        sb.append(", moodscore=").append(moodscore);
        sb.append(", balancescore=").append(balancescore);
        sb.append(", completedcount=").append(completedcount);
        sb.append(", note=").append(note);
        sb.append(", createtime=").append(createtime);
        sb.append(", updatetime=").append(updatetime);
        sb.append(", isdelete=").append(isdelete);
        sb.append("]");
        return sb.toString();
    }
}