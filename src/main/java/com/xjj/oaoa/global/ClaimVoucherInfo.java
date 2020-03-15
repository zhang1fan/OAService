package com.xjj.oaoa.global;

import com.xjj.oaoa.entity.ClaimVoucher;
import com.xjj.oaoa.entity.ClaimVoucherItem;
import lombok.Data;

import java.util.List;

//整个报销单
@Data
public class ClaimVoucherInfo {

    //报销单头
    private ClaimVoucher claimVoucher;
    //报销名目
    private List<ClaimVoucherItem> items;
}
