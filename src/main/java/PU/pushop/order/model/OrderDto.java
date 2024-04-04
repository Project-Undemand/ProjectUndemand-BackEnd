package PU.pushop.order.model;

import PU.pushop.order.entity.Order;
import PU.pushop.order.entity.enums.PayMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long orderId;
    private Long memberId;
    private Long cartId;
    private String ordererName;
    private String productName;
    PayMethod payMethod;
    private String impUid;
    private String merchantUid;
    private Long totalPrice;
    private String address;
    private String postCode;
    private Long phoneNumber;
    private LocalDate orderDay;

    public OrderDto(Order order) {
        this(
                order.getOrderId(),
                order.getMember().getId(),
                order.getCart().getCartId(),
                order.getOrdererName(),
                order.getProductName(),
                order.getPayMethod(),
                order.getImpUid(),
                order.getMerchantUid(),
                order.getTotalPrice(),
                order.getAddress(),
                order.getPostCode(),
                order.getPhoneNumber(),
                order.getOrderDay()
        );
    }
}
