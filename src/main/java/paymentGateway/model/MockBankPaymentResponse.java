package paymentGateway.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockBankPaymentResponse {
    private Integer paymentId;
    private PaymentStatus paymentStatus;
    private Timestamp updatedAt;
}
