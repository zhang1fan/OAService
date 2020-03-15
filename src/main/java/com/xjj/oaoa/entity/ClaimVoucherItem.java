package com.xjj.oaoa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

//报销项目
@Data
public class ClaimVoucherItem {

    @TableId(type = IdType.AUTO)
    private Integer id;
    //报销单号
    private Integer claimVoucherId;
    //报销类别
    private String item;
    //报销金额
    private Double amount;
    //备注
    private String comment;
}
