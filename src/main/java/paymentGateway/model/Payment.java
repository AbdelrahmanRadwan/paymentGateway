package paymentGateway.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer paymentId;
    @CreatedDate
    private Date createdAt; // TODO: Add updated at as well

    private Integer cardId; // TODO: Explore if it is makes sense to have all IDs as Strings instead
    private String merchantId;
    private String userId;

    private Double amount;
    private String currency; // TODO: Should be enum value with validation.
    private String paymentStatus; // TODO: Should be enum value with validation.

}
