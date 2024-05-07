package PU.pushop.global.Exception;

public class PaymentCancelFailureException extends RuntimeException  {

    public PaymentCancelFailureException(String message) {
        super(message);
    }
}
