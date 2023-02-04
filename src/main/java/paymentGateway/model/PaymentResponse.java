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
public class PaymentResponse {
    private Integer paymentId;
    private Integer cardId;
    private String merchantId;
    private String userId;
    private PaymentStatus paymentStatus;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
