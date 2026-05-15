package com.linkfit.admin.service;

import com.linkfit.admin.domain.Consult;
import java.util.List;
import java.util.Optional;

public interface ConsultService {
    List<Consult> findAll(String type, int page, int size);
    long count(String type);
    Optional<Consult> findById(Long id);
    Consult saveNew(Consult consult);
    Consult saveExisting(Consult consult);
    Consult update(Long id, Consult consult);
    void delete(Long id);
}
