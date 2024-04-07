package PU.pushop.payment.model;

import PU.pushop.payment.entity.PaymentHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryDto {
    private Long memberId;
    private Long orderId;
    private Integer price;
    private Long productId;

    public PaymentHistoryDto(PaymentHistory paymentHistory) {
        this(
                paymentHistory.getMember().getId(),
                paymentHistory.getOrders().getOrderId(),
                paymentHistory.getPrice(),
                paymentHistory.getProduct().getProductId()
        );
    }
}
