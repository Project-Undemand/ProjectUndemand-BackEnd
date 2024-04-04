package PU.pushop.order.model;

import PU.pushop.order.entity.Order;
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
    private Long totalPrice;
    private String address;
    private Long phoneNumber;
    private LocalDate createdAt;

    public OrderDto(Order order) {
        this(
                order.getOrderId(),
                order.getMember().getId(),
                order.getCart().getCartId(),
                order.getTotalPrice(),
                order.getAddress(),
                order.getPhoneNumber(),
                order.getCreatedAt()
        );
    }
}
