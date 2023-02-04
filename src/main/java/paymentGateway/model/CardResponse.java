package paymentGateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponse {
    private Integer cardId;
    private String userId;
    private String merchantId;
    private String displayCardNumber;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
