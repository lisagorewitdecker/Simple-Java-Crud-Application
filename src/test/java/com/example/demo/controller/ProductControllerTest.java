package com.example.demo.controller;

import java.math.BigDecimal;
import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.exception.GlobalExceptionHandler;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ProductControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ProductController productController;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        productRepository.deleteAll();
    }

    @Test
    void testCreateAndGetProduct() {
        ProductRequest product = new ProductRequest("Laptop", new BigDecimal("1299.99"));
        ResponseEntity<ProductResponse> createResponse = productController.createProduct(product);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());
        assertEquals("Laptop", createResponse.getBody().getName());
        assertEquals(0, new BigDecimal("1299.99").compareTo(createResponse.getBody().getPrice()));

        Long id = createResponse.getBody().getId();
        ResponseEntity<ProductResponse> getResponse = productController.getProductById(id);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(id, getResponse.getBody().getId());
    }

    @Test
    void testGetProductById() throws Exception {
        Product saved = productRepository.save(new Product("Laptop", new BigDecimal("1299.99")));

        mockMvc.perform(get("/api/products/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1299.99));
    }

    @Test
    void testGetProductNotFound() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllProducts() throws Exception {
        productRepository.save(new Product("Phone", new BigDecimal("799.99")));
        productRepository.save(new Product("Tablet", new BigDecimal("499.99")));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("Phone"))
                .andExpect(jsonPath("$.content[0].price").value(799.99))
                .andExpect(jsonPath("$.content[1].name").value("Tablet"))
                .andExpect(jsonPath("$.content[1].price").value(499.99));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product saved = productRepository.save(new Product("Monitor", new BigDecimal("299.99")));
        String updatedPayload = """
                {
                    "name": "UltraWide Monitor",
                    "price": 349.99
                }
                """;

        mockMvc.perform(put("/api/products/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("UltraWide Monitor"))
                .andExpect(jsonPath("$.price").value(349.99));
    }

    @Test
    void testUpdateProductNotFound() throws Exception {
        String updatedPayload = """
                {
                    "name": "Ghost Monitor",
                    "price": 199.99
                }
                """;

        mockMvc.perform(put("/api/products/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPayload))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProduct() throws Exception {
        Product saved = productRepository.save(new Product("Keyboard", new BigDecimal("99.99")));

        mockMvc.perform(delete("/api/products/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProductNotFound() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProductValidationBlankName() throws Exception {
        String invalidPayload = """
                {
                    "name": "",
                    "price": 99.99
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void testCreateProductValidationNegativePrice() throws Exception {
        String invalidPayload = """
                {
                    "name": "Keyboard",
                    "price": -10.00
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    void testCreateProductValidationNullPrice() throws Exception {
        String invalidPayload = """
                {
                    "name": "Keyboard",
                    "price": null
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    void testUpdateProductValidationFailure() throws Exception {
        Product saved = productRepository.save(new Product("Valid Item", new BigDecimal("49.99")));
        String invalidPayload = """
                {
                    "name": "",
                    "price": -5.0
                }
                """;

        mockMvc.perform(put("/api/products/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchProductsByName() throws Exception {
        productRepository.save(new Product("Mechanical Keyboard", new BigDecimal("120.00")));
        productRepository.save(new Product("Wireless Mouse", new BigDecimal("40.00")));

        mockMvc.perform(get("/api/products").param("name", "keyboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Mechanical Keyboard"));
    }

    @Test
    void testFilterProductsByPriceRange() throws Exception {
        productRepository.save(new Product("Budget Mouse", new BigDecimal("15.00")));
        productRepository.save(new Product("Pro Mouse", new BigDecimal("60.00")));
        productRepository.save(new Product("Luxury Mouse", new BigDecimal("150.00")));

        mockMvc.perform(get("/api/products")
                        .param("minPrice", "20.00")
                        .param("maxPrice", "100.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Pro Mouse"));
    }

    @Test
    void testFilterProductsInvalidPriceRange() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("minPrice", "100.00")
                        .param("maxPrice", "50.00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("minPrice must not be greater than maxPrice"));
    }

    @Test
    void testFilterProductsNegativePrice() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("minPrice", "-10.00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("minPrice must not be negative"));

        mockMvc.perform(get("/api/products")
                        .param("maxPrice", "-5.00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("maxPrice must not be negative"));
    }

    @Test
    void testMalformedJsonRequest() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed or invalid request"));
    }

    @Test
    void testTypeMismatchException() throws Exception {
        mockMvc.perform(get("/api/products/{id}", "not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed or invalid request"));
    }
}


