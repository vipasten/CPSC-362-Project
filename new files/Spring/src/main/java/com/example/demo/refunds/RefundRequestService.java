package com.example.demo.refunds;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

// Marks this as a Spring service which handles all the business logic
@Service
public class RefundRequestService {

    private final RefundRequestRepository refundRequestRepository;

    // Constructor injection that allows Spring to automatically passes the repository in
    public RefundRequestService(RefundRequestRepository refundRequestRepository) {
        this.refundRequestRepository = refundRequestRepository;
    }

    // Saves a new refund request to the database
    // Called when customer submits refundrequest.html
    public RefundRequest submitRequest(
        String refNumber,
        String firstName,
        String lastName,
        String orderNumber,
        String movie,
        String reason,
        String details
    ) {
        RefundRequest request = new RefundRequest();
        request.setRefNumber(refNumber);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setOrderNumber(orderNumber);
        request.setMovie(movie);
        request.setReason(reason);
        request.setDetails(details);
        request.setStatus("PENDING");
        request.setSubmittedAt(LocalDateTime.now());
        return refundRequestRepository.save(request);
    }

    // Returns all refund requests which is used by admin and employee pages
    public List<RefundRequest> getAllRequests() {
        return refundRequestRepository.findAll();
    }

    // Finds a single refund request by REF number
    // Used when customer looks up their request on refundlookup.html
    public Optional<RefundRequest> getByRefNumber(String refNumber) {
        return refundRequestRepository.findByRefNumber(refNumber);
    }

    // Saves the admin reply and updates the replied timestamp
    // Called when admin clicks Send Reply on refunds.html
    public boolean replyToRequest(String refNumber, String replyText) {
        Optional<RefundRequest> found = refundRequestRepository.findByRefNumber(refNumber);
        if (!found.isPresent()) return false;

        RefundRequest request = found.get();
        request.setAdminReply(replyText);
        request.setRepliedAt(LocalDateTime.now());
        refundRequestRepository.save(request);
        return true;
    }

    // Updates the status to APPROVED
    // Called when admin clicks Approve on refunds.html
    public boolean approveRequest(String refNumber) {
        Optional<RefundRequest> found = refundRequestRepository.findByRefNumber(refNumber);
        if (!found.isPresent()) return false;

        RefundRequest request = found.get();
        request.setStatus("APPROVED");
        refundRequestRepository.save(request);
        return true;
    }

    // Updates the status to DENIED
    // Called when admin clicks Deny on refunds.html
    public boolean denyRequest(String refNumber) {
        Optional<RefundRequest> found = refundRequestRepository.findByRefNumber(refNumber);
        if (!found.isPresent()) return false;

        RefundRequest request = found.get();
        request.setStatus("DENIED");
        refundRequestRepository.save(request);
        return true;
    }

    // Updates the status to CLOSED
    // Called when customer clicks Close Request on refundthread.html
    public boolean closeRequest(String refNumber) {
        Optional<RefundRequest> found = refundRequestRepository.findByRefNumber(refNumber);
        if (!found.isPresent()) return false;

        RefundRequest request = found.get();
        request.setStatus("CLOSED");
        refundRequestRepository.save(request);
        return true;
    }
}