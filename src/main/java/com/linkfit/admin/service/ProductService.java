package com.linkfit.admin.service;

import com.linkfit.admin.domain.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll(String type, int page, int size);
    long count(String type);
    Optional<Product> findById(Long id);
    Product save(Product product);
    Product update(Long id, Product product);
    void delete(Long id);
}
