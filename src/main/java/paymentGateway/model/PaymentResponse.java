package paymentGateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private Integer paymentId;
    private Integer cardId;
    private String merchantId;
    private String userId;
    private Date createdAt; // TODO: Add updated at as well
    private String paymentStatus;
}
