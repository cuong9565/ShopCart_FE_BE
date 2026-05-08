package com.shopcart.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopcart.entity.TestEntity;
import com.shopcart.service.TestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/test")
public class TestController {
    private final TestService testService;

    @GetMapping
    public List<TestEntity> getAll(){
        return testService.findAll();
    }
}
