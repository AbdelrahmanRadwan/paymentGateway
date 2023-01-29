package paymentGateway.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {
    private String cardNumber;
    private String displayName;
    private Integer expMonth;
    private Integer expYear;
    private Integer cvv;
}
