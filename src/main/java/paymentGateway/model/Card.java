package paymentGateway.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "Card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer cardId;
    private String userId;
    private String merchantId;

    private String cardNumber;
    private String displayCardNumber;
    private String displayName;
    private Integer expMonth;
    private Integer expYear;
    private Integer cvv;

}
