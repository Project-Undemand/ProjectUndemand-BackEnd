package PU.pushop.order.controller;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.order.entity.Cart;
import PU.pushop.order.service.CartService;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.Path;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final MemberRepositoryV1 memberRepository;
    private final ProductRepositoryV1 productRepository;

    @Data
    static class Request {
        private Long memberId;
        private Product product;
        private Long quantity;
        private Long price;
    }

    private Cart RequestForm(Request request, Long productId) {
        Cart cart = new Cart();
        Member member = memberRepository.findById(request.getMemberId()).orElse(null);
        Product product = productRepository.findByProductId(productId).orElse(null);

        Long price = product.getPrice() * request.getQuantity();

        cart.setMember(member);
        cart.setProduct(request.getProduct());
        cart.setQuantity(request.getQuantity());
        cart.setPrice(price);


        return cart;
    }

    /**
     * 장바구니 담기
     *
     * @param request
     * @param productId
     * @return
     */
    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addCart(@Valid @RequestBody Request request, @PathVariable Long productId) {
        Cart cart = RequestForm(request, productId);
        Long createdId = cartService.addCart(cart, productId);

        return ResponseEntity.ok(createdId);
    }

    /**
     * 내 장바구니 리스트
     *
     * @param memberId
     * @return
     */
    @GetMapping("/{memberId}")
    public List<Cart> getMyCarts(@PathVariable Long memberId) {
        return cartService.allCarts(memberId);
    }

    @Data
    static class UpdateRequest {
        private Long quantity;
    }

    private Cart UpdateRequestForm(UpdateRequest request) {
        Cart cart = new Cart();
        cart.setQuantity(request.getQuantity());

        return cart;
    }

    /**
     * 장바구니 수정(상품 갯수 수정)
     *
     * @param cartId
     * @param request
     * @return
     */
    @PutMapping("/{cartId}")
    public ResponseEntity<?> updateCart(@PathVariable Long cartId, @Valid @RequestBody UpdateRequest request) {
        Cart updatedCart = UpdateRequestForm(request);
        Cart updated = cartService.updateCart(cartId, updatedCart);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCart(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);
        return ResponseEntity.ok().build();
    }
}
