package com.yupi.yuoj.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户视图（脱敏）
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 五行体质 - 木
     */
    private Integer wood;

    /**
     * 五行体质 - 火
     */
    private Integer fire;

    /**
     * 五行体质 - 土
     */
    private Integer earth;

    /**
     * 五行体质 - 金
     */
    private Integer metal;

    /**
     * 五行体质 - 水
     */
    private Integer water;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
