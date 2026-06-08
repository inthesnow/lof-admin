package com.linkfit.admin.service;

import com.linkfit.admin.domain.FeedbackRequest;
import com.linkfit.admin.domain.FeedbackTicket;
import com.linkfit.admin.domain.TicketSettings;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FeedbackService {

    // Sector 7 — tickets
    List<FeedbackTicket> findTickets(Long gymId, String monthYear, String status, int page, int size);
    long countTickets(Long gymId, String monthYear, String status);
    FeedbackTicket issueTicket(String memberId, Long gymId, String ticketType, String monthYear);
    void updateTicketStatus(String id, String status);
    void assignTicketTrainer(String id, String trainerId);
    Map<String, Integer> usageSummary(Long gymId, String monthYear);

    // Sector 8 — settings
    Optional<TicketSettings> getSettings(Long gymId);
    TicketSettings updateSettings(Long gymId, int freePerMember, Integer maxPerMonth, boolean isBeta, String updatedBy);

    // Sector 10 — requests
    List<FeedbackRequest> findRequests(Long gymId, String status, String trainerId, int page, int size);
    long countRequests(Long gymId, String status, String trainerId);
    Optional<FeedbackRequest> findRequestById(String id);
    void assignRequestTrainer(String id, String trainerId);
    void respondToRequest(String id, String response);
    void updateRequestStatus(String id, String status);
}
