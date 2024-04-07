package PU.pushop.cart.model;

import PU.pushop.cart.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Long cartId;
    private Long memberId;
    private Long productId;
    private Long quantity;
    private Long price;

    public CartDto(Cart cart) {
        this(
                cart.getCartId(),
                cart.getMember().getId(),
                cart.getProduct().getProductId(),
                cart.getQuantity(),
                cart.getPrice()
        );
    }

}
