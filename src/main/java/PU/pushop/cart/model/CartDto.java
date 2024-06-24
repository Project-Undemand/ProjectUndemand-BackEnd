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
    private String color;
    private String size;
    private Integer productPrice;
    private String productInfo;
    private Boolean isDiscount;
    private Boolean isSoldOut;
    private Long quantity;
    private Long totalPrice;
    private String productThumbnail;

    public CartDto(Cart cart) {
        this(
                cart.getCartId(),
                cart.getMember().getId(),
                cart.getMember().getEmail(),
                cart.getMember().getPhone(),
                cart.getMember().getJoinedAt(),
                cart.getMember().getWishLists(),
                cart.getProductManagement().getProduct().getProductId(),
                cart.getProductManagement().getProduct().getProductType(),
                cart.getProductManagement().getProduct().getProductName(),
                cart.getProductManagement().getColor().getColor(),
                cart.getProductManagement().getSize().toString(),
                cart.getProductManagement().getProduct().getPrice(),
                cart.getProductManagement().getProduct().getProductInfo(),
                cart.getProductManagement().getProduct().getIsDiscount(),
                cart.getProductManagement().isSoldOut(),
                cart.getQuantity(),
                cart.getPrice(),
                cart.getProductManagement().getProduct().getProductThumbnails().get(0).getImagePath()
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
                .productId(cart.getProductManagement().getProduct().getProductId())
                .productType(cart.getProductManagement().getProduct().getProductType())
                .productName(cart.getProductManagement().getProduct().getProductName())
                .color(cart.getProductManagement().getColor().getColor())
                .size(cart.getProductManagement().getSize().toString())
                .productPrice(cart.getProductManagement().getProduct().getPrice())
                .productInfo(cart.getProductManagement().getProduct().getProductInfo())
                .isDiscount(cart.getProductManagement().getProduct().getIsDiscount())
                .isSoldOut(cart.getProductManagement().isSoldOut())
                .quantity(cart.getQuantity())
                .totalPrice(cart.getPrice())
                .productThumbnail(cart.getProductManagement().getProduct().getProductThumbnails().get(0).getImagePath())
                .build();
    }



}
