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
    // 고유 id
    private Long paymentId;
    private Long memberId;
    private Long orderId;
    // Product 관련 field
    private Long productId;
    private String productName;
    private String imagePath;
    private Integer productPrice;
    private Long productQuantity;
    private String option;
    // Order 관련 field
    private String merchantUid; // 주문번호
    private String ordererName;
    private String phoneNumber;
    private boolean isDiscount;
    private String buyerAddr;
    private LocalDateTime orderedAt;
    private Integer totalPrice;
    private String payMethod;
    private String bankName;
    private LocalDateTime paiedAt;
    private Status statusType; // 취소여부
    private Boolean review;


    public PaymentHistoryDto(PaymentHistory paymentHistory) {

        this(
                paymentHistory.getId(),
                paymentHistory.getMember().getId(),
                paymentHistory.getOrders().getOrderId(),
                // product field
                paymentHistory.getProduct().getProductId(),
                paymentHistory.getProduct().getProductName(),
                paymentHistory.getFirstThumbnailImagePath(),
                paymentHistory.getProduct().getPrice(),
                paymentHistory.getQuantity(),
                paymentHistory.getProductOption(),
                // payment field
                paymentHistory.getOrders().getMerchantUid(),
                paymentHistory.getOrders().getOrdererName(),
                paymentHistory.getOrders().getPhoneNumber(),
                paymentHistory.getProduct().getIsDiscount(),
                paymentHistory.getBuyerAddr(),
                paymentHistory.getOrders().getOrderDay(),
                paymentHistory.getTotalPrice(),
                paymentHistory.getPayMethod(),
                paymentHistory.getBankName(),
                paymentHistory.getPaidAt(),
                paymentHistory.getStatusType(),
                paymentHistory.getReview()
        );
    }

}
