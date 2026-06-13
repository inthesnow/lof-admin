package com.linkfit.admin.service.mybatis;

import com.linkfit.admin.domain.Product;
import com.linkfit.admin.mapper.ProductMapper;
import com.linkfit.admin.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyBatisProductService implements ProductService {

    private final ProductMapper productMapper;

    public MyBatisProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public List<Product> findAll(String type, int page, int size) {
        return productMapper.findAll(type, page * size, size);
    }

    @Override
    public long count(String type) {
        return productMapper.count(type);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productMapper.findById(id);
    }

    @Override
    public Product save(Product product) {
        productMapper.insert(product);
        return product;
    }

    @Override
    public Product update(Long id, Product product) {
        product.setId(id);
        productMapper.update(product);
        return product;
    }

    @Override
    public void delete(Long id) {
        productMapper.delete(id);
    }
}
