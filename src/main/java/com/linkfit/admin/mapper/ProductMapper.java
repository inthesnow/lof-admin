package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {
    List<Product> findAll(@Param("type") String type, @Param("offset") int offset, @Param("size") int size);
    long count(@Param("type") String type);
    Optional<Product> findById(@Param("id") Long id);
    List<Product> findAllActive();
    void insert(Product product);
    void update(Product product);
    void delete(@Param("id") Long id);
}
