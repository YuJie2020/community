package com.singy.community.controller;

import com.singy.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot.";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求行和请求头
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames(); // 获取所有的请求头名称
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name); // 通过请求头的名称获取请求头的值
            System.out.println(name + ": " + value);
        }
        System.out.println(request.getParameter("code")); // 根据请求参数名称获取参数值

        // 返回响应数据
        response.setContentType("text/html;charset=utf-8"); // 设置Content-Type响应头：服务器传达给客户端的本次响应体数据格式以及编码格式信息
        try ( // java7新特性：将对象的创建定义在try后面的括号中，编译的时候会自动加一个finally代码块，将对象的close()定义在此代码块中
                PrintWriter writer = response.getWriter() // 获取字符输出流：用于设置响应体
        ) {
            writer.write("<h1>singy community</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GET请求 /student?current=1&limit=20
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // GET请求 /student/123 （使用路径变量的形式）
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    // POST请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 响应HTML数据
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("name", "张三");
        mv.addObject("age", "13");
        mv.setViewName("/demo/view");
        return mv;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "南加州大学");
        model.addAttribute("age", 100);
        return "/demo/view";
    }

    // 响应JSON数据（异步请求）
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 14);
        emp.put("salary", 25000.0);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 14);
        emp.put("salary", 25000.0);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "李四");
        emp.put("age", 24);
        emp.put("salary", 65000.0);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "王五");
        emp.put("age", 27);
        emp.put("salary", 135000.0);
        list.add(emp);
        return list;
    }
}