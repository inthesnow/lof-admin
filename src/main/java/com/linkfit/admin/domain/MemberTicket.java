package com.linkfit.admin.domain;

public class MemberTicket {
    private String ticketType;
    private int remaining;

    public MemberTicket() {}

    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }
    public int getRemaining() { return remaining; }
    public void setRemaining(int remaining) { this.remaining = remaining; }
}
