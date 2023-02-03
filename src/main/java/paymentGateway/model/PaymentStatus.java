package paymentGateway.model;

public enum PaymentStatus {
    CREATED,
    PROCESSING,
    SUCCESSFUL,
    FAILED,
    REPROCESSING,
    REFUNDED,
}
