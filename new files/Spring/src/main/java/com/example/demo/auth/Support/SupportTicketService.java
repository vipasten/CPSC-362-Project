package com.example.demo.auth.Support;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SupportTicketService {

    private final SupportTicketRepository ticketRepository;

    public SupportTicketService(SupportTicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }
    
    // Returns all tickets or filters by search query
    public List<SupportTicket> searchTickets(String search) {
        if (search == null || search.isEmpty()) {
            return ticketRepository.findAll();
        }
        return ticketRepository.findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            search, search, search
        );
    }

    // Saves a new support ticket
    public void saveTicket(SupportTicket ticket) {
        ticket.setPostedDate(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    // Marks a ticket as resolved
    public void resolveTicket(Long id) {
        SupportTicket ticket = ticketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID: " + id));
        ticket.setResolved(!ticket.isResolved()); // Toggle resolved status
        ticketRepository.save(ticket);
    }
}