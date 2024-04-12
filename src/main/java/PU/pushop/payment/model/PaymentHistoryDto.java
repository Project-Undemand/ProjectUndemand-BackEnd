package PU.pushop.payment.model;

import PU.pushop.payment.entity.PaymentHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryDto {
    private Long paymentId;
    private Long memberId;
    private Long orderId;
    private String product;
    private String option;
    private Integer productPrice;
    private LocalDateTime orderedAt;
    private Long totalPrice;
    private LocalDateTime payedAte;


    public PaymentHistoryDto(PaymentHistory paymentHistory) {
        this(
                paymentHistory.getId(),
                paymentHistory.getMember().getId(),
                paymentHistory.getOrders().getOrderId(),
                paymentHistory.getProduct().getProductName(),
                paymentHistory.getProductOption(),
                paymentHistory.getProduct().getPrice(),
                paymentHistory.getOrders().getOrderDay(),
                paymentHistory.getTotalPrice(),
                paymentHistory.getPaidAt()
        );
    }

}
