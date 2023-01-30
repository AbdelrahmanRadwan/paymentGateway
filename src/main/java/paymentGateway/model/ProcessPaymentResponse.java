package paymentGateway.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessPaymentResponse {
    private Integer paymentId;
    private String paymentStatus;
}
