package com.xjj.oaoa.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xjj.oaoa.dao.ClaimVoucherItemMapper;
import com.xjj.oaoa.dao.ClaimVoucherMapper;
import com.xjj.oaoa.dao.DealRecordMapper;
import com.xjj.oaoa.dao.EmployeeMapper;
import com.xjj.oaoa.entity.*;
import com.xjj.oaoa.global.ClaimVoucherInfo;
import com.xjj.oaoa.global.Contact;
import com.xjj.oaoa.global.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class CliamVoucherController {

    @Autowired
    private ClaimVoucherMapper claimVoucherMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private ClaimVoucherItemMapper claimVoucherItemMapper;
    @Autowired
    private DealRecordMapper dealRecordMapper;

    @GetMapping(value = "claim_voucher_deal")
    public String deal(Map<String, Object> map, HttpSession session){
        Employee employee = (Employee) session.getAttribute("employee");
        QueryWrapper<ClaimVoucher> queryWrapper = new QueryWrapper();
        queryWrapper.select().eq("next_deal_sn", employee.getSn());
        List<ClaimVoucher> list = claimVoucherMapper.selectList(queryWrapper);
        List<Employee> elist = employeeMapper.selectList(null);
        map.put("employee1", employee);
        map.put("img", Util.take(employee));
        map.put("elist", elist);
        map.put("list", list);
        map.put("Contact", new Contact());
        return "pages/claim_voucher_deal";
    }
    @GetMapping(value = "claim_voucher_submit")
    public String submit(@RequestParam("id") Integer id, HttpSession session, Map<String, Object> map){


        ClaimVoucher claimVoucher = claimVoucherMapper.selectById(id);
        //获得创建者信息
        Employee employee = employeeMapper.selectById(claimVoucher.getCreateSn());
        claimVoucher.setStatus(Contact.CLAIMVOUCHER_SUBMIT);
        //找到该员工的部门经理，并设置为下一个处理人,如果是总经理则，本人即为下一个处理人
        if (employee.getPost().equals(Contact.POST_STAFF)||employee.getPost().equals(Contact.POST_FM)){
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.select().eq("department_sn", employee.getDepartmentSn()).eq("post",Contact.POST_FM);
        Employee employee1 = employeeMapper.selectOne(queryWrapper);
        claimVoucher.setNextDealSn(employee1.getSn());
        }else {
            claimVoucher.setNextDealSn(employee.getSn());
        }
        claimVoucher.setStatus(Contact.CLAIMVOUCHER_SUBMIT);
        //更新报销单
        claimVoucherMapper.updateById(claimVoucher);
        //打上记录
        DealRecord dealRecord = new DealRecord();
        dealRecord.setDealWay(Contact.DEAL_SUBMIT);
        dealRecord.setDealSn(employee.getSn());
        dealRecord.setClaimVoucherId(id);
        dealRecord.setDealResult(Contact.CLAIMVOUCHER_SUBMIT);
        dealRecord.setDealTime(LocalDateTime.now());
        dealRecord.setComment("无");
        dealRecordMapper.insert(dealRecord);
        Employee employee1 = (Employee) session.getAttribute("employee");
        map.put("employee1", employee1);
        map.put("img",Util.take(employee1));
        return "pages/claim_voucher_deal";
    }
    @GetMapping(value = "claim_voucher_to_check")
    public String to_check(@RequestParam("id") Integer id, Map<String, Object> map, HttpSession session){

        //获取报销单claimVoucher
        ClaimVoucher claimVoucher = claimVoucherMapper.selectById(id);
        //获取名目items
        QueryWrapper<ClaimVoucherItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.select().eq("claim_voucher_id", id);
        List<ClaimVoucherItem> items = claimVoucherItemMapper.selectList(queryWrapper);
        //获取记录dealrecord
        QueryWrapper<DealRecord> dealRecordQueryWrapper = new QueryWrapper<>();
        dealRecordQueryWrapper.select().eq("claim_voucher_id", id);
        List<DealRecord> dealRecords = dealRecordMapper.selectList(dealRecordQueryWrapper);
        //当前处理人的信息
        Employee employee = (Employee) session.getAttribute("employee");
        //创建者信息
        Employee create_employee = employeeMapper.selectById(claimVoucher.getCreateSn());
        //所有人员信息
        List<Employee> employees = employeeMapper.selectList(null);
        map.put("claimVoucher", claimVoucher);
        map.put("items", items);
        map.put("dealRecords", dealRecords);
        map.put("deal_employee", employee);
        map.put("create_employee", create_employee);
        map.put("employees", employees);
        map.put("record", new DealRecord());
        map.put("Contact", new Contact());
        map.put("img",Util.take((Employee) session.getAttribute("employee")));
        return "pages/claim_voucher_check";
    }
    @GetMapping(value = "claim_voucher_check")
    public String check(@ModelAttribute("record") DealRecord dealRecord, HttpSession session){
        //获取处理人的信息
        Employee deal_employee = (Employee) session.getAttribute("employee");
        //处理记录已经有的信息为报销单id 备注 处理方式
        dealRecord.setDealTime(LocalDateTime.now());
        dealRecord.setDealSn(deal_employee.getSn());
        dealRecord.setDealResult(dealRecord.getDealWay());
        System.out.println(dealRecord);
        //通过处理方式来决定报销单的状态(4种：通过，打回，拒绝，打款)
        ClaimVoucher claimVoucher = claimVoucherMapper.selectById(dealRecord.getClaimVoucherId());

        if (dealRecord.getDealWay().equals(Contact.DEAL_PASS)){
            if (claimVoucher.getTotalAmount()<=Contact.LIMIT_CHECK|| deal_employee.getPost().equals(Contact.POST_GM)){
                claimVoucher.setStatus(Contact.CLAIMVOUCHER_APPROVED);
                QueryWrapper<Employee> wrapper = new QueryWrapper<>();
                wrapper.select().eq("post",Contact.POST_CASHIER);
                claimVoucher.setNextDealSn(employeeMapper.selectOne(wrapper).getSn());
            }else {
                //报销太多要总经理复审
                claimVoucher.setStatus(Contact.CLAIMVOUCHER_RECHECK);
                QueryWrapper<Employee> employeeQueryWrapper = new QueryWrapper<>();
                employeeQueryWrapper.select().eq("post",Contact.POST_GM);
                claimVoucher.setNextDealSn(employeeMapper.selectOne(employeeQueryWrapper).getSn());
            }
        }else if(dealRecord.getDealWay().equals(Contact.DEAL_BACK)){
            claimVoucher.setStatus(Contact.CLAIMVOUCHER_BACK);
            claimVoucher.setNextDealSn(claimVoucher.getCreateSn());
        }else if(dealRecord.getDealWay().equals(Contact.DEAL_REJECT)){
            claimVoucher.setStatus(Contact.CLAIMVOUCHER_TERMINATED);
        }else if (dealRecord.getDealWay().equals(Contact.DEAL_PAID)){
            QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
            queryWrapper.select().eq("post", Contact.POST_CASHIER);
            claimVoucher.setStatus(Contact.CLAIMVOUCHER_PAID);
            claimVoucher.setNextDealSn(employeeMapper.selectOne(queryWrapper).getSn());
        }

        dealRecordMapper.insert(dealRecord);
        claimVoucherMapper.updateById(claimVoucher);
        return "redirect:claim_voucher_deal";
    }
    @GetMapping(value = "claim_voucher_self")
    public String self(HttpSession session, Map<String,Object> map){
        //查询报销单
        Employee employee = (Employee) session.getAttribute("employee");
        QueryWrapper<ClaimVoucher> queryWrapperCliamVoucher = new QueryWrapper<>();
        queryWrapperCliamVoucher.select().eq("create_sn",employee.getSn());
        List<ClaimVoucher> claimVoucher = claimVoucherMapper.selectList(queryWrapperCliamVoucher);
        System.out.println(claimVoucher);
        //查询报销目录
        QueryWrapper<ClaimVoucherItem> queryWrapper = new QueryWrapper();
        queryWrapper.select().eq("claim_voucher_id",employee.getSn());
        List<ClaimVoucherItem> list = claimVoucherItemMapper.selectList(queryWrapper);
        //查找人

        map.put("em",employeeMapper.selectList(null));
        map.put("clist",claimVoucher);
        map.put("employee1", employee);
        map.put("img", Util.take(employee));
        return "pages/claim_voucher_self";
    }
    @GetMapping(value = "claim_voucher_detail")
    public String detail(@RequestParam("id") Integer id, Map<String, Object> map, HttpSession session){

        ClaimVoucher claimVoucher = claimVoucherMapper.selectById(id);

        QueryWrapper<ClaimVoucherItem> queryWrapper = new QueryWrapper();
        queryWrapper.select().eq("claim_voucher_id", id);
        List<ClaimVoucherItem> list = claimVoucherItemMapper.selectList(queryWrapper);

        QueryWrapper<DealRecord> recordQueryWrapper = new QueryWrapper<>();
        recordQueryWrapper.select().eq("claim_voucher_id", id);
        List<DealRecord> rlist = dealRecordMapper.selectList(recordQueryWrapper);

        List<Employee> employees = employeeMapper.selectList(null);
        Employee employee = (Employee) session.getAttribute("employee");
        map.put("employee1", employee);
        map.put("claimVoucher", claimVoucher);
        map.put("clist", list);
        map.put("records", rlist);
        map.put("employees", employees);
        map.put("img",Util.take(employee));
        return "pages/claim_voucher_detail";
    }
    @GetMapping(value = "claim_voucher_to_add")
    public String to_add(Map<String, Object> map, HttpSession session){
        Employee employee = (Employee) session.getAttribute("employee");
        map.put("employee1", employee);
        ClaimVoucherInfo claimVoucherInfo = new ClaimVoucherInfo();
        map.put("it", Contact.getItems());
        map.put("info", claimVoucherInfo);
        map.put("img",Util.take(employee));
        return "pages/claim_voucher_add";
    }
    @PostMapping(value = "claim_voucher_add")
    public String add(@ModelAttribute("info") ClaimVoucherInfo claimVoucherInfo, HttpSession session){
        System.out.println("表单提交的："+claimVoucherInfo.getClaimVoucher());
        ClaimVoucher claimVoucher = new ClaimVoucher();
        Employee employee = (Employee) session.getAttribute("employee");
        claimVoucher.setCreateSn(employee.getSn());
        claimVoucher.setCreateTime(LocalDateTime.now());
        claimVoucher.setNextDealSn(claimVoucher.getCreateSn());
        claimVoucher.setCause(claimVoucherInfo.getClaimVoucher().getCause());
        claimVoucher.setStatus(Contact.CLAIMVOUCHER_CREATED);
        claimVoucher.setTotalAmount(claimVoucherInfo.getClaimVoucher().getTotalAmount());
        claimVoucherInfo.setClaimVoucher(claimVoucher);
       claimVoucherMapper.insert(claimVoucher);
        for (int i=0;i< claimVoucherInfo.getItems().size();i++)
        {
            claimVoucherInfo.getItems().get(i).setClaimVoucherId(claimVoucherInfo.getClaimVoucher().getId());
            claimVoucherItemMapper.insert(claimVoucherInfo.getItems().get(i));
        }

        System.out.println(claimVoucherMapper.selectList(null));
        System.out.println("-------分割线-------");
        System.out.println(claimVoucherInfo);
        return "redirect:claim_voucher_deal";
    }

    @GetMapping(value = "claim_voucher_to_update")
    public String to_update(Map<String, Object> map, @RequestParam("id") Integer id, HttpSession session){
        Employee employee = (Employee) session.getAttribute("employee");

        ClaimVoucher claimVoucher = claimVoucherMapper.selectById(id);
        QueryWrapper<ClaimVoucherItem> queryWrapper = new QueryWrapper();
        queryWrapper.select().eq("claim_voucher_id", id);
        List<ClaimVoucherItem> lists = claimVoucherItemMapper.selectList(queryWrapper);
        ClaimVoucherInfo info = new ClaimVoucherInfo();
        info.setClaimVoucher(claimVoucher);
        info.setItems(lists);
        map.put("employee1", employee);
        map.put("img", Util.take(employee));
        map.put("info", info);
        map.put("it", Contact.getItems());
        System.out.println("修改前的info:"+info);
        return "pages/claim_voucher_update";
    }

    @PostMapping(value = "claim_voucher_update")
    public String update(@ModelAttribute("info") ClaimVoucherInfo info, HttpSession session){
        System.out.println("进入数据库前的info:"+info);
        Employee employee = (Employee) session.getAttribute("employee");
/*        info
        claimVoucherMapper.updateById()*/
        //由于前端页面没有提供claimvoucher的其他属性，因此需要手动添加某些属性
        ClaimVoucher claimVoucher = info.getClaimVoucher();
        claimVoucher.setCreateSn(employee.getSn());
        claimVoucher.setNextDealSn(employee.getSn());
        claimVoucher.setStatus(Contact.CLAIMVOUCHER_CREATED);
        claimVoucherMapper.updateById(claimVoucher);
        //对于修改后的item可能只会修改，不会删除或者增加

        //删除情况
        QueryWrapper<ClaimVoucherItem> queryWrapper = new QueryWrapper();
        queryWrapper.select().eq("claim_voucher_id", claimVoucher.getId());
        List<ClaimVoucherItem> news = info.getItems();
        List<ClaimVoucherItem> olds = claimVoucherItemMapper.selectList(queryWrapper);

        //判断以前的item是否还在现在的item中
        for (ClaimVoucherItem o : olds) {
            //不在标志
            boolean flag = false;
            for (ClaimVoucherItem n : news){
                if (n.getId() == o.getId())
                    flag = true;
                    break;
            }
            if (!flag)
                claimVoucherItemMapper.deleteById(o.getId());
        }
        //对于新增的，即没有claimvoucherid
        for (ClaimVoucherItem c : info.getItems()) {

            c.setClaimVoucherId(claimVoucher.getId());
            if (c.getId() != null){
                claimVoucherItemMapper.updateById(c);

            }else {
                claimVoucherItemMapper.insert(c);
            }

        }


        return "redirect:claim_voucher_deal";
    }

    public void test() {
        System.out.println("first commit");
    }

}
