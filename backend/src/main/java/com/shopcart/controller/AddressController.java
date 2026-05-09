package com.shopcart.controller;

import com.shopcart.dto.AddressResponse;
import com.shopcart.entity.Address;
import com.shopcart.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/{userId}")
    public ResponseEntity<AddressResponse> addAddress(
            @PathVariable UUID userId, 
            @RequestBody Address address) {
        return ResponseEntity.ok(addressService.createAddress(userId, address));
    }
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses() {
        List<AddressResponse> addresses = addressService.getAllAddresses();
        return ResponseEntity.ok(addresses);
    }
}