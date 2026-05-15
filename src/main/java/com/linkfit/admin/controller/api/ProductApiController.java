package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.Product;
import com.linkfit.admin.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(Map.of(
            "products", productService.findAll(type, page, size),
            "total", productService.count(type)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> get(@PathVariable Long id) {
        return productService.findById(id)
            .map(p -> ResponseEntity.ok(ApiResponse.ok(p)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiResponse<Product> create(@RequestBody Product product) {
        return ApiResponse.ok(productService.save(product));
    }

    @PutMapping("/{id}")
    public ApiResponse<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return ApiResponse.ok(productService.update(id, product));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.ok();
    }
}
