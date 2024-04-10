package PU.pushop.payment.model;

import PU.pushop.payment.entity.PaymentHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryDto {
    private Long memberId;
    private Long orderId;
    private String product;
    private String option;
    private Integer productPrice;
    private LocalDate orderedAt;
    private Long totalPrice;
    private LocalDate payedAte;


    public PaymentHistoryDto(PaymentHistory paymentHistory) {
        this(
                paymentHistory.getMember().getId(),
                paymentHistory.getOrders().getOrderId(),
                paymentHistory.getProduct().getProductName(),
                paymentHistory.getOption(),
                paymentHistory.getProduct().getPrice(),
                paymentHistory.getOrders().getOrderDay(),
                paymentHistory.getTotalPrice(),
                paymentHistory.getPaidAt()
        );
    }
}
