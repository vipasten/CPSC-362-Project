package com.example.demo.refunds;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// Tells Spring to handle all the database operations automatically
// JpaRepository gives us save, findAll, findById, delete etc. for free
public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {

    // Custom method to find a refund request by its REF number
    // Spring automatically generates the SQL for this based on the method name
    // Used when customer looks up their request on refundlookup.html
    Optional<RefundRequest> findByRefNumber(String refNumber);
}