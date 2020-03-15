package com.xjj.oaoa.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjj.oaoa.entity.ClaimVoucherItem;

import java.util.List;

public interface ClaimVoucherItemMapper extends BaseMapper<ClaimVoucherItem> {

    List<ClaimVoucherItem> getList();
}
