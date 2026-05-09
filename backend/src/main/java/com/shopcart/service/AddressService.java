package com.shopcart.service;

import com.shopcart.dto.AddressResponse;
import com.shopcart.entity.Address;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressResponse createAddress(UUID userId, Address address);
    List<AddressResponse> getAllAddresses();
}