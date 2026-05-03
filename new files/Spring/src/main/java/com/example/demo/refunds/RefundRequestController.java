package com.example.demo.refunds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Marks this as a REST controller which handles HTTP requests and returns JSON
@RestController
@RequestMapping("/api/refunds")
public class RefundRequestController {

    private final RefundRequestService refundRequestService;

    // Constructor injection and allows Spring to automatically passes the service in
    public RefundRequestController(RefundRequestService refundRequestService) {
        this.refundRequestService = refundRequestService;
    }

    // POST /api/refunds/submit
    // Called when customer submits refundrequest.html
    // Receives all form fields and the generated REF number as parameters
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitRequest(
        @RequestParam String refNumber,
        @RequestParam String firstName,
        @RequestParam String lastName,
        @RequestParam String orderNumber,
        @RequestParam String movie,
        @RequestParam String reason,
        @RequestParam(required = false) String details
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            RefundRequest saved = refundRequestService.submitRequest(
                refNumber, firstName, lastName, orderNumber, movie, reason, details
            );
            response.put("success", true);
            response.put("refNumber", saved.getRefNumber());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to submit refund request.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // GET /api/refunds/all
    // Returns all refund requests which used by admin and employee pages
    @GetMapping("/all")
    public List<RefundRequest> getAllRequests() {
        return refundRequestService.getAllRequests();
    }

    // GET /api/refunds/lookup?ref=REF-XXXXXX
    // Returns a single refund request by REF number
    // Used when customer looks up their request on refundlookup.html
    @GetMapping("/lookup")
    public ResponseEntity<Map<String, Object>> lookupRequest(
        @RequestParam String ref
    ) {
        Map<String, Object> response = new HashMap<>();
        Optional<RefundRequest> found = refundRequestService.getByRefNumber(ref.trim().toUpperCase());

        if (!found.isPresent()) {
            response.put("success", false);
            response.put("message", "No refund request found with that REF number.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        RefundRequest request = found.get();
        response.put("success", true);
        response.put("refNumber", request.getRefNumber());
        response.put("firstName", request.getFirstName());
        response.put("lastName", request.getLastName());
        response.put("orderNumber", request.getOrderNumber());
        response.put("movie", request.getMovie());
        response.put("reason", request.getReason());
        response.put("details", request.getDetails());
        response.put("status", request.getStatus());
        response.put("adminReply", request.getAdminReply());
        response.put("submittedAt", request.getSubmittedAt());
        response.put("repliedAt", request.getRepliedAt());
        return ResponseEntity.ok(response);
    }

    // POST /api/refunds/reply?ref=REF-XXXXXX&replyText=...
    // Called when admin clicks Send Reply on refunds.html
    @PostMapping("/reply")
    public ResponseEntity<Map<String, Object>> replyToRequest(
        @RequestParam String ref,
        @RequestParam String replyText
    ) {
        Map<String, Object> response = new HashMap<>();
        boolean success = refundRequestService.replyToRequest(ref.trim().toUpperCase(), replyText);
        response.put("success", success);
        if (!success) {
            response.put("message", "Refund request not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    // POST /api/refunds/approve?ref=REF-XXXXXX
    // Called when admin clicks Approve on refunds.html
    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveRequest(
        @RequestParam String ref
    ) {
        Map<String, Object> response = new HashMap<>();
        boolean success = refundRequestService.approveRequest(ref.trim().toUpperCase());
        response.put("success", success);
        if (!success) {
            response.put("message", "Refund request not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    // POST /api/refunds/deny?ref=REF-XXXXXX
    // Called when admin clicks Deny on refunds.html
    @PostMapping("/deny")
    public ResponseEntity<Map<String, Object>> denyRequest(
        @RequestParam String ref
    ) {
        Map<String, Object> response = new HashMap<>();
        boolean success = refundRequestService.denyRequest(ref.trim().toUpperCase());
        response.put("success", success);
        if (!success) {
            response.put("message", "Refund request not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    // POST /api/refunds/close?ref=REF-XXXXXX
    // Called when customer clicks Close Request on refundthread.html
    @PostMapping("/close")
    public ResponseEntity<Map<String, Object>> closeRequest(
        @RequestParam String ref
    ) {
        Map<String, Object> response = new HashMap<>();
        boolean success = refundRequestService.closeRequest(ref.trim().toUpperCase());
        response.put("success", success);
        if (!success) {
            response.put("message", "Refund request not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }
}