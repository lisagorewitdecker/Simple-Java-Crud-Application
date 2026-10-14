// src/main/java/com/example/demo/repository/ProductRepository.java
package com.example.demo.repository;

import java.math.BigDecimal;
import com.example.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Case-insensitive name search
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Price range filtering
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // Optional filters; null parameters are ignored
    @Query("""
            SELECT p FROM Product p
            WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """)
    Page<Product> search(@Param("name") String name,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         Pageable pageable);
}
