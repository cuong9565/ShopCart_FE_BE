package com.shopcart.service;

import com.shopcart.dto.AddressResponse;
import com.shopcart.entity.Address;
import com.shopcart.entity.User;
import com.shopcart.repository.AddressRepository;
import com.shopcart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public AddressResponse createAddress(UUID userId, Address address) {
        // Tìm user theo ID, nếu không có thì báo lỗi
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        // Thiết lập mối quan hệ
        address.setUser(user);
        Address saved = addressRepository.save(address);

        return AddressResponse.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .phone(saved.getPhone())
                .province(saved.getProvince())
                .district(saved.getDistrict())
                .ward(saved.getWard())
                .detail(saved.getDetail())
                .userId(user.getId())
                .build();
    }

    @Override
public List<AddressResponse> getAllAddresses() {
    return addressRepository.findAll().stream()
            .map(address -> AddressResponse.builder()
                    .id(address.getId())
                    .fullName(address.getFullName())
                    .phone(address.getPhone())
                    .province(address.getProvince())
                    .district(address.getDistrict())
                    .ward(address.getWard())
                    .detail(address.getDetail())
                    .userId(address.getUser() != null ? address.getUser().getId() : null)
                    .build())
            .collect(Collectors.toList());
}
}