package com.team42.payment;

import com.team42.shared.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * POST /api/v1/payments/charge
     * Initiate a payment charge via the mock gateway.
     * Returns immediately with PENDING status. Real outcome via callback.
     */
    @PostMapping("/charge")
    public ResponseEntity<?> charge(@RequestBody Map<String, Object> request,
                                    @RequestHeader(value = "X-Mock-Mode", required = false) String mockMode,
                                    @RequestHeader(value = "X-Mock-Force", required = false) String mockForce) {
        try {
            int amount = Integer.parseInt(request.get("amount").toString());
            String currency = request.getOrDefault("currency", "BDT").toString();
            String bookingRef = request.get("bookingRef").toString();
            String userId = request.getOrDefault("userId", "").toString();
            String seatNumbers = request.getOrDefault("seatNumbers", "").toString();
            Long showtimeId = request.containsKey("showtimeId")
                    ? Long.valueOf(request.get("showtimeId").toString()) : null;

            Map<String, Object> result = paymentService.initiateCharge(
                amount, currency, bookingRef, userId, seatNumbers, showtimeId, mockMode, mockForce
            );
            return ResponseEntity.accepted().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * POST /api/v1/payments/webhook
     * Callback handler from the mock gateway.
     *
     * CRITICAL RULES:
     * 1. Always return 200 (even for duplicates) to prevent retry storms
     * 2. Deduplicate by event_id
     * 3. Handle callback arriving before /charge response
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody String rawBody,
                                     @RequestHeader(value = "X-Signature", required = false) String signature,
                                     @RequestHeader(value = "X-Gateway-Event", required = false) String gatewayEvent) {
        try {
            // Parse raw JSON manually to preserve raw body for HMAC
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> body = mapper.readValue(rawBody, Map.class);

            String eventId = (String) body.get("event_id");
            String paymentId = (String) body.get("payment_id");
            String bookingRef = (String) body.get("booking_ref");
            String status = (String) body.get("status");
            int amount = body.get("amount") != null ? Integer.parseInt(body.get("amount").toString()) : 0;

            paymentService.handleCallback(eventId, paymentId, bookingRef, status, amount, rawBody, signature);

            // ALWAYS return 200 - non-200 causes infinite retries
            return ResponseEntity.ok(Map.of("received", true));
        } catch (Exception e) {
            // Even on error, return 200 to prevent gateway retry storms
            System.err.println("⚠️ Webhook processing error: " + e.getMessage());
            return ResponseEntity.ok(Map.of("received", true, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/v1/payments/status/{bookingRef}
     * Check payment status by booking reference.
     */
    @GetMapping("/status/{bookingRef}")
    public ResponseEntity<?> getStatus(@PathVariable("bookingRef") String bookingRef) {
        PaymentRecordEntity record = paymentService.getPaymentByBookingRef(bookingRef);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Payment status", Map.of(
            "bookingRef", record.getBookingRef(),
            "paymentId", record.getPaymentId() != null ? record.getPaymentId() : "pending",
            "status", record.getStatus(),
            "amount", record.getAmount()
        )));
    }

    /**
     * POST /api/v1/payments/otp/send
     * Dispatch OTP code via Mock Gateway.
     */
    @PostMapping("/otp/send")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> body) {
        String phone = body.getOrDefault("phone", "");
        String ref = body.getOrDefault("ref", "otp_" + System.currentTimeMillis());
        Map<String, Object> result = paymentService.sendOtp(phone, ref);
        return ResponseEntity.accepted().body(result);
    }

    /**
     * POST /api/v1/payments/otp/verify
     * Verify OTP code via Mock Gateway.
     */
    @PostMapping("/otp/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> body) {
        String ref = body.getOrDefault("ref", "");
        String code = body.getOrDefault("code", "");
        Map<String, Object> result = paymentService.verifyOtp(ref, code);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/v1/payments/user/{userId}
     * Get all payments for a user (for My Bookings page).
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPayments(@PathVariable("userId") String userId) {
        List<PaymentRecordEntity> payments = paymentService.getPaymentsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User payments", payments));
    }

    /**
     * Health endpoint - must stay green even when gateway is down.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "payment-service"));
    }
}
