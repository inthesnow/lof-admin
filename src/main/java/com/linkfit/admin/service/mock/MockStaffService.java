package com.linkfit.admin.service.mock;

import com.linkfit.admin.domain.Staff;
import com.linkfit.admin.service.StaffService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MockStaffService implements StaffService {

    private final Map<String, Staff> store = new LinkedHashMap<>();

    public MockStaffService() {
        seed();
    }

    private void seed() {
        String[][] data = {
            {"김관리", "010-1111-2222", "admin@linkfit.com", "SUPER_ADMIN"},
            {"이트레이너", "010-3333-4444", "trainer1@linkfit.com", "TRAINER"},
            {"박매니저", "010-5555-6666", "manager@linkfit.com", "ADMIN"},
            {"최트레이너", "010-7777-8888", "trainer2@linkfit.com", "TRAINER"},
        };
        for (String[] row : data) {
            Staff s = new Staff();
            s.setId(UUID.randomUUID().toString());
            s.setName(row[0]);
            s.setPhone(row[1]);
            s.setEmail(row[2]);
            s.setRole(row[3]);
            s.setHireDate(LocalDate.now().minusMonths(new Random().nextInt(24)));
            s.setStatus("ACTIVE");
            store.put(s.getId(), s);
        }
    }

    @Override
    public List<Staff> findAll(String role, int page, int size) {
        return store.values().stream()
            .filter(s -> role == null || role.isBlank() || s.getRole().equals(role))
            .skip((long) page * size)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    public long count(String role) {
        return store.values().stream()
            .filter(s -> role == null || role.isBlank() || s.getRole().equals(role))
            .count();
    }

    @Override
    public Optional<Staff> findById(String id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public Staff save(Staff staff) {
        if (staff.getId() == null || staff.getId().isBlank()) {
            staff.setId(UUID.randomUUID().toString());
        }
        staff.setHireDate(LocalDate.now());
        staff.setStatus("ACTIVE");
        store.put(staff.getId(), staff);
        return staff;
    }

    @Override
    public Staff update(String id, Staff staff) {
        staff.setId(id);
        store.put(id, staff);
        return staff;
    }

    @Override
    public void delete(String id) { store.remove(id); }

    @Override
    public void updateRole(String id, String role) {
        Optional.ofNullable(store.get(id)).ifPresent(s -> s.setRole(role));
    }
}
