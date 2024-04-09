package PU.pushop.payment.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PaymentRequestDto {
    private Long memberId;
    private Long orderId;
    private Long price;
    private List<Long> productIdList;

    public PaymentRequestDto(Long memberId, Long orderId, Long price, List<Long> productIdList) {
        this.memberId = memberId;
        this.orderId = orderId;
        this.price = price;
        this.productIdList = productIdList;
    }
}
