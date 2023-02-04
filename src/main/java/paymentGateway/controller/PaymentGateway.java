package paymentGateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.context.request.async.DeferredResult;

import paymentGateway.model.*;
import paymentGateway.service.PaymentGatewayService;

import java.util.List;
import java.util.Optional;

import static paymentGateway.transformer.Transformer.toCardResponse;


@Slf4j
@RestController
@RequestMapping("/api")
public class PaymentGateway extends SpringBootServletInitializer {

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    /**
     * Payment Operations.
     **/

    @GetMapping("/payment/merchant/{merchantId}/user/{userId}/payment/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(final @PathVariable String merchantId,
                                                      final @PathVariable String userId,
                                                      final @PathVariable Integer paymentId) {
        // TODO: Add Authorization check based on the merchant and user.
        Optional<PaymentResponse> paymentResponse = paymentGatewayService.getPaymentById(paymentId);
        if (paymentResponse.isEmpty()) {
            log.error("Could not find payment with id {}", paymentId);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(paymentResponse.get());
    }

    @GetMapping("/payment/merchant/{merchantId}/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getAllPaymentsByMerchantAndUser(final @PathVariable String merchantId,
                                                                                 final @PathVariable String userId) {
        // TODO: Add Authorization check based on the merchant and user.
        return ResponseEntity.ok(paymentGatewayService.getAllPaymentsByMerchantAndUser(merchantId, userId));
    }

    @PostMapping("/payment/merchant/{merchantId}/user/{userId}")
    public DeferredResult<ResponseEntity<PaymentResponse>> processPayment(final @PathVariable String merchantId,
                                                                          final @PathVariable String userId,
                                                                          final @RequestBody PaymentRequest paymentRequest) {
        DeferredResult<ResponseEntity<PaymentResponse>> deferredPaymentResponse = new DeferredResult<>();
        // Fetch Payment Card.
        Optional<Card> usedCard = paymentGatewayService.getCardById(paymentRequest.getCardId());
        if (usedCard.isEmpty()) {
            log.error("Could not find card with id {}", paymentRequest.getCardId());
            deferredPaymentResponse.setResult(ResponseEntity.badRequest().build());
            return deferredPaymentResponse;
        }
        // Process the payment
        final Optional<PaymentResponse> paymentResponse = paymentGatewayService.storeAndProcessPayment(merchantId, userId, usedCard.get(), paymentRequest);
        if (paymentResponse.isEmpty()) {
            deferredPaymentResponse.setResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } else {
            deferredPaymentResponse.setResult(ResponseEntity.ok(paymentResponse.get()));
        }
        return deferredPaymentResponse;
    }

    /**
     * Card Operations.
     **/

    @GetMapping("/card/merchant/{merchantId}/user/{userId}/card/{cardId}")
    public ResponseEntity<CardResponse> getCard(final @PathVariable String merchantId,
                                                final @PathVariable String userId,
                                                final @PathVariable Integer cardId) {
        // TODO: Add Authorization check based on the merchant and user.
        Optional<Card> card = paymentGatewayService.getCardById(cardId);
        if (card.isEmpty()) {
            log.error("Could not find card with id {}", cardId);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(toCardResponse(card.get()));
    }

    @GetMapping("/card/merchant/{merchantId}/user/{userId}")
    public ResponseEntity<List<CardResponse>> getAllCardsByMerchantAndUser(final @PathVariable String merchantId,
                                                                           final @PathVariable String userId) {
        // TODO: Add Authorization check based on the merchant and user.
        return ResponseEntity.ok(paymentGatewayService.getAllCardsByMerchantAndUser(merchantId, userId));
    }

    @PostMapping("/card/merchant/{merchantId}/user/{userId}")
    public ResponseEntity<CardResponse> saveCard(final @PathVariable String merchantId,
                                                 final @PathVariable String userId,
                                                 final @RequestBody CardRequest cardRequest) {
        // TODO: Add Authorization check based on the merchant and user.
        return ResponseEntity.ok(paymentGatewayService.storeCard(merchantId, userId, cardRequest));
    }

    /**
     * Get All Endpoints for Manual Testing.
     **/

    // TODO: Only for Testing purposes, should be removed for production.
    @GetMapping("/card")
    public ResponseEntity<List<CardResponse>> getAllCards() {
        // TODO: Add Authorization check based on the merchant and user.
        return ResponseEntity.ok(paymentGatewayService.getAllCards());
    }

    // TODO: Only for Testing purposes, should be removed for production.
    @GetMapping("/payment")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        // TODO: Add Authorization check based on the merchant and user.
        return ResponseEntity.ok(paymentGatewayService.getAllPayments());
    }
}
