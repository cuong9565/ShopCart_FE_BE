package com.shopcart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopcart.entity.TestEntity;
import com.shopcart.service.TestService;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping
    public List<TestEntity> getAll(){
        return testService.findAll();
    }
}
