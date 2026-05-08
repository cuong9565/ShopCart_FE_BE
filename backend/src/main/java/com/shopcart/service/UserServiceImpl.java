package com.shopcart.service;

import org.springframework.stereotype.Service;

import com.shopcart.entity.User;
import com.shopcart.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
}
