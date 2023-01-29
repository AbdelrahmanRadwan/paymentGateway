package paymentGateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymentGateway.model.*;
import paymentGateway.repository.CardRepository;
import paymentGateway.repository.PaymentRepository;

import java.util.List;

import static paymentGateway.converter.cardConverter.*;


@RestController
@RequestMapping("/api")
public class PaymentGateway {

    @Autowired
    CardRepository cardRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @GetMapping("/payment/merchant/{merchantId}/user/{userId}")
    public ResponseEntity<List<Card>> getAllPaymentsByMerchantAndUser(final @PathVariable String merchantId,
                                                                   final @PathVariable String userId) {
        return ResponseEntity.ok(paymentRepository.findByMerchantIdAndUserId(merchantId, userId));
    }

    @PostMapping("/payment/merchant/{merchantId}/user/{userId}")
    public ResponseEntity<PaymentResponse> savePayment(final @PathVariable String merchantId,
                                                       final @PathVariable String userId,
                                                       final @RequestBody PaymentRequest paymentRequest) {

        PaymentResponse paymentResponse = toPaymentResponse(paymentRepository.save(toPayment(paymentRequest, merchantId, userId)));
        return ResponseEntity.ok(paymentResponse);
    }

    @GetMapping("/card/merchant/{merchantId}/user/{userId}")
    public ResponseEntity<List<Card>> getAllCardsByMerchantAndUser(final @PathVariable String merchantId,
                                                                   final @PathVariable String userId) {
        return ResponseEntity.ok(cardRepository.findByMerchantIdAndUserId(merchantId, userId));
    }

    @PostMapping("/card/merchant/{merchantId}/user/{userId}")
    public ResponseEntity<CardResponse> saveCard(final @PathVariable String merchantId,
                                                 final @PathVariable String userId,
                                                 final @RequestBody CardRequest cardRequest) {
        CardResponse cardResponse = toCardResponse(cardRepository.save(toCard(cardRequest, merchantId, userId)));
        return ResponseEntity.ok(cardResponse);
    }

    // TODO: Only for Testing purposes, should be removed for production.
    @GetMapping("/card")
    public ResponseEntity<List<Card>> getAllCards() {
        return ResponseEntity.ok(cardRepository.findAll());
    }

    // TODO: Only for Testing purposes, should be removed for production.
    @GetMapping("/payment")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }
}
