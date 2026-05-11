package com.shopcart.security;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class CartSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        this.objectMapper = new ObjectMapper();
    }
@Test
@WithMockCustomUser(username = "phuc@shopcart.com")
@DisplayName("ST_01: Kiểm tra lỗi IDOR - Chặn User A sửa giỏ hàng User B")
void testIDOR_UpdateOtherUserCart() throws Exception {
    UUID productIdOfUserB = UUID.randomUUID(); 
    Map<String, Object> attackData = new HashMap<>();
    attackData.put("productId", productIdOfUserB);
    attackData.put("quantity", 100);

    // Sử dụng assertThrows để bắt Exception từ Servlet
    org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
        mockMvc.perform(put("/api/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(attackData)));
    });
}
}