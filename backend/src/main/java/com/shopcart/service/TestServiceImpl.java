package com.shopcart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopcart.entity.TestEntity;
import com.shopcart.repository.TestRepository;

@Service
public class TestServiceImpl implements TestService{
    @Autowired
    TestRepository testRepository;
    
    @Override 
    public List<TestEntity> findAll() {
        return testRepository.findAll();
    }
}
