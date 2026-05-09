package com.shopcart.service;

import com.shopcart.dto.UserResponse;
import com.shopcart.entity.User;
import java.util.List;
import java.util.UUID;

import org.jspecify.annotations.Nullable;

public interface UserService {
    UserResponse registerUser(User user);
    UserResponse getUserProfile(UUID id);
    List<UserResponse> getAllUsers();
    @Nullable
    Object createUser(User user);
}