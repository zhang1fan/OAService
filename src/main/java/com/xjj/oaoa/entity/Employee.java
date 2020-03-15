package com.xjj.oaoa.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjj.oaoa.global.Contact;
import lombok.Data;

@Data
public class Employee {

    @TableId
    private String sn;

    private String password;

    private String name;

    private String departmentSn;

    private String post;

    private String img;

    public String getImg(){
        if (this.getPost().equals(Contact.POST_GM)) {
            return Contact.GM;
        } else if (this.getPost().equals(Contact.POST_FM)) {
            return Contact.FM;
        } else if (this.getPost().equals(Contact.POST_STAFF)) {
            return Contact.STAFF;
        } else {
            return Contact.CASHIER;
        }
    }
}
