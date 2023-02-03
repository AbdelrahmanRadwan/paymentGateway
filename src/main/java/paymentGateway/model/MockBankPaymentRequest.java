package paymentGateway.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MockBankPaymentRequest {
    private Integer paymentId;
    private String userId;
    private String merchantId;

    private String cardNumber;
    private String displayName;
    private Integer expMonth;
    private Integer expYear;
    private Integer cvv;

    private Double amount;
    private String currency; // TODO: Should be enum value with validation.
}