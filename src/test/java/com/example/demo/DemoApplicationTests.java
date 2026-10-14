package com.example.demo;

import com.example.demo.controller.ProductController;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DemoApplicationTests {

	@Autowired(required = false)
	private ProductRepository productRepository;

	@Autowired(required = false)
	private ProductController productController;

	@Test
	void contextLoads() {
		assertNotNull(productRepository, "ProductRepository bean should be discovered and instantiated by Spring Boot");
		assertNotNull(productController, "ProductController bean should be discovered and instantiated by Spring Boot");
	}

}
