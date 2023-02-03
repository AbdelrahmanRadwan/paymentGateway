package paymentGateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymentGateway.model.*;

@RestController
@RequestMapping("/mockBank")
public class MockBank {

    @PostMapping("/processPayment")
    public ResponseEntity<MockBankPaymentResponse> processPayment(final @RequestBody MockBankPaymentRequest MockBankPaymentRequest) throws InterruptedException {
        // TODO: Implement actual logic with different scenario here instead of hardcoded happy path.
        Thread.sleep(5000);
        return ResponseEntity.ok(new MockBankPaymentResponse(MockBankPaymentRequest.getPaymentId(), PaymentStatus.PROCESSING.name()));
    }
}