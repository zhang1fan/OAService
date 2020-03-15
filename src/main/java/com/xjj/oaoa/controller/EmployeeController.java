package com.xjj.oaoa.controller;

import com.xjj.oaoa.dao.DepartmentMapper;
import com.xjj.oaoa.dao.EmployeeMapper;
import com.xjj.oaoa.entity.Employee;
import com.xjj.oaoa.global.Contact;
import com.xjj.oaoa.global.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @GetMapping(value = "employee_list")
    public String list(Model model, HttpSession session){
        Employee employee = (Employee) session.getAttribute("employee");
        model.addAttribute("employee1", employee);
        model.addAttribute("departmentlist",departmentMapper.selectList(null));
        model.addAttribute("employeeList",employeeMapper.selectList(null));
        model.addAttribute("img", Util.take((Employee) session.getAttribute("employee")));
        return "pages/employee_list";
    }

    @GetMapping(value = "employee_to_add")
    public String to_add(Map<String,Object> map, HttpSession session){
        Employee employee = (Employee) session.getAttribute("employee");
        map.put("employee1", employee);
        map.put("img",Util.take(employee));
        map.put("dlist",departmentMapper.selectList(null));
        map.put("plist", Contact.getPosts());
        map.put("employee", new Employee());
        return "pages/employee_add";
    }

    @PostMapping(value = "employee_add")
    public String add(@ModelAttribute("employee")Employee employee){
        employee.setPassword("000000");
        employeeMapper.insert(employee);
        return "redirect:employee_list";
    }

    @GetMapping(value = "employee_remove")
    public String remove(@RequestParam("sn") String sn){
        employeeMapper.deleteById(sn);
        return "redirect:employee_list";
    }

    @GetMapping(value = "employee_to_update")
    public String to_update(@RequestParam("sn") String sn, Map<String, Object> map, HttpSession session){
        Employee employee = (Employee) session.getAttribute("employee");
        map.put("employee1", employee);
        map.put("employee", new Employee());
        map.put("em", employeeMapper.selectById(sn));
        map.put("plist", Contact.getPosts());
        map.put("dlist",departmentMapper.selectList(null));
        map.put("img",Util.take((Employee) session.getAttribute("employee")));
        return "pages/employee_update";
    }

    @PostMapping(value = "employee_update")
    public String update(@ModelAttribute("employee") Employee employee){
        employeeMapper.updateById(employee);
        return "redirect:employee_list";
    }
}
