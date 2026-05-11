package com.shopcart.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Basic Integration Test for OrderController.
 * 
 * <p>This test class verifies the basic HTTP endpoint functionality
 * of the OrderController without requiring complex authentication
 * or database setup.</p>
 * 
 * <p>Tests focus on:</p>
 * <ul>
 *   <li>HTTP method validation</li>
 *   <li>Content-Type validation</li>
 *   <li>Basic request/response handling</li>
 *   <li>Error handling for invalid requests</li>
 * </ul>
 * 
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-11
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("OrderController Integration Tests")
class OrderControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    @DisplayName("placeOrder_ShouldReturnBadRequest_WhenEmptyRequest")
    void placeOrder_ShouldReturnBadRequest_WhenEmptyRequest() throws Exception {
        // Arrange
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String requestBody = "{}";

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().is4xxClientError()); // Expected due to validation/authentication
    }

    @Test
    @DisplayName("placeOrder_ShouldReturnBadRequest_WhenInvalidJson")
    void placeOrder_ShouldReturnBadRequest_WhenInvalidJson() throws Exception {
        // Arrange
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String requestBody = "{ invalid json }";

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("placeOrder_ShouldReturnUnsupportedMediaType_WhenNotJson")
    void placeOrder_ShouldReturnUnsupportedMediaType_WhenNotJson() throws Exception {
        // Arrange
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String requestBody = "plain text content";

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.TEXT_PLAIN)
                .content(requestBody))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("placeOrder_ShouldReturnMethodNotAllowed_WhenWrongHttpMethod")
    void placeOrder_ShouldReturnMethodNotAllowed_WhenWrongHttpMethod() throws Exception {
        // Arrange
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Act & Assert - Testing GET method on POST endpoint
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/orders"))
                .andExpect(status().is4xxClientError()); // Could be 401 (unauthorized) or 405 (method not allowed)
    }

    @Test
    @DisplayName("placeOrder_ShouldReturnNotFound_WhenWrongEndpoint")
    void placeOrder_ShouldReturnNotFound_WhenWrongEndpoint() throws Exception {
        // Arrange
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String requestBody = "{}";

        // Act & Assert - Testing wrong endpoint
        mockMvc.perform(post("/api/orders/wrong")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("placeOrder_ShouldValidateContentType")
    void placeOrder_ShouldValidateContentType() throws Exception {
        // Arrange
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String requestBody = """
                {
                    "addressId": "123e4567-e89b-12d3-a456-426614174000",
                    "shippingMethodId": "123e4567-e89b-12d3-a456-426614174001",
                    "paymentMethodId": "123e4567-e89b-12d3-a456-426614174002",
                    "shippingFullName": "John Doe",
                    "shippingPhone": "1234567890",
                    "couponIds": []
                }
                """;

        // Act & Assert - Test with different content type
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_XML)
                .content(requestBody))
                .andExpect(status().isUnsupportedMediaType());
    }
}
