package paymentGateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import paymentGateway.model.Card;
import paymentGateway.model.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Card> findByMerchantIdAndUserId(final String merchantId, final String userId);
}
