package com.example.demo.refunds;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

// Tells Spring this class is a database table
@Entity
@Table(name = "refund_requests")
public class RefundRequest {

    // Auto-generated unique ID for each refund request
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The unique REF number shown to the customer (e.g. REF-482910)
    @Column(nullable = false, unique = true)
    private String refNumber;

    // Customer details
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    // The order number they are requesting a refund for
    @Column(nullable = false)
    private String orderNumber;

    // The movie associated with the order
    @Column(nullable = false)
    private String movie;

    // The reason selected from the dropdown
    @Column(nullable = false)
    private String reason;

    // Optional additional details from the customer
    @Column
    private String details;

    // Status of the request: PENDING, APPROVED, DENIED, CLOSED
    @Column(nullable = false)
    private String status;

    // Admin reply message
    @Column
    private String adminReply;

    // When the request was submitted
    @Column(nullable = false)
    private LocalDateTime submittedAt;

    // When the admin last replied
    @Column
    private LocalDateTime repliedAt;

    // --- Getters and Setters ---

    public Long getId() { return id; }

    public String getRefNumber() { return refNumber; }
    public void setRefNumber(String refNumber) { this.refNumber = refNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getMovie() { return movie; }
    public void setMovie(String movie) { this.movie = movie; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getRepliedAt() { return repliedAt; }
    public void setRepliedAt(LocalDateTime repliedAt) { this.repliedAt = repliedAt; }
}