package com.example.demo.refunds;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

// This entity represents a single message in a refund request conversation thread
// Each refund request can have many messages from both the customer and admin
@Entity
@Table(name = "refund_messages")
public class RefundMessage {

    // Auto-generated unique ID for each message
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The REF number of the refund request this message belongs to (e.g. REF-482910)
    // Used to link messages back to their parent refund request
    @Column(nullable = false)
    private String refNumber;

    // Who sent this message - either "CUSTOMER" or "ADMIN"
    @Column(nullable = false)
    private String sender;

    // The display name of the sender (e.g. "John Doe" or "Admin")
    @Column(nullable = false)
    private String senderName;

    // The actual message content
    @Column(nullable = false, length = 2000)
    private String message;

    // When this message was sent
    @Column(nullable = false)
    private LocalDateTime sentAt;

    // --- Getters and Setters ---

    public Long getId() { return id; }

    public String getRefNumber() { return refNumber; }
    public void setRefNumber(String refNumber) { this.refNumber = refNumber; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}