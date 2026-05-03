package com.example.demo.refunds;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for refund message database operations
// Extends JpaRepository to get built in CRUD functionality for free
public interface RefundMessageRepository extends JpaRepository<RefundMessage, Long> {

    // Finds all messages for a specific refund request ordered by oldest first
    // Used to display the full conversation thread in chronological order
    List<RefundMessage> findByRefNumberOrderBySentAtAsc(String refNumber);
}