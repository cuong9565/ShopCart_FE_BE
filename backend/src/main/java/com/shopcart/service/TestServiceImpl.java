package com.shopcart.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopcart.entity.TestEntity;
import com.shopcart.repository.TestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService{
    private final TestRepository testRepository;
    
    @Override 
    public List<TestEntity> findAll() {
        return testRepository.findAll();
    }
}
