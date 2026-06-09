package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.Product;
import com.linkfit.admin.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private static final Logger log = LoggerFactory.getLogger(ProductApiController.class);

    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("[Product] GET /api/products - type={}, page={}", type, page);
        return ApiResponse.ok(Map.of(
            "products", productService.findAll(type, page, size),
            "total", productService.count(type)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> get(@PathVariable Long id) {
        log.info("[Product] GET /api/products/{id} - id={}", id);
        return productService.findById(id)
            .map(p -> ResponseEntity.ok(ApiResponse.ok(p)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiResponse<Product> create(@RequestBody Product product) {
        log.info("[Product] POST /api/products");
        return ApiResponse.ok(productService.save(product));
    }

    @PutMapping("/{id}")
    public ApiResponse<Product> update(@PathVariable Long id, @RequestBody Product product) {
        log.info("[Product] PUT /api/products/{id} - id={}", id);
        return ApiResponse.ok(productService.update(id, product));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("[Product] DELETE /api/products/{id} - id={}", id);
        productService.delete(id);
        return ApiResponse.ok();
    }
}
