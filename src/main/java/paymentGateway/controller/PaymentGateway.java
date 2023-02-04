package paymentGateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import paymentGateway.model.*;

import paymentGateway.transformer.Transformer;

import paymentGateway.repository.CardRepository;
import paymentGateway.repository.PaymentRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static paymentGateway.transformer.Transformer.toCardResponse;
import static paymentGateway.transformer.Transformer.toCard;
import static paymentGateway.transformer.Transformer.toPayment;
import static paymentGateway.transformer.Transformer.toPaymentResponse;
import static paymentGateway.transformer.Transformer.toMockBankPaymentRequest;


@Slf4j
@RestController
@RequestMapping("/api")
public class PaymentGateway {
    private final static String MOCK_BANK_URI = "http://localhost:8080/mockBank/processPayment";

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Payment Operations.
     **/

    @GetMapping("/payment/merchant/{merchantId}/user/{userId}/payment/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(final @PathVariable String merchantId,
                                                      final @PathVariable String userId,
                                                      final @PathVariable Integer paymentId) {
        // TODO: Add Authorization check based on the merchant and user.
        Optional<Payment> paymentResponses = paymentRepository.findById(paymentId);
        if (paymentResponses.isEmpty()) {
            log.error("Could not find payment with id {}", paymentId);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(toPaymentResponse(paymentResponses.get()));
    }

    @GetMapping("/payment/merchant/{merchantId}/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getAllPaymentsByMerchantAndUser(final @PathVariable String merchantId,
                                                                                 final @PathVariable String userId) {
        // TODO: Add Authorization check based on the merchant and user.
        List<PaymentResponse> paymentResponses = paymentRepository.findByMerchantIdAndUserId(merchantId, userId).stream().map(Transformer::toPaymentResponse).toList();
        return ResponseEntity.ok(paymentResponses);
    }

    @PostMapping("/payment/merchant/{merchantId}/user/{userId}")
    public DeferredResult<ResponseEntity<PaymentResponse>> processPayment(final @PathVariable String merchantId,
                                                                          final @PathVariable String userId,
                                                                          final @RequestBody PaymentRequest paymentRequest) {
        DeferredResult<ResponseEntity<PaymentResponse>> deferredPaymentResponse = new DeferredResult<>();
        // Fetch Payment Card.
        Optional<Card> usedCard = cardRepository.findById(paymentRequest.getCardId());
        if (usedCard.isEmpty()) {
            log.error("Could not find card with id {}", paymentRequest.getCardId());
            deferredPaymentResponse.setResult(ResponseEntity.badRequest().build());
            return deferredPaymentResponse;
        }

        // Store the payment and generate payment ID.
        final Payment storedPayment = paymentRepository.save(toPayment(paymentRequest, merchantId, userId, PaymentStatus.CREATED));

        // Process the payment
        log.info("Processing payment with PaymentId {} for user {} using {}", storedPayment.getPaymentId(), userId, paymentRequest.getCardId());
        CompletableFuture.runAsync(() -> {
            try {
                ResponseEntity<String> mockBankPaymentResponse = processPaymentThoughMockBank(usedCard.get(), paymentRequest, storedPayment.getPaymentId()).get();
                PaymentStatus updatedPaymentStatus = objectMapper.readValue(mockBankPaymentResponse.getBody(), MockBankPaymentResponse.class).getPaymentStatus();
                log.info("Updating payment's status with PaymentId {} to {}", storedPayment.getPaymentId(), updatedPaymentStatus);
                // Update the payment status in the DB.
                storedPayment.setPaymentStatus(updatedPaymentStatus);
                paymentRepository.save(storedPayment);
                deferredPaymentResponse.setResult(ResponseEntity.ok(toPaymentResponse(storedPayment)));
            } catch (final InterruptedException | ExecutionException | JsonProcessingException exception) {
                log.error("Error while processing payment with PaymentId {}", storedPayment.getPaymentId(), exception);
                deferredPaymentResponse.setResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            }
        });

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
        Optional<Card> cardInfo = cardRepository.findById(cardId);
        if (cardInfo.isEmpty()) {
            log.error("Could not find card with id {}", cardId);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(toCardResponse(cardInfo.get()));
    }

    @GetMapping("/card/merchant/{merchantId}/user/{userId}")
    public ResponseEntity<List<CardResponse>> getAllCardsByMerchantAndUser(final @PathVariable String merchantId,
                                                                           final @PathVariable String userId) {
        // TODO: Add Authorization check based on the merchant and user.
        List<CardResponse> cardResponses = cardRepository.findByMerchantIdAndUserId(merchantId, userId).stream().map(Transformer::toCardResponse).toList();
        return ResponseEntity.ok(cardResponses);
    }

    @PostMapping("/card/merchant/{merchantId}/user/{userId}")
    public ResponseEntity<CardResponse> saveCard(final @PathVariable String merchantId,
                                                 final @PathVariable String userId,
                                                 final @RequestBody CardRequest cardRequest) {
        // TODO: Add Authorization check based on the merchant and user.
        CardResponse cardResponse = toCardResponse(cardRepository.save(toCard(cardRequest, merchantId, userId)));
        return ResponseEntity.ok(cardResponse);
    }

    /**
     * Get All Endpoints for Manual Testing.
     **/

    // TODO: Only for Testing purposes, should be removed for production.
    @GetMapping("/card")
    public ResponseEntity<List<CardResponse>> getAllCards() {
        // TODO: Add Authorization check based on the merchant and user.
        List<CardResponse> cardResponses = cardRepository.findAll().stream().map(Transformer::toCardResponse).toList();
        return ResponseEntity.ok(cardResponses);
    }

    // TODO: Only for Testing purposes, should be removed for production.
    @GetMapping("/payment")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        // TODO: Add Authorization check based on the merchant and user.
        List<PaymentResponse> paymentResponses = paymentRepository.findAll().stream().map(Transformer::toPaymentResponse).toList();
        return ResponseEntity.ok(paymentResponses);
    }

    private CompletableFuture<ResponseEntity<String>> processPaymentThoughMockBank(final Card paymentCard, final PaymentRequest paymentRequest, final Integer paymentId) {
        MockBankPaymentRequest mockBankPaymentRequest = toMockBankPaymentRequest(paymentCard, paymentRequest, paymentId);
        HttpEntity<MockBankPaymentRequest> postRequest = new HttpEntity<>(mockBankPaymentRequest);
        return CompletableFuture.supplyAsync(() -> restTemplate.postForEntity(MOCK_BANK_URI, postRequest, String.class));
    }
}
