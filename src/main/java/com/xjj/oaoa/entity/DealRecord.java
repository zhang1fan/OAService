package com.xjj.oaoa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DealRecord {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer claimVoucherId;

    private String dealSn;

    private LocalDateTime dealTime;

    private String dealWay;

    private String dealResult;

    private String comment;
}
