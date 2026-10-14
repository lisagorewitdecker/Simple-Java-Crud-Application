// src/main/java/com/example/demo/service/ProductService.java
package com.example.demo.service;

import java.math.BigDecimal;
import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        super();
        this.repository = repository;
    }

    // Paginated fetch
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(ProductResponse::fromEntity);
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product(request.getName(), request.getPrice());
        return ProductResponse.fromEntity(repository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductResponse.fromEntity(product);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product existing = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        existing.setName(request.getName());
        existing.setPrice(request.getPrice());
        return ProductResponse.fromEntity(repository.save(existing));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(String name, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (minPrice != null && minPrice.signum() < 0) {
            throw new IllegalArgumentException("minPrice must not be negative");
        }
        if (maxPrice != null && maxPrice.signum() < 0) {
            throw new IllegalArgumentException("maxPrice must not be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice must not be greater than maxPrice");
        }
        String nameFilter = (name == null || name.isBlank()) ? null : name.trim();
        return repository.search(nameFilter, minPrice, maxPrice, pageable).map(ProductResponse::fromEntity);
    }
}
