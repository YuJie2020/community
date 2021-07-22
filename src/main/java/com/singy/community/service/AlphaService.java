package com.singy.community.service;

import com.singy.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype") // 指定 bean 的作用范围：singleton prototype ...
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    @PostConstruct // 指定初始化方法：在构造器之后调用
    public void init() {
        System.out.println("初始化AlphaService");
    }

    @PreDestroy // 指定销毁方法：在销毁对象之前调用
    public void destroy() {
        System.out.println("销毁AlphaService");
    }

    public String find() {
        return alphaDao.select();
    }
}
