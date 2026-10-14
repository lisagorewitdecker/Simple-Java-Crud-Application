package com.example.demo.config;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadInitialData(ProductRepository repository) {
        return args -> {
            ObjectMapper objectMapper = new ObjectMapper();
            try (InputStream inputStream = getClass().getResourceAsStream("/products.json")) {
                if (inputStream != null && repository.count() == 0) {
                    List<Product> products = objectMapper.readValue(inputStream, new TypeReference<List<Product>>() {});
                    repository.saveAll(products);
                    System.out.println("Products loaded from products.json");
                }
            } catch (Exception e) {
                System.out.println("No initial data loaded: " + e.getMessage());
            }
        };
    }
}