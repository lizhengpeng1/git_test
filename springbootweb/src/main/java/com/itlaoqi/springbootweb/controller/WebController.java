package com.itlaoqi.springbootweb.controller;

import com.itlaoqi.springbootweb.entity.Dept;
import com.itlaoqi.springbootweb.entity.Emp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class WebController {
    Logger logger = LoggerFactory.getLogger(WebController.class);
    private List<Emp> emps = new ArrayList<Emp>();
    private List<Dept> depts = new ArrayList<Dept>();
    //Value中${}用于读取配置文件信息
    @Value("${app.upload.location}")
    private String path = null;
    public WebController() {
        System.out.println("AAA");
        emps.add(new Emp(7782, "CLARK", "DEVELOPER", "2017-01-02", 7780f, "RESEARCH"));
        emps.add(new Emp(7839, "KING", "CSO", "2018-03-04", 8820f, "SALES"));
        depts.add(new Dept(10,"REASERCH" , "2017-10-07"));
        depts.add(new Dept(20,"SALES" , "2015-12-01"));
        depts.add(new Dept(30,"ACCOUNTING" , "2013-03-02"));
    }

    //RequestMethod.GET 只有Get请求才能访问这个方法，如果是POST则会提示405错误
    //高内聚，低耦合设计原则
    @RequestMapping(value = "/" , method = RequestMethod.GET)
    public ModelAndView index(){
        //设置上下文数据，上下文数据说白了就是页面中要读取的记录
        //在SpringMVC中常用的设置上下文有三种：
        /**
         * 1. ModelAndView (推荐）
         * 2. Model
         * 3. WebRequest或者原生的HttpServletRequest对象（不推荐）
         */
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("emps" , emps);
        return mav;
    }

//    @RequestMapping(value="/dept" , method = RequestMethod.GET)
    //AJAX返回的是JSON数据，而不是跳转页面
    @GetMapping("/dept")
    @ResponseBody //@ResponseBody代表将返回值JSON化后送给浏览器 ,默认SB使用的JSON序列化工具为Jackson
    public List<Dept> obtainDept(){
        List<Dept> newDepts = new ArrayList();
        newDepts.add(new Dept(-1 , "请选择" , "1970-01-01"));
        newDepts.addAll(depts);
        return newDepts;
    }

    @GetMapping("/job")
    @ResponseBody
    public List<String> obtainJob(String d){
        List<String> jobs = new ArrayList<String>();
        jobs.add("请选择");
        if(d.equals("REASERCH")){
            jobs.add("CTO");
            jobs.add("Programmer");
        }else if(d.equals("SALES")){
            jobs.add("CSO");
            jobs.add("Saler");
        }else if(d.equals("ACCOUNTING")){
            jobs.add("CFO");
            jobs.add("Cashier");
        }
        return jobs;
    }


/*
    @RequestMapping(value = "/" , method = RequestMethod.GET)
    public String index(Model model){
        model.addAttribute("emp" , emps);
        return "index";
    }

    //WebRequest是对request对象的包装
    //这两者都是与J2EE容器强耦合，为了将来扩展性需要，不推荐使用
    public String index(WebRequest req , HttpServletRequest request){
        //setAttribute，是向当前的请求中放入对象，这种方式与WEb容器强耦合
        req.setAttribute("emps" , emps , WebRequest.SCOPE_REQUEST);
        request.setAttribute("emps" , emps);
        return "index";
    }*/
    @PostMapping("/create")
    //MultipartFile是上传文件接口，对应了保存的临时文件
    //参数名与前端的name保持一致
    //@RequestParam("photo") 代表了photo参数对应于前端name=photo的file框

    /**
     * 前后端数据绑定，后端使用bean接受，要求属性与前端name保持一致就可以自动注入
     */
    public ModelAndView create(Emp emp, @RequestParam("photo") MultipartFile photo) throws IOException {
        //photo.getOriginalFilename() 原始文件名
        //String filename=photo.getOriginalFilename();
        String filename = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String suffix = photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf("."));
        if(!suffix.equals(".jpg")){
            throw new RuntimeException("无效的图片格式");
        }
        emp.setPhotoFile(filename + suffix);
        emps.add(emp);//向数据源增加一个emp对象
        //Spring提供了一个文件操作类FileCopyUtil
        //对上传文件的复制，成为“归档”
        FileCopyUtils.copy(photo.getInputStream() , new FileOutputStream(path + filename + suffix ));
        //页面重定向到localhost/
        //格式为：redirect:跳转地址
        ModelAndView mav = new ModelAndView("redirect:/");
        return mav;
    }
}
