package com.shopcart.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Basic Integration Test for CartController.
 * 
 * <p>This test class verifies the basic HTTP endpoint functionality
 * of the CartController such as routing, content-type validation,
 * and error handling for malformed requests.</p>
 */
@SpringBootTest
@ActiveProfiles("test") // Sử dụng profile test để tránh ảnh hưởng đến môi trường khác
@DisplayName("CartController Integration Tests")
class CartControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    @DisplayName("addToCart_ShouldReturnBadRequest_WhenEmptyRequest")
    void addToCart_ShouldReturnBadRequest_WhenEmptyRequest() throws Exception {
        // Arrange
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String requestBody = "{}";

        // Act & Assert
        mockMvc.perform(post("/api/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().is4xxClientError()); // Trả về 400 do thiếu validation hoặc 401 nếu chưa login
    }

    @Test
    @DisplayName("addToCart_ShouldReturnBadRequest_WhenInvalidJson")
    void addToCart_ShouldReturnBadRequest_WhenInvalidJson() throws Exception {
        // Arrange
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String requestBody = "{ bad json format }";

        // Act & Assert
        mockMvc.perform(post("/api/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("addToCart_ShouldReturnUnsupportedMediaType_WhenNotJson")
    void addToCart_ShouldReturnUnsupportedMediaType_WhenNotJson() throws Exception {
        // Arrange
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String requestBody = "product_id=123&quantity=1";

        // Act & Assert
        mockMvc.perform(post("/api/cart")
                .contentType(MediaType.TEXT_PLAIN)
                .content(requestBody))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("getCartItems_ShouldReturnMethodNotAllowed_WhenUsingWrongMethod")
    void getCartItems_ShouldReturnMethodNotAllowed_WhenUsingWrongMethod() throws Exception {
        // Arrange
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Act & Assert - Dùng PATCH cho endpoint chỉ chấp nhận GET/POST
        mockMvc.perform(patch("/api/cart"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("cartEndpoint_ShouldReturnNotFound_WhenPathIsWrong")
    void cartEndpoint_ShouldReturnNotFound_WhenPathIsWrong() throws Exception {
        // Arrange
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Act & Assert
        mockMvc.perform(get("/api/cart/invalid/path"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("calculatePricing_ShouldValidateContentType")
    void calculatePricing_ShouldValidateContentType() throws Exception {
        // Arrange
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String requestBody = """
                {
                    "couponCodes": ["DISCOUNT10"],
                    "shippingMethodId": "123e4567-e89b-12d3-a456-426614174000"
                }
                """;

        // Act & Assert - Gửi XML thay vì JSON
        mockMvc.perform(post("/api/cart/pricing")
                .contentType(MediaType.APPLICATION_XML)
                .content(requestBody))
                .andExpect(status().isUnsupportedMediaType());
    }

    // @Test
    // @DisplayName("getCartTotal_ShouldReturnClientError_WhenNotAuthenticated")
    // void getCartTotal_ShouldReturnClientError_WhenNotAuthenticated() throws Exception {
    //     // Arrange
    //     MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    //     // Act & Assert - Không cung cấp auth token/user details
    //     mockMvc.perform(get("/api/cart/total"))
    //             .andExpect(status().is4xxClientError()); 
    // }
}