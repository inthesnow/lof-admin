package com.linkfit.admin.service.mock;

import com.linkfit.admin.domain.Consult;
import com.linkfit.admin.service.ConsultService;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class MockConsultService implements ConsultService {

    private final Map<Long, Consult> store = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public MockConsultService() {
        seed();
    }

    private void seed() {
        String[][] data = {
            {"NEW", "홍길동", "010-1111-2222", "M", "헬스", "REGISTERED"},
            {"NEW", "김영희", "010-2222-3333", "F", "필라테스", "PENDING"},
            {"EXISTING", "이철수", "010-3333-4444", "M", "PT", "REGISTERED"},
        };
        for (String[] row : data) {
            Consult c = new Consult();
            c.setId(seq.getAndIncrement());
            c.setType(row[0]);
            c.setName(row[1]);
            c.setPhone(row[2]);
            c.setGender(row[3]);
            c.setInterest(row[4]);
            c.setResult(row[5]);
            c.setConsultDate(LocalDate.now().minusDays(new Random().nextInt(7)));
            c.setStaffName("김관리");
            store.put(c.getId(), c);
        }
    }

    @Override
    public List<Consult> findAll(String type, int page, int size) {
        return store.values().stream()
            .filter(c -> type == null || type.isBlank() || c.getType().equals(type))
            .skip((long) page * size)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    public long count(String type) {
        return store.values().stream()
            .filter(c -> type == null || type.isBlank() || c.getType().equals(type))
            .count();
    }

    @Override
    public Optional<Consult> findById(Long id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public Consult saveNew(Consult consult) {
        consult.setId(seq.getAndIncrement());
        consult.setType("NEW");
        consult.setConsultDate(LocalDate.now());
        store.put(consult.getId(), consult);
        return consult;
    }

    @Override
    public Consult saveExisting(Consult consult) {
        consult.setId(seq.getAndIncrement());
        consult.setType("EXISTING");
        consult.setConsultDate(LocalDate.now());
        store.put(consult.getId(), consult);
        return consult;
    }

    @Override
    public Consult update(Long id, Consult consult) {
        consult.setId(id);
        store.put(id, consult);
        return consult;
    }

    @Override
    public void delete(Long id) { store.remove(id); }
}
