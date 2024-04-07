package PU.pushop.order.model;

import PU.pushop.cart.entity.Cart;
import PU.pushop.order.entity.Orders;
import PU.pushop.order.entity.enums.PayMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long orderId;
    private Long memberId;
    private List<Cart> carts;
    private String ordererName;
    private String productName;
    PayMethod payMethod;
    private String merchantUid;
    private BigDecimal totalPrice;
    private String address;
    private String detailAddress;
    private String postCode;
    private String phoneNumber;
    private LocalDate orderDay;
    private Boolean paymentStatus;

    public OrderDto(Orders order) {
        this(
                order.getOrderId(),
                order.getMember().getId(),
                order.getCarts(),
                order.getOrdererName(),
                order.getProductName(),
                order.getPayMethod(),
                order.getMerchantUid(),
                order.getTotalPrice(),
                order.getAddress(),
                order.getDetailAddress(),
                order.getPostCode(),
                order.getPhoneNumber(),
                order.getOrderDay(),
                order.getPaymentStatus()
        );
    }

}
