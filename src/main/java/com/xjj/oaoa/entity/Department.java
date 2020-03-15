package com.xjj.oaoa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Department {

    @TableId
    private String sn;

    private String name;

    private String address;

}
