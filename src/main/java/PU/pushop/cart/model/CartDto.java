package PU.pushop.cart.model;

import PU.pushop.cart.entity.Cart;
import PU.pushop.members.entity.Member;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.wishList.entity.WishList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDto {
    private Long cartId;
    private Long memberId;
    private String memberEmail;
    private String memberPhone;
    private LocalDateTime memberJoinedAt;
    private List<WishList> wishLists;
    private Long productId;
    private ProductType productType;
    private String productName;
    private Integer productPrice;
    private String productInfo;
    private Boolean isSale;
    private Long quantity;
    private Long totalPrice;

    public CartDto(Cart cart) {
        this(
                cart.getCartId(),
                cart.getMember().getId(),
                cart.getMember().getEmail(),
                cart.getMember().getPhone(),
                cart.getMember().getJoinedAt(),
                cart.getMember().getWishLists(),
                cart.getProduct().getProductId(),
                cart.getProduct().getProductType(),
                cart.getProduct().getProductName(),
                cart.getProduct().getPrice(),
                cart.getProduct().getProductInfo(),
                cart.getProduct().getIsSale(),
                cart.getQuantity(),
                cart.getPrice()
        );
    }

    public static CartDto fromEntity(Cart cart) {
        return CartDto.builder()
                .cartId(cart.getCartId())
                .memberId(cart.getMember().getId())
                .memberEmail(cart.getMember().getEmail())
                .memberPhone(cart.getMember().getPhone())
                .memberJoinedAt(cart.getMember().getJoinedAt())
                .wishLists(cart.getMember().getWishLists())
                .productId(cart.getProduct().getProductId())
                .productType(cart.getProduct().getProductType())
                .productName(cart.getProduct().getProductName())
                .productPrice(cart.getProduct().getPrice())
                .productInfo(cart.getProduct().getProductInfo())
                .isSale(cart.getProduct().getIsSale())
                .quantity(cart.getQuantity())
                .totalPrice(cart.getPrice())
                .build();
    }



}
