package paymentGateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import paymentGateway.converter.cardConverter;
import paymentGateway.model.*;
import paymentGateway.repository.CardRepository;
import paymentGateway.repository.PaymentRepository;

import java.util.List;
import java.util.Optional;

import static paymentGateway.converter.cardConverter.*;
import static paymentGateway.converter.cardConverter.toCardResponse;

@Slf4j
@RestController
@RequestMapping("/api")
public class PaymentGateway {
    private final static String PAYMENT_STATUS_INITIALIZED = "INITIALIZED";
    private final static String URI = "http://localhost:8080/mockBank/processPayment";

    @Autowired
    CardRepository cardRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/payment/merchant/{merchantId}/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getAllPaymentsByMerchantAndUser(final @PathVariable String merchantId,
                                                                                 final @PathVariable String userId) {
        List<PaymentResponse> paymentResponses = paymentRepository.findByMerchantIdAndUserId(merchantId, userId).stream().map(cardConverter::toPaymentResponse).toList();
        return ResponseEntity.ok(paymentResponses);
    }

    @PostMapping("/payment/merchant/{merchantId}/user/{userId}")
    public ResponseEntity<PaymentResponse> processPayment(final @PathVariable String merchantId,
                                                          final @PathVariable String userId,
                                                          final @RequestBody PaymentRequest paymentRequest) {
        // Fetch Payment Card.
        Optional<Card> usedCard = cardRepository.findById(paymentRequest.getCardId());
        if(usedCard.isEmpty()) {
            log.error("Could not find card with id {}", paymentRequest.getCardId());
            return ResponseEntity.badRequest().build();
        }
        // Store the payment and generate payment ID
        Payment storedPayment = paymentRepository.save(toPayment(paymentRequest, merchantId, userId, PAYMENT_STATUS_INITIALIZED));
        PaymentResponse paymentResponse = toPaymentResponse(storedPayment);
        // Process the payment
        log.info("Processing payment with id {} for user {}", paymentResponse.getPaymentId(),userId);
        try {
            String processPaymentResponseNewPaymentStatus = ProcessPaymentThoughMockBank(usedCard.get(), paymentRequest, paymentResponse.getPaymentId()).getPaymentStatus();
            // update the payment status
            storedPayment.setPaymentStatus(processPaymentResponseNewPaymentStatus);
            paymentResponse = toPaymentResponse(storedPayment);
        } catch (final JsonProcessingException e) {
            log.warn("Can't parse response from mockBank: {}", e.getMessage());
        }
        return ResponseEntity.ok(paymentResponse);
    }

    @GetMapping("/card/merchant/{merchantId}/user/{userId}")
    public ResponseEntity<List<CardResponse>> getAllCardsByMerchantAndUser(final @PathVariable String merchantId,
                                                                           final @PathVariable String userId) {
        List<CardResponse> cardResponses = cardRepository.findByMerchantIdAndUserId(merchantId, userId).stream().map(cardConverter::toCardResponse).toList();
        return ResponseEntity.ok(cardResponses);
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
    public ResponseEntity<List<CardResponse>> getAllCards() {
        List<CardResponse> cardResponses = cardRepository.findAll().stream().map(cardConverter::toCardResponse).toList();
        return ResponseEntity.ok(cardResponses);
    }

    // TODO: Only for Testing purposes, should be removed for production.
    @GetMapping("/payment")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> paymentResponses = paymentRepository.findAll().stream().map(cardConverter::toPaymentResponse).toList();
        return ResponseEntity.ok(paymentResponses);
    }

    private ProcessPaymentResponse ProcessPaymentThoughMockBank(final Card paymentCard, final PaymentRequest paymentRequest, final Integer paymentId) throws JsonProcessingException {
        ProcessPaymentRequest processPaymentRequest = toProcessPaymentRequest(paymentCard, paymentRequest, paymentId);
        HttpEntity<ProcessPaymentRequest> postRequest = new HttpEntity<>(processPaymentRequest);
        // TODO: Return the response from the mock bank and use in the main method to update the payment record.
        ResponseEntity<String> response = this.restTemplate.postForEntity(URI, postRequest, String.class);

        return objectMapper.readValue(response.getBody(), ProcessPaymentResponse.class);
    }
}
