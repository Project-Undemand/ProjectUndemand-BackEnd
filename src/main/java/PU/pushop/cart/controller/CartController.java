package PU.pushop.cart.controller;

import PU.pushop.cart.model.CartDto;
import PU.pushop.cart.model.CartRequestDto;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.cart.entity.Cart;
import PU.pushop.cart.service.CartService;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    /**
     * 장바구니 담기
     * @param request
     * @param productId
     * @return
     */
    @PostMapping("/add/{inventoryId}")
    public ResponseEntity<?> addCart(@Valid @RequestBody CartRequestDto request, @PathVariable Long inventoryId) {

        Long createdId = cartService.addCart(request, inventoryId);

        return ResponseEntity.ok("장바구니에 등록되었습니다. cart_id : " + createdId);
    }

    /**
     * 내 장바구니 리스트
     * @param memberId
     * @return
     */
    @GetMapping("/{memberId}")
    public List<CartDto> getMyCarts(@PathVariable Long memberId) {
        return cartService.allCarts(memberId);
    }

    /**
     * 장바구니 수정(상품 갯수 수정)
     * @param cartId
     * @param request
     * @return
     */
    @PutMapping("/{cartId}")
    public ResponseEntity<?> updateCart(@PathVariable Long cartId, @Valid @RequestBody CartRequestDto request) {
        Cart updatedCart = CartRequestDto.updateRequestForm(request);
        CartDto updatedCartDto = new CartDto(cartService.updateCart(cartId, updatedCart));

        return ResponseEntity.ok(updatedCartDto);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCart(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);
        return ResponseEntity.ok("삭제되었습니다");
    }

/*    @PostMapping("")
    public ResponseEntity<?> deleteCartList(@RequestBody Map<String, Object> payload) {
        List<Long> cartIds = (List<Long>) payload.get("cartIds");
        cartService.deleteCartList(cartIds);
        return ResponseEntity.ok().build();

    }*/

}
