package PU.pushop.cart.service;

import PU.pushop.cart.entity.Cart;
import PU.pushop.cart.model.CartDto;
import PU.pushop.cart.model.CartRequestDto;
import PU.pushop.cart.repository.CartRepository;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.repository.ProductManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static PU.pushop.global.ResponseMessageConstants.*;
import static PU.pushop.global.authorization.MemberAuthorizationUtil.verifyUserIdMatch;

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
                .orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND));
        ProductManagement productMgt = productManagementRepository.findById(productMgtId)
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND + " productMgtId: " + productMgtId));

        verifyUserIdMatch(request.getMemberId()); // 로그인 된 사용자와 요청 사용자 비교


        Cart existingCart = cartRepository.findByProductManagementAndMember(productMgt, member).orElse(null);

        if (existingCart != null) { // 이미 담은 상품과 옵션인 경우 수량, 가격을 수정

            existingCart.setQuantity(existingCart.getQuantity() + request.getQuantity()); // 현재 수량 + 담은 수량

            existingCart.setPrice(existingCart.getPrice() + productMgt.getProduct().getPrice() * request.getQuantity()); // 현재 가격 + 담은 가격


            cartRepository.save(existingCart);
            return existingCart.getCartId();

        } else {
            Long price = productMgt.getProduct().getPrice() * request.getQuantity();
            Cart cart = new Cart(member, productMgt, request.getQuantity(), price);

            cartRepository.save(cart);
            return cart.getCartId();

        }

    }

    /**
     * 유저의 전체 장바구니 리스트 조회
     * @return
     */
    public List<CartDto> allCarts(Long memberId) {
        verifyUserIdMatch(memberId); // 로그인 된 사용자와 요청 사용자 비교

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
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));

        verifyUserIdMatch(existingCart.getMember().getId()); // 로그인 된 사용자와 요청 사용자 비교

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
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));

        verifyUserIdMatch(cart.getMember().getId()); // 로그인 된 사용자와 요청 사용자 비교

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
