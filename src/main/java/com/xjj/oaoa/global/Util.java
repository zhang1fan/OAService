package com.xjj.oaoa.global;

import com.xjj.oaoa.entity.Employee;

public class Util {
    public static String take(Employee employee) {

        if (employee.getPost().equals(Contact.POST_GM)) {
            return Contact.GM;
        } else if (employee.getPost().equals(Contact.POST_FM)) {
            return Contact.FM;
        } else if (employee.getPost().equals(Contact.POST_STAFF)) {
            return Contact.STAFF;
        } else {
            return Contact.CASHIER;
        }
    }
}
