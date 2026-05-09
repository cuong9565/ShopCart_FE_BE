package com.shopcart.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.shopcart.dto.UserResponse;
import com.shopcart.entity.User;
import com.shopcart.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
public UserResponse createUser(User user) {
    // 1. Kiểm tra email đã tồn tại chưa để tránh lỗi trùng lặp (Unique Constraint)
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        throw new RuntimeException("Email này đã được đăng ký!");
    }

    // 2. Lưu thực thể User vào database thông qua Repository
    // Lưu ý: Trường createdAt đã được khởi tạo trong Constructor của User entity
    User savedUser = userRepository.save(user);

    // 3. Chuyển đổi từ Entity sang DTO (UserResponse) để trả về
    return UserResponse.builder()
            .id(savedUser.getId())
            .fullName(savedUser.getFullName())
            .email(savedUser.getEmail())
            .phone(savedUser.getPhone())
            .createdAt(savedUser.getCreatedAt())
            .build();
}

    @Override
    public UserResponse registerUser(User user) {
        // Kiểm tra email trùng lặp trước khi lưu
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác.");
        }
        
        // Lưu ý: Password nên được hash bằng BCrypt ở tầng này trước khi truyền vào Entity
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse getUserProfile(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại với ID: " + id));
        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .build();
    }

}