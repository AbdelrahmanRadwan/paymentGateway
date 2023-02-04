package paymentGateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paymentGateway.model.MockBankPaymentResponse;
import paymentGateway.model.MockBankPaymentRequest;
import paymentGateway.model.PaymentStatus;

import java.sql.Timestamp;
import java.util.Date;

@RestController
@RequestMapping("/mockBank")
public class MockBank {

    @PostMapping("/processPayment")
    public ResponseEntity<MockBankPaymentResponse> processPayment(final @RequestBody MockBankPaymentRequest MockBankPaymentRequest) throws InterruptedException {
        // TODO: Implement actual logic with different scenario here instead of hardcoded happy path.
        Thread.sleep(3000);
        return ResponseEntity.ok(new MockBankPaymentResponse(MockBankPaymentRequest.getPaymentId(), PaymentStatus.PROCESSING, new Timestamp(new Date().getTime())));
    }
}