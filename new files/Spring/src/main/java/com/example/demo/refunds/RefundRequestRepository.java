package com.example.demo.refunds;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for refund request database operations
public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {

    // Finds a refund request by its REF number
    // Used when customer looks up their request on refundlookup.html
    Optional<RefundRequest> findByRefNumber(String refNumber);

    // Finds all refund requests submitted by a specific username
    // Used on the member account page to show all their refund requests
    List<RefundRequest> findByUsername(String username);
}