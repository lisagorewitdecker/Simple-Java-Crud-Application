package com.example.demo.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductRequest {

    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    public ProductRequest() {
        super();
    }

    public ProductRequest(String name, BigDecimal price) {
        super();
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductRequest that = (ProductRequest) o;
        return java.util.Objects.equals(name, that.name) &&
                java.util.Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, price);
    }

    @Override
    public String toString() {
        return "ProductRequest{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}