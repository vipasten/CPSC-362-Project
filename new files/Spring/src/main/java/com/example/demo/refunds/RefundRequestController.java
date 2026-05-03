package com.example.demo.refunds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/refunds")
public class RefundRequestController {

    private final RefundRequestService refundRequestService;

    public RefundRequestController(RefundRequestService refundRequestService) {
        this.refundRequestService = refundRequestService;
    }

    // POST /api/refunds/submit
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitRequest(
        @RequestParam String refNumber,
        @RequestParam String firstName,
        @RequestParam String lastName,
        @RequestParam String orderNumber,
        @RequestParam String movie,
        @RequestParam String reason,
        @RequestParam(required = false) String details,
        HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Get the username from the session if logged in; null if guest
            String username = (String) session.getAttribute("username");

            RefundRequest saved = refundRequestService.submitRequest(
                refNumber, username, firstName, lastName, orderNumber, movie, reason, details
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

    // GET /api/refunds/mine
    // Returns all refund requests submitted by the currently logged in member
    // Used on the member account page to show their request history without needing a REF number
    @GetMapping("/mine")
    public ResponseEntity<Map<String, Object>> getMyRefunds(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            response.put("success", false);
            response.put("message", "Not logged in.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<RefundRequest> requests = refundRequestService.getByUsername(username);
        List<Map<String, Object>> result = buildRequestList(requests);

        response.put("success", true);
        response.put("refunds", result);
        return ResponseEntity.ok(response);
    }

    // GET /api/refunds/all
    // Returns all refund requests with messages; used by admin and employee pages
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllRequests() {
        List<RefundRequest> requests = refundRequestService.getAllRequests();
        return ResponseEntity.ok(buildRequestList(requests));
    }

    // Helper method to build a list of request maps with messages included
    private List<Map<String, Object>> buildRequestList(List<RefundRequest> requests) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (RefundRequest request : requests) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("refNumber", request.getRefNumber());
            entry.put("firstName", request.getFirstName());
            entry.put("lastName", request.getLastName());
            entry.put("orderNumber", request.getOrderNumber());
            entry.put("movie", request.getMovie());
            entry.put("reason", request.getReason());
            entry.put("details", request.getDetails());
            entry.put("status", request.getStatus());
            entry.put("submittedAt", request.getSubmittedAt());

            List<RefundMessage> messages = refundRequestService.getMessages(request.getRefNumber());
            List<Map<String, Object>> msgList = new ArrayList<>();
            for (RefundMessage msg : messages) {
                Map<String, Object> m = new HashMap<>();
                m.put("sender", msg.getSender());
                m.put("senderName", msg.getSenderName());
                m.put("message", msg.getMessage());
                m.put("sentAt", msg.getSentAt());
                msgList.add(m);
            }
            entry.put("messages", msgList);
            result.add(entry);
        }
        return result;
    }

    // GET /api/refunds/lookup?ref=REF-XXXXXX
    @GetMapping("/lookup")
    public ResponseEntity<Map<String, Object>> lookupRequest(@RequestParam String ref) {
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
        response.put("submittedAt", request.getSubmittedAt());

        List<RefundMessage> messages = refundRequestService.getMessages(request.getRefNumber());
        List<Map<String, Object>> msgList = new ArrayList<>();
        for (RefundMessage msg : messages) {
            Map<String, Object> m = new HashMap<>();
            m.put("sender", msg.getSender());
            m.put("senderName", msg.getSenderName());
            m.put("message", msg.getMessage());
            m.put("sentAt", msg.getSentAt());
            msgList.add(m);
        }
        response.put("messages", msgList);
        return ResponseEntity.ok(response);
    }

    // POST /api/refunds/reply - admin reply
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

    // POST /api/refunds/customerReply - customer reply
    @PostMapping("/customerReply")
    public ResponseEntity<Map<String, Object>> customerReply(
        @RequestParam String ref,
        @RequestParam String replyText,
        @RequestParam String customerName
    ) {
        Map<String, Object> response = new HashMap<>();
        boolean success = refundRequestService.customerReply(ref.trim().toUpperCase(), replyText, customerName);
        response.put("success", success);
        if (!success) {
            response.put("message", "Refund request not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    // POST /api/refunds/approve
    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveRequest(@RequestParam String ref) {
        Map<String, Object> response = new HashMap<>();
        boolean success = refundRequestService.approveRequest(ref.trim().toUpperCase());
        response.put("success", success);
        if (!success) {
            response.put("message", "Refund request not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    // POST /api/refunds/deny
    @PostMapping("/deny")
    public ResponseEntity<Map<String, Object>> denyRequest(@RequestParam String ref) {
        Map<String, Object> response = new HashMap<>();
        boolean success = refundRequestService.denyRequest(ref.trim().toUpperCase());
        response.put("success", success);
        if (!success) {
            response.put("message", "Refund request not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    // POST /api/refunds/close
    @PostMapping("/close")
    public ResponseEntity<Map<String, Object>> closeRequest(@RequestParam String ref) {
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