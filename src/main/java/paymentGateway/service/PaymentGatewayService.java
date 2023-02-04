package paymentGateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import paymentGateway.model.*;
import paymentGateway.repository.CardRepository;
import paymentGateway.repository.PaymentRepository;
import paymentGateway.transformer.Transformer;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static paymentGateway.transformer.Transformer.*;


@Slf4j
public class PaymentGatewayService {
    private final static String MOCK_BANK_URI = "http://localhost:8080/mockBank/processPayment"; // TODO: https should be used here instead.
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public Optional<Card> getCardById(final Integer cardId) {
        return cardRepository.findById(cardId);
    }

    public List<CardResponse> getAllCards() {
        return cardRepository.findAll().stream().map(Transformer::toCardResponse).toList();
    }

    public List<CardResponse> getAllCardsByMerchantAndUser(final String merchantId,
                                                           final String userId) {
        return cardRepository.findByMerchantIdAndUserId(merchantId, userId).stream().map(Transformer::toCardResponse).toList();
    }

    public CardResponse storeCard(final String merchantId,
                                  final String userId,
                                  final CardRequest cardRequest) {
        return toCardResponse(cardRepository.save(toCard(cardRequest, merchantId, userId)));
    }

    public Optional<PaymentResponse> getPaymentById(final Integer paymentId) {
        Optional<Payment> paymentResponse = paymentRepository.findById(paymentId);
        if (paymentResponse.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toPaymentResponse(paymentResponse.get()));
    }

    public List<PaymentResponse> getAllPaymentsByMerchantAndUser(final String merchantId,
                                                                 final String userId) {
        return paymentRepository.findByMerchantIdAndUserId(merchantId, userId).stream().map(Transformer::toPaymentResponse).toList();
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream().map(Transformer::toPaymentResponse).toList();
    }

    @Transactional
    public Optional<PaymentResponse> storeAndProcessPayment(final String merchantId,
                                                            final String userId,
                                                            final Card card,
                                                            final PaymentRequest paymentRequest) {
        // Store the payment and generate payment ID.
        final Payment payment = paymentRepository.save(toPayment(paymentRequest, merchantId, userId, PaymentStatus.CREATED));
        // Setting the createdAt and updateAt timestamps for the transactional record.
        payment.setCreatedAt(new Timestamp(new Date().getTime()));
        payment.setUpdatedAt(new Timestamp(new Date().getTime()));

        // Process the payment
        try {
            log.info("Processing payment with PaymentId {} for user {} using {}", payment.getPaymentId(), userId, paymentRequest.getCardId());
            ResponseEntity<String> mockBankPaymentResponse = processPaymentThoughMockBank(payment.getPaymentId(), card, paymentRequest).get();
            PaymentStatus updatedPaymentStatus = objectMapper.readValue(mockBankPaymentResponse.getBody(), MockBankPaymentResponse.class).getPaymentStatus();
            log.info("Updating payment's status with PaymentId {}. New status: {}", payment.getPaymentId(), updatedPaymentStatus);
            // Update the payment status in the DB.
            payment.setPaymentStatus(updatedPaymentStatus);
            final Payment updatedPayment = paymentRepository.save(payment);
            log.info("Successfully stored updated payment transaction");
            return Optional.of(toPaymentResponse(updatedPayment));
        } catch (final InterruptedException | ExecutionException | JsonProcessingException exception) {
            log.error("Exception while processing payment with PaymentId {} for user {} using {}", payment.getPaymentId(), userId, paymentRequest.getCardId());
            return Optional.empty();
        }
    }

    private CompletableFuture<ResponseEntity<String>> processPaymentThoughMockBank(final Integer paymentId, final Card paymentCard, final PaymentRequest paymentRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        MockBankPaymentRequest mockBankPaymentRequest = toMockBankPaymentRequest(paymentCard, paymentRequest, paymentId);
        HttpEntity<MockBankPaymentRequest> postRequest = new HttpEntity<>(mockBankPaymentRequest, headers);

        return CompletableFuture.supplyAsync(() -> restTemplate.postForEntity(MOCK_BANK_URI, postRequest, String.class));
    }
}
