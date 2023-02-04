package paymentGateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymentGateway.model.MockBankPaymentResponse;
import paymentGateway.model.MockBankPaymentRequest;
import paymentGateway.model.PaymentStatus;

import java.sql.Timestamp;
import java.util.Date;

@RestController
@RequestMapping("/mockBank")
public class MockBank  {

    @PostMapping("/processPayment")
    public ResponseEntity<MockBankPaymentResponse> processPayment(final @RequestBody MockBankPaymentRequest MockBankPaymentRequest) throws InterruptedException {
        Thread.sleep(3000); // TODO: Implement actual logic with different scenario here instead of hardcoded happy path.
        return ResponseEntity.ok(new MockBankPaymentResponse(MockBankPaymentRequest.getPaymentId(), PaymentStatus.PROCESSING, new Timestamp(new Date().getTime())));
    }

    @GetMapping("/paymentStatus/merchant/{merchantId}/user/{userId}/payment/{paymentId}")
    public ResponseEntity<MockBankPaymentResponse> getPaymentStatus(final @PathVariable Integer merchantId,
                                                                    final @PathVariable Integer userId,
                                                                    final @PathVariable Integer paymentId) throws InterruptedException {
        Thread.sleep(2000); // TODO: Implement actual logic with different scenario here instead of hardcoded happy path.
        return ResponseEntity.ok(new MockBankPaymentResponse(paymentId, PaymentStatus.SUCCESSFUL, new Timestamp(new Date().getTime()))); // TODO: Timestamp should be coming from the bank managed DB.
    }
}