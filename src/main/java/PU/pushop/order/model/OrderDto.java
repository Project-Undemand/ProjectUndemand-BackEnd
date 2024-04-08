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
    private String postCode;
    private String address;
    private String detailAddress;
    private String ordererName;
    private String phoneNumber;
    PayMethod payMethod;

    public OrderDto(Orders order) {
        this(
                order.getPostCode(),
                order.getAddress(),
                order.getDetailAddress(),
                order.getOrdererName(),
                order.getPhoneNumber(),
                order.getPayMethod()
        );
    }

    public static Orders RequestForm(OrderDto request){
        Orders orders = new Orders();

        orders.setPostCode(request.getPostCode());
        orders.setAddress(request.getAddress());
        orders.setDetailAddress(request.getDetailAddress());
        orders.setOrdererName(request.getOrdererName());
        orders.setPhoneNumber(request.getPhoneNumber());
        orders.setPayMethod(request.getPayMethod());

        return orders;
    }

}
