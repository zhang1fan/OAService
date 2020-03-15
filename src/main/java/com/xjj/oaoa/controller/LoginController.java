package com.xjj.oaoa.controller;

import com.xjj.oaoa.dao.DepartmentMapper;
import com.xjj.oaoa.dao.EmployeeMapper;
import com.xjj.oaoa.entity.Department;
import com.xjj.oaoa.entity.Employee;
import com.xjj.oaoa.global.Contact;
import com.xjj.oaoa.global.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private DepartmentMapper departmentMapper;

    @GetMapping(value = "self")
    public String T(HttpSession session, Map<String, Object> map){
        Employee employee = (Employee) session.getAttribute("employee");
        System.out.println(employee);
        Department department = departmentMapper.selectById(employee.getDepartmentSn());
        map.put("employee1", employee);
        map.put("department", department);
        map.put("img",Util.take(employee));
        return "pages/self";
    }
    @GetMapping(value = "oa")
    public String oa(){
        return "redirect:self";
    }
    @GetMapping(value = "login")
    public String login(){
        return "pages/login";
    }

    @PostMapping(value = "tologin")
    public ModelAndView toLogin(@RequestParam("sn") String sn, @RequestParam("password") String password, HttpSession session){
        Employee employee = employeeMapper.selectById(sn);
        Department department = departmentMapper.selectById(employee.getDepartmentSn());
        ModelAndView model ;
        if (employee.getPassword().equals(password)){
            String img = Util.take(employee);
            model = new ModelAndView("pages/self");
            model.addObject("img", img);
            model.addObject("employee1", employee);
            model.addObject("department", department);
            session.setAttribute("employee", employee);
            return model;
        }
        model = new ModelAndView("pages/login");
        return model;
    }

    @GetMapping(value = "quit")
    public String quit(HttpSession session){
        session.setAttribute("employee", null);
        return "redirect:login";
    }
    @GetMapping(value = "to_change_password")
    public String to_change(Map<String,Object> map,HttpSession session){
        Employee employee = (Employee) session.getAttribute("employee");
        map.put("employee1", employee);
        map.put("img", Util.take(employee));
        return "pages/change_password";
    }

    @PostMapping(value = "change_password")
    public String change(@RequestParam("new1") String new1, @RequestParam("old") String old, @RequestParam("new2") String new2, HttpSession session){
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee.getPassword().equals(old)){
            if (new1.equals(new2)){
                employee.setPassword(new1);
                employeeMapper.updateById(employee);
                return "redirect:self";
            }
        }
        return "redirect:to_change_password";
    }
}
