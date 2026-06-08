package com.linkfit.admin.service.mock;

import com.linkfit.admin.domain.Product;
import com.linkfit.admin.service.ProductService;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class MockProductService implements ProductService {

    private final Map<Long, Product> store = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public MockProductService() {
        seed();
    }

    private void seed() {
        Object[][] data = {
            {"헬스 1개월", "MEMBERSHIP", 80000, 30},
            {"헬스 3개월", "MEMBERSHIP", 210000, 90},
            {"헬스 6개월", "MEMBERSHIP", 380000, 180},
            {"필라테스 그룹반 1개월", "GROUP", 150000, 30},
            {"PT 10회", "PT", 500000, 0},
            {"PT 20회", "PT", 900000, 0},
            {"락커 1개월", "LOCKER", 20000, 30},
            {"수건 월정액", "ITEM", 10000, 30},
        };
        for (Object[] row : data) {
            Product p = new Product();
            p.setId(seq.getAndIncrement());
            p.setName((String) row[0]);
            p.setType((String) row[1]);
            p.setPrice((int) row[2]);
            p.setDurationDays((int) row[3]);
            p.setActive(true);
            store.put(p.getId(), p);
        }
    }

    @Override
    public List<Product> findAll(String type, int page, int size) {
        return store.values().stream()
            .filter(p -> type == null || type.isBlank() || p.getType().equals(type))
            .skip((long) page * size)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    public long count(String type) {
        return store.values().stream()
            .filter(p -> type == null || type.isBlank() || p.getType().equals(type))
            .count();
    }

    @Override
    public Optional<Product> findById(Long id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public Product save(Product product) {
        product.setId(seq.getAndIncrement());
        product.setActive(true);
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public Product update(Long id, Product product) {
        product.setId(id);
        store.put(id, product);
        return product;
    }

    @Override
    public void delete(Long id) { store.remove(id); }
}
