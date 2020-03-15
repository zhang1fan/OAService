package com.xjj.oaoa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClaimVoucher {

    @TableId(type = IdType.AUTO)
    private Integer id;
    //报销单原因
    private String cause;

    private String createSn;

    private LocalDateTime createTime;

    private String nextDealSn;

    private Double totalAmount;

    private String status;
}
