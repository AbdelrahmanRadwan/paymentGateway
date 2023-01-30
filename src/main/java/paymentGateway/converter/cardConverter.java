package paymentGateway.converter;

import paymentGateway.model.*;

import java.time.Instant;
import java.util.Date;

public class cardConverter {
    public static String DISPLAY_CARD_NUMBER_FORMAT = "**** **** **** %s";

    public static Card toCard(final CardRequest cardRequest, final String merchantId, final String userId) {
        return Card.builder()
                .merchantId(merchantId)
                .userId(userId)
                .expMonth(cardRequest.getExpMonth())
                .expYear(cardRequest.getExpYear())
                .displayName(cardRequest.getDisplayName())
                .cardNumber(cardRequest.getCardNumber())
                .cvv(cardRequest.getCvv())
                .displayCardNumber(String.format(DISPLAY_CARD_NUMBER_FORMAT, cardRequest.getCardNumber().substring(cardRequest.getCardNumber().length() - 4)))
                .build();
    }
    public static CardResponse toCardResponse(final Card card) {
        return CardResponse.builder()
                .cardId(card.getCardId())
                .userId(card.getUserId())
                .merchantId(card.getMerchantId())
                .displayCardNumber(card.getDisplayCardNumber())
                .build();
    }
    public static Payment toPayment(final PaymentRequest paymentRequest, final String merchantId, final String userId, final String paymentStatus) {
        return Payment.builder()
                .amount(paymentRequest.getAmount())
                .currency(paymentRequest.getCurrency())
                .merchantId(merchantId)
                .userId(userId)
                .cardId(paymentRequest.getCardId())
                .createdAt(Date.from(Instant.now()))
                .paymentStatus(paymentStatus)
                .build();
    }

    public static PaymentResponse toPaymentResponse(final Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .userId(payment.getUserId())
                .merchantId(payment.getMerchantId())
                .cardId(payment.getCardId())
                .createdAt(payment.getCreatedAt())
                .paymentStatus(payment.getPaymentStatus())
                .build();
    }

    public static ProcessPaymentRequest toProcessPaymentRequest(final Card paymentCard, final PaymentRequest paymentRequest, final Integer paymentId) {
        return ProcessPaymentRequest.builder()
                .paymentId(paymentId)
                .merchantId(paymentCard.getMerchantId())
                .userId(paymentCard.getUserId())
                .cardNumber(paymentCard.getCardNumber())
                .cvv(paymentCard.getCvv())
                .displayName(paymentCard.getDisplayName())
                .expMonth(paymentCard.getExpMonth())
                .expYear(paymentCard.getExpYear())
                .amount(paymentRequest.getAmount())
                .currency(paymentRequest.getCurrency())
                .build();
    }
}
