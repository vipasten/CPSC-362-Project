package com.example.demo.auth.Support;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SupportTicketController {

    private final SupportTicketService ticketService;

    public SupportTicketController(SupportTicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Displays the support tickets list with optional search
    @GetMapping("/viewsupporttickets")
    public String viewTickets(@RequestParam(required = false) String search, Model model) {
        List<SupportTicket> tickets = ticketService.searchTickets(search);
        model.addAttribute("users", tickets);
        model.addAttribute("searchQuery", search != null ? search : "");
        return "viewsupporttickets";
    }

    // Handles the support ticket form submission
    @PostMapping("/submitsupportticket")
    public String submitTicket(@RequestParam String username, @RequestParam String fname,
        @RequestParam String lname,
        @RequestParam String subject,
        @RequestParam String details) {
        SupportTicket ticket = new SupportTicket();
        ticket.setUsername(username);
        ticket.setFirstName(fname);
        ticket.setLastName(lname);
        ticket.setSubject(subject);
        ticket.setDescription(details);
        ticketService.saveTicket(ticket);
        return "redirect:/html/pages/supportsubmit.html";
    }

    @PostMapping("/resolveticket/{id}")
    public String resolveTicket(@PathVariable Long id) {
        ticketService.resolveTicket(id);
        return "redirect:/viewsupporttickets";
    }
}
