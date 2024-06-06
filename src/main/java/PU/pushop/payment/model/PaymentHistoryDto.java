package PU.pushop.payment.model;

import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.payment.entity.Status;
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
    private String imagePath;
    private String merchantUid; // 주문번호
    private String ordererName;
    private String phoneNumber;
    private String product;
    private String option;
    private Integer productPrice;
    private boolean isDiscount;
    private String buyerAddr;
    private LocalDateTime orderedAt;
    private Integer totalPrice;
    private String payMethod;
    private LocalDateTime paiedAt;
    private Status statusType; // 취소여부
    private Boolean review;


    public PaymentHistoryDto(PaymentHistory paymentHistory) {
        this(
                paymentHistory.getId(),
                paymentHistory.getMember().getId(),
                paymentHistory.getOrders().getOrderId(),
                // product FirstThumbnailImagePath 를 추가.
                paymentHistory.getFirstThumbnailImagePath(),
                paymentHistory.getOrders().getMerchantUid(),
                paymentHistory.getOrders().getOrdererName(),
                paymentHistory.getOrders().getPhoneNumber(),
                paymentHistory.getProduct().getProductName(),
                paymentHistory.getProductOption(),
                paymentHistory.getProduct().getPrice(),
                paymentHistory.getProduct().getIsDiscount(),
                paymentHistory.getBuyerAddr(),
                paymentHistory.getOrders().getOrderDay(),
                paymentHistory.getTotalPrice(),
                paymentHistory.getPayMethod(),
                paymentHistory.getPaidAt(),
                paymentHistory.getStatusType(),
                paymentHistory.getReview()
        );
    }

}
