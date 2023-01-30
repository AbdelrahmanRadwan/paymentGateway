package paymentGateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymentGateway.model.*;

@RestController
@RequestMapping("/mockBank")
public class MockBank {
    public static String PAYMENT_STATUS_PROCESSING = "PROCESSING";

    @PostMapping("/processPayment")
    public ResponseEntity<ProcessPaymentResponse> processPayment(final @RequestBody ProcessPaymentRequest ProcessPaymentRequest) {
        // TODO: Implement actual logic with different scenario here instead of hardcoded happy path.
        return ResponseEntity.ok(new ProcessPaymentResponse(ProcessPaymentRequest.getPaymentId(), PAYMENT_STATUS_PROCESSING));
    }

}
