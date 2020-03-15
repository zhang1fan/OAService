package com.xjj.oaoa.controller;

import com.xjj.oaoa.dao.DepartmentMapper;
import com.xjj.oaoa.entity.Department;
import com.xjj.oaoa.entity.Employee;
import com.xjj.oaoa.global.Contact;
import com.xjj.oaoa.global.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class DepartController {

    @Autowired
    private DepartmentMapper departmentMapper;


    @GetMapping(value = "department_list")
    public String list(Model model, HttpSession session){
        Employee employee = (Employee) session.getAttribute("employee");
        List<Department> list = departmentMapper.selectList(null);
        System.out.println(list);
        model.addAttribute("list", list);
        model.addAttribute("img", Util.take(employee));
        model.addAttribute("employee1", employee);
        return "pages/department_list";
    }



    @GetMapping(value = "department_to_add")
    public String to_add(HttpSession session, Map<String, Object> map){
        Employee employee = (Employee) session.getAttribute("employee");
        map.put("employee1", employee);
        map.put("img",Util.take(employee));
        if (employee.getPost().equals(Contact.POST_GM)){

            return "pages/department_add";
        }
        return "pages/403";
    }

    @PostMapping(value = "department_add")
    public String add(@RequestParam("sn") String sn, @RequestParam("name") String name, @RequestParam("address") String address){
        Department department =new Department();
        department.setSn(sn);
        department.setAddress(address);
        department.setName(name);
        departmentMapper.insert(department);
        return "redirect:department_list";
    }

    @GetMapping(value = "department_to_update")
    public ModelAndView to_update(@RequestParam("sn") String sn,HttpSession session){
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee.getPost().equals(Contact.POST_GM))
        return new ModelAndView("pages/department_update").addObject("department",departmentMapper.selectById(sn)).addObject("employee1",employee).addObject("img",Util.take(employee));
        return new ModelAndView("pages/403").addObject("employee1",employee).addObject("img",Util.take(employee));
    }

    @PostMapping(value = "department_update")
    public String update(@RequestParam("sn") String sn, @RequestParam("name") String name, @RequestParam("address") String address){
        Department department= departmentMapper.selectById(sn);
        department.setName(name);
        department.setAddress(address);
        departmentMapper.updateById(department);
        return "redirect:department_list";

    }

    @GetMapping(value = "department_remove")
    public String remove(@RequestParam("sn") String sn, HttpSession session, Map<String, Object> map ){
        Employee employee = (Employee) session.getAttribute("employee");

        if (employee.getPost().equals(Contact.POST_GM)){
            departmentMapper.deleteById(sn);
            return "redirect:department_list";
        }else {
            map.put("employee1", employee);
            map.put("img",Util.take(employee));
            return "pages/403";
        }

    }
}
