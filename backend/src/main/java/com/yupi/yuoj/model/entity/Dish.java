package com.yupi.yuoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 菜品表
 * @TableName dish
 */
@TableName(value ="dish")
@Data
public class Dish {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 副标题
     */
    private String subtitle;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片链接
     */
    private String image;

    /**
     * 五行分类(wood/fire/earth/metal/water)
     */
    private String category;

    /**
     * 对应脏腑
     */
    private String organ;

    /**
     * 食材列表(JSON数组)
     */
    private Object ingredients;

    /**
     * 营养成分-卡路里
     */
    private Integer calories;

    /**
     * 营养成分-蛋白质
     */
    private Double protein;

    /**
     * 营养成分-脂肪
     */
    private Double fat;

    /**
     * 营养成分-碳水化合物
     */
    private Double carbs;

    /**
     * 营养成分-膳食纤维
     */
    private Double fiber;

    /**
     * 抗炎功效(JSON数组)
     */
    private Object antiinflammatory;

    /**
     * 道家养生功效(JSON数组)
     */
    private Object taoistbenefits;

    /**
     * 烹饪方法
     */
    private String cookingmethod;

    /**
     * 烹饪时间(分钟)
     */
    private Integer cooktime;

    /**
     * 难度 1-5
     */
    private Integer difficulty;

    /**
     * 适合体质(JSON数组)
     */
    private Object suitableconstitution;

    /**
     * 不适合体质(JSON数组)
     */
    private Object unsuitableconstitution;

    /**
     * 华夏智慧格言
     */
    private String wisdomquote;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 收藏数
     */
    private Integer favorites;

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
    @TableLogic
    private Integer isDelete;

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
        Dish other = (Dish) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getSubtitle() == null ? other.getSubtitle() == null : this.getSubtitle().equals(other.getSubtitle()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getImage() == null ? other.getImage() == null : this.getImage().equals(other.getImage()))
            && (this.getCategory() == null ? other.getCategory() == null : this.getCategory().equals(other.getCategory()))
            && (this.getOrgan() == null ? other.getOrgan() == null : this.getOrgan().equals(other.getOrgan()))
            && (this.getIngredients() == null ? other.getIngredients() == null : this.getIngredients().equals(other.getIngredients()))
            && (this.getCalories() == null ? other.getCalories() == null : this.getCalories().equals(other.getCalories()))
            && (this.getProtein() == null ? other.getProtein() == null : this.getProtein().equals(other.getProtein()))
            && (this.getFat() == null ? other.getFat() == null : this.getFat().equals(other.getFat()))
            && (this.getCarbs() == null ? other.getCarbs() == null : this.getCarbs().equals(other.getCarbs()))
            && (this.getFiber() == null ? other.getFiber() == null : this.getFiber().equals(other.getFiber()))
            && (this.getAntiinflammatory() == null ? other.getAntiinflammatory() == null : this.getAntiinflammatory().equals(other.getAntiinflammatory()))
            && (this.getTaoistbenefits() == null ? other.getTaoistbenefits() == null : this.getTaoistbenefits().equals(other.getTaoistbenefits()))
            && (this.getCookingmethod() == null ? other.getCookingmethod() == null : this.getCookingmethod().equals(other.getCookingmethod()))
            && (this.getCooktime() == null ? other.getCooktime() == null : this.getCooktime().equals(other.getCooktime()))
            && (this.getDifficulty() == null ? other.getDifficulty() == null : this.getDifficulty().equals(other.getDifficulty()))
            && (this.getSuitableconstitution() == null ? other.getSuitableconstitution() == null : this.getSuitableconstitution().equals(other.getSuitableconstitution()))
            && (this.getUnsuitableconstitution() == null ? other.getUnsuitableconstitution() == null : this.getUnsuitableconstitution().equals(other.getUnsuitableconstitution()))
            && (this.getWisdomquote() == null ? other.getWisdomquote() == null : this.getWisdomquote().equals(other.getWisdomquote()))
            && (this.getLikes() == null ? other.getLikes() == null : this.getLikes().equals(other.getLikes()))
            && (this.getFavorites() == null ? other.getFavorites() == null : this.getFavorites().equals(other.getFavorites()))
            && (this.getCreatetime() == null ? other.getCreatetime() == null : this.getCreatetime().equals(other.getCreatetime()))
            && (this.getUpdatetime() == null ? other.getUpdatetime() == null : this.getUpdatetime().equals(other.getUpdatetime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getSubtitle() == null) ? 0 : getSubtitle().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getImage() == null) ? 0 : getImage().hashCode());
        result = prime * result + ((getCategory() == null) ? 0 : getCategory().hashCode());
        result = prime * result + ((getOrgan() == null) ? 0 : getOrgan().hashCode());
        result = prime * result + ((getIngredients() == null) ? 0 : getIngredients().hashCode());
        result = prime * result + ((getCalories() == null) ? 0 : getCalories().hashCode());
        result = prime * result + ((getProtein() == null) ? 0 : getProtein().hashCode());
        result = prime * result + ((getFat() == null) ? 0 : getFat().hashCode());
        result = prime * result + ((getCarbs() == null) ? 0 : getCarbs().hashCode());
        result = prime * result + ((getFiber() == null) ? 0 : getFiber().hashCode());
        result = prime * result + ((getAntiinflammatory() == null) ? 0 : getAntiinflammatory().hashCode());
        result = prime * result + ((getTaoistbenefits() == null) ? 0 : getTaoistbenefits().hashCode());
        result = prime * result + ((getCookingmethod() == null) ? 0 : getCookingmethod().hashCode());
        result = prime * result + ((getCooktime() == null) ? 0 : getCooktime().hashCode());
        result = prime * result + ((getDifficulty() == null) ? 0 : getDifficulty().hashCode());
        result = prime * result + ((getSuitableconstitution() == null) ? 0 : getSuitableconstitution().hashCode());
        result = prime * result + ((getUnsuitableconstitution() == null) ? 0 : getUnsuitableconstitution().hashCode());
        result = prime * result + ((getWisdomquote() == null) ? 0 : getWisdomquote().hashCode());
        result = prime * result + ((getLikes() == null) ? 0 : getLikes().hashCode());
        result = prime * result + ((getFavorites() == null) ? 0 : getFavorites().hashCode());
        result = prime * result + ((getCreatetime() == null) ? 0 : getCreatetime().hashCode());
        result = prime * result + ((getUpdatetime() == null) ? 0 : getUpdatetime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", subtitle=").append(subtitle);
        sb.append(", description=").append(description);
        sb.append(", image=").append(image);
        sb.append(", category=").append(category);
        sb.append(", organ=").append(organ);
        sb.append(", ingredients=").append(ingredients);
        sb.append(", calories=").append(calories);
        sb.append(", protein=").append(protein);
        sb.append(", fat=").append(fat);
        sb.append(", carbs=").append(carbs);
        sb.append(", fiber=").append(fiber);
        sb.append(", antiinflammatory=").append(antiinflammatory);
        sb.append(", taoistbenefits=").append(taoistbenefits);
        sb.append(", cookingmethod=").append(cookingmethod);
        sb.append(", cooktime=").append(cooktime);
        sb.append(", difficulty=").append(difficulty);
        sb.append(", suitableconstitution=").append(suitableconstitution);
        sb.append(", unsuitableconstitution=").append(unsuitableconstitution);
        sb.append(", wisdomquote=").append(wisdomquote);
        sb.append(", likes=").append(likes);
        sb.append(", favorites=").append(favorites);
        sb.append(", createtime=").append(createtime);
        sb.append(", updatetime=").append(updatetime);
        sb.append(", isDelete=").append(isDelete);
        sb.append("]");
        return sb.toString();
    }
}
