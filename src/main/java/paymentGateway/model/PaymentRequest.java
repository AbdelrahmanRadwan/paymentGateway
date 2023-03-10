package paymentGateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    private Integer cardId;
    private Double amount;
    private String currency; // TODO: Should be an enum value with validation.
}
