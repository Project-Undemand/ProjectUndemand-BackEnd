package PU.pushop.cart.model;

import PU.pushop.cart.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartRequestDto {
    private Long memberId;
    private Long quantity;

    public CartRequestDto(Cart cart) {
        this(
                cart.getMember().getId(),
                cart.getQuantity()
        );
    }
}
