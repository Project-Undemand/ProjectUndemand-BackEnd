package PU.pushop.cart.service;

import PU.pushop.cart.entity.Cart;
import PU.pushop.cart.model.CartDto;
import PU.pushop.cart.model.CartRequestDto;
import PU.pushop.cart.repository.CartRepository;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.repository.ProductManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    public final ProductRepositoryV1 productRepository;
    public final MemberRepositoryV1 memberRepository;
    public final ProductManagementRepository productManagementRepository;


    /**
     * 장바구니 담기
     * @param request
     * @param productMgtId
     * @return
     */
    public Long addCart(CartRequestDto request, Long productMgtId) { // 0408 수정 productId -> productMgtId

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다"));
        ProductManagement productMgt = productManagementRepository.findById(productMgtId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다. productMgtId: " + productMgtId));

        Long price = productMgt.getProduct().getPrice() * request.getQuantity();

        Cart cart = new Cart();
        cart.setProductManagement(productMgt);
        cart.setMember(member);
        cart.setQuantity(request.getQuantity());
        cart.setPrice(price);

        cartRepository.save(cart);

        return cart.getCartId();
    }

    /**
     * 유저의 전체 장바구니 리스트 조회
     * @return
     */
    public List<CartDto> allCarts(Long memberId) {
        List<Cart> carts = cartRepository.findByMemberId(memberId);
        return carts.stream()
                .map(CartDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 장바구니 수정 (상품 갯수 수정 -> 가격 변경)
     * @param cartId
     * @param updatedCart
     * @return
     */
    public Cart updateCart(Long cartId, Cart updatedCart) {
        Cart existingCart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

        existingCart.setQuantity(updatedCart.getQuantity());
        Long price = existingCart.getProductManagement().getProduct().getPrice() * updatedCart.getQuantity();
        existingCart.setPrice(price);

        return cartRepository.save(existingCart);
    }

    /**
     * 장바구니 삭제
     * @param cartId
     */
    public void deleteCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

        cartRepository.delete(cart);
    }

    /**
     * 여러 장바구니 한 번에 삭제
     * @param cartIds - 여러 cart 의 cartId 를 리스트로
     */
/*    public void deleteCartList(List<Long> cartIds) {
        System.out.println("Type of service cartIds: " + cartIds.getClass().getSimpleName()); // cartIds의 데이터 형식 출력

        for (Long cartId : cartIds) {

            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
            cartRepository.delete(cart);
        }
    }*/


}
