package com.linkfit.admin.service.mybatis;

import com.linkfit.admin.domain.FeedbackRequest;
import com.linkfit.admin.domain.FeedbackTicket;
import com.linkfit.admin.domain.TicketSettings;
import com.linkfit.admin.mapper.FeedbackRequestMapper;
import com.linkfit.admin.mapper.FeedbackTicketMapper;
import com.linkfit.admin.service.FeedbackService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class MyBatisFeedbackService implements FeedbackService {

    private final FeedbackTicketMapper ticketMapper;
    private final FeedbackRequestMapper requestMapper;

    public MyBatisFeedbackService(FeedbackTicketMapper ticketMapper, FeedbackRequestMapper requestMapper) {
        this.ticketMapper  = ticketMapper;
        this.requestMapper = requestMapper;
    }

    // ── Sector 7 ──────────────────────────────────────────────

    @Override
    public List<FeedbackTicket> findTickets(Long gymId, String monthYear, String status, int page, int size) {
        return ticketMapper.findAll(gymId, monthYear, status, page * size, size);
    }

    @Override
    public long countTickets(Long gymId, String monthYear, String status) {
        return ticketMapper.count(gymId, monthYear, status);
    }

    @Override
    public FeedbackTicket issueTicket(String memberId, Long gymId, String ticketType, String monthYear) {
        if (monthYear == null || monthYear.isBlank()) {
            monthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        FeedbackTicket ticket = new FeedbackTicket();
        ticket.setId(UUID.randomUUID().toString());
        ticket.setMemberId(memberId);
        ticket.setGymId(gymId);
        ticket.setTicketType(ticketType != null ? ticketType : "free");
        ticket.setStatus("issued");
        ticket.setMonthYear(monthYear);
        // expires at end of the month
        LocalDateTime expires = LocalDate.parse(monthYear + "-01")
                .withDayOfMonth(LocalDate.parse(monthYear + "-01").lengthOfMonth())
                .atTime(23, 59, 59);
        ticket.setExpiresAt(expires);
        ticketMapper.insert(ticket);
        return ticket;
    }

    @Override
    public void updateTicketStatus(String id, String status) {
        ticketMapper.updateStatus(id, status);
    }

    @Override
    public void assignTicketTrainer(String id, String trainerId) {
        ticketMapper.assignTrainer(id, trainerId);
    }

    @Override
    public Map<String, Integer> usageSummary(Long gymId, String monthYear) {
        return Map.of(
                "issued",  ticketMapper.countByStatus(gymId, monthYear, "issued"),
                "used",    ticketMapper.countByStatus(gymId, monthYear, "used"),
                "expired", ticketMapper.countByStatus(gymId, monthYear, "expired"),
                "pending", ticketMapper.countByStatus(gymId, monthYear, "pending")
        );
    }

    // ── Sector 8 ──────────────────────────────────────────────

    @Override
    public Optional<TicketSettings> getSettings(Long gymId) {
        return ticketMapper.findSettingsByGymId(gymId);
    }

    @Override
    public TicketSettings updateSettings(Long gymId, int freePerMember, Integer maxPerMonth, boolean isBeta, String updatedBy) {
        TicketSettings s = ticketMapper.findSettingsByGymId(gymId).orElseGet(() -> {
            TicketSettings ns = new TicketSettings();
            ns.setId(UUID.randomUUID().toString());
            ns.setGymId(gymId);
            return ns;
        });
        s.setFreeTicketsPerMember(freePerMember);
        s.setMaxTicketsPerMonth(maxPerMonth);
        s.setBeta(isBeta);
        s.setUpdatedBy(updatedBy);
        if (s.getId() == null) s.setId(UUID.randomUUID().toString());
        ticketMapper.upsertSettings(s);
        return s;
    }

    // ── Sector 10 ─────────────────────────────────────────────

    @Override
    public List<FeedbackRequest> findRequests(Long gymId, String status, String trainerId, int page, int size) {
        return requestMapper.findAll(gymId, status, trainerId, page * size, size);
    }

    @Override
    public long countRequests(Long gymId, String status, String trainerId) {
        return requestMapper.count(gymId, status, trainerId);
    }

    @Override
    public Optional<FeedbackRequest> findRequestById(String id) {
        return requestMapper.findById(id);
    }

    @Override
    public void assignRequestTrainer(String id, String trainerId) {
        requestMapper.assignTrainer(id, trainerId);
    }

    @Override
    public void respondToRequest(String id, String response) {
        requestMapper.respond(id, response);
    }

    @Override
    public void updateRequestStatus(String id, String status) {
        requestMapper.updateStatus(id, status);
    }
}
