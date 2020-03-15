package com.xjj.oaoa.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.xjj.oaoa.entity.ClaimVoucher;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ClaimVoucherMapper extends BaseMapper<ClaimVoucher> {

    @Select("select * from claim_voucher ${ew.customSqlSegment}")
    ClaimVoucher getByCreate(@Param(Constants.WRAPPER) Wrapper<ClaimVoucher> wrapper);
}
