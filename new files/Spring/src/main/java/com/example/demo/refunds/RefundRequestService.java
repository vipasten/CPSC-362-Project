package com.example.demo.refunds;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RefundRequestService {

    private final RefundRequestRepository refundRequestRepository;
    private final RefundMessageRepository refundMessageRepository;

    public RefundRequestService(
        RefundRequestRepository refundRequestRepository,
        RefundMessageRepository refundMessageRepository
    ) {
        this.refundRequestRepository = refundRequestRepository;
        this.refundMessageRepository = refundMessageRepository;
    }

    // Saves a new refund request to the database and creates the first customer message
    // Now also saves the username of the logged in member
    public RefundRequest submitRequest(
        String refNumber,
        String username,
        String firstName,
        String lastName,
        String orderNumber,
        String movie,
        String reason,
        String details
    ) {
        RefundRequest request = new RefundRequest();
        request.setRefNumber(refNumber);
        request.setUsername(username);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setOrderNumber(orderNumber);
        request.setMovie(movie);
        request.setReason(reason);
        request.setDetails(details);
        request.setStatus("PENDING");
        request.setSubmittedAt(LocalDateTime.now());
        refundRequestRepository.save(request);

        // Save the customer's initial message to the messages table
        if (details != null && !details.trim().isEmpty()) {
            RefundMessage firstMessage = new RefundMessage();
            firstMessage.setRefNumber(refNumber);
            firstMessage.setSender("CUSTOMER");
            firstMessage.setSenderName(firstName + " " + lastName);
            firstMessage.setMessage(details);
            firstMessage.setSentAt(LocalDateTime.now());
            refundMessageRepository.save(firstMessage);
        }

        return request;
    }

    // Returns all refund requests - used by admin and employee pages
    public List<RefundRequest> getAllRequests() {
        return refundRequestRepository.findAll();
    }

    // Finds a single refund request by REF number
    public Optional<RefundRequest> getByRefNumber(String refNumber) {
        return refundRequestRepository.findByRefNumber(refNumber);
    }

    // Returns all refund requests submitted by a specific username
    // Used on the member account page to show their request history
    public List<RefundRequest> getByUsername(String username) {
        return refundRequestRepository.findByUsername(username);
    }

    // Returns all messages for a specific refund request in chronological order
    public List<RefundMessage> getMessages(String refNumber) {
        return refundMessageRepository.findByRefNumberOrderBySentAtAsc(refNumber);
    }

    // Saves a new admin reply message to the thread
    public boolean replyToRequest(String refNumber, String replyText) {
        Optional<RefundRequest> found = refundRequestRepository.findByRefNumber(refNumber);
        if (!found.isPresent()) return false;

        RefundMessage message = new RefundMessage();
        message.setRefNumber(refNumber);
        message.setSender("ADMIN");
        message.setSenderName("Admin");
        message.setMessage(replyText);
        message.setSentAt(LocalDateTime.now());
        refundMessageRepository.save(message);

        RefundRequest request = found.get();
        request.setRepliedAt(LocalDateTime.now());
        refundRequestRepository.save(request);
        return true;
    }

    // Saves a new customer reply message to the thread
    public boolean customerReply(String refNumber, String replyText, String customerName) {
        Optional<RefundRequest> found = refundRequestRepository.findByRefNumber(refNumber);
        if (!found.isPresent()) return false;

        RefundMessage message = new RefundMessage();
        message.setRefNumber(refNumber);
        message.setSender("CUSTOMER");
        message.setSenderName(customerName);
        message.setMessage(replyText);
        message.setSentAt(LocalDateTime.now());
        refundMessageRepository.save(message);
        return true;
    }

    // Updates the status to APPROVED
    public boolean approveRequest(String refNumber) {
        Optional<RefundRequest> found = refundRequestRepository.findByRefNumber(refNumber);
        if (!found.isPresent()) return false;
        RefundRequest request = found.get();
        request.setStatus("APPROVED");
        refundRequestRepository.save(request);
        return true;
    }

    // Updates the status to DENIED
    public boolean denyRequest(String refNumber) {
        Optional<RefundRequest> found = refundRequestRepository.findByRefNumber(refNumber);
        if (!found.isPresent()) return false;
        RefundRequest request = found.get();
        request.setStatus("DENIED");
        refundRequestRepository.save(request);
        return true;
    }

    // Updates the status to CLOSED
    public boolean closeRequest(String refNumber) {
        Optional<RefundRequest> found = refundRequestRepository.findByRefNumber(refNumber);
        if (!found.isPresent()) return false;
        RefundRequest request = found.get();
        request.setStatus("CLOSED");
        refundRequestRepository.save(request);
        return true;
    }
}