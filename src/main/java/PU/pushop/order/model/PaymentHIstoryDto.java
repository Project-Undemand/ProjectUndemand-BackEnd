package PU.pushop.order.model;

import PU.pushop.order.entity.PaymentHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHIstoryDto {
    private Long id;
    private String buyerName;
    private String postCode;
    private String phone;
    private String merchantUid;
    private String productName;
    private Long price;
    private LocalDate paidAt;
    private String payMethod;
    private String receiptUrl;
    private String status;
    private String paymentUid;

    public PaymentHIstoryDto(PaymentHistory paymentHistory) {
        this(
                paymentHistory.getId(),
                paymentHistory.getBuyerName(),
                paymentHistory.getPostCode(),
                paymentHistory.getPhone(),
                paymentHistory.getMerchantUid(),
                paymentHistory.getProductName(),
                paymentHistory.getPrice(),
                paymentHistory.getPaidAt(),
                paymentHistory.getPayMethod(),
                paymentHistory.getReceiptUrl(),
                paymentHistory.getStatus(),
                paymentHistory.getPaymentUid()
        );
    }
}
