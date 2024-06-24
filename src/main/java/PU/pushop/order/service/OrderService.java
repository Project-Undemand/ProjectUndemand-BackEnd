package PU.pushop.order.service;


import PU.pushop.cart.entity.Cart;
import PU.pushop.cart.repository.CartRepository;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.order.entity.Orders;
import PU.pushop.order.model.OrderDto;
import PU.pushop.order.repository.OrderRepository;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.productManagement.entity.ProductManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static PU.pushop.global.ResponseMessageConstants.MEMBER_NOT_FOUND;
import static PU.pushop.global.authorization.MemberAuthorizationUtil.verifyUserIdMatch;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class OrderService {
    public final CartRepository cartRepository;
    public final OrderRepository orderRepository;
    public final ProductRepositoryV1 productRepository;
    public final MemberRepositoryV1 memberRepository;

    /**
     * 주문서 화면에 나타날 정보 (사용자에게 입력받지 않고 자동으로 가져와 화면에 띄워주거나 저장할 값)
     * @param cartIds card id 리스트
     * @return order 객체 반환
     */
    public Orders createOrder(List<Long> cartIds) {
        List<Cart> carts = cartRepository.findByCartIdIn(cartIds);

        Long memberId = carts.get(0).getMember().getId();
        verifyUserIdMatch(memberId); // 로그인 된 사용자와 요청 사용자 비교

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND));

        // 주문할 상품들
        List<ProductManagement> productMgts = new ArrayList<>();
        for (Cart cart : carts) {
            ProductManagement productMgt = cart.getProductManagement();
            productMgts.add(productMgt);
        }

        // 모든 장바구니의 memberID가 동일한지 확인
        boolean sameMember = carts.stream()
                .allMatch(cart -> cart.getMember().getId().equals(memberId));
        if (!sameMember || member == null) {
            // 동일하지 않거나 회원이 존재하지 않는 경우, 주문 생성 실패
            return null;
        }

        // 주문 반환
        return new Orders(member, productMgts,member.getUsername(),getProductNames(carts),calculateTotalPrice(carts),getMemberPhoneNumber(carts));

    }

    // 주문 상품 이름들을 가져오는 메서드
    private String getProductNames(List<Cart> carts) {
        StringBuilder productNamesBuilder = new StringBuilder();
        for (Cart cart : carts) {

            Long productId = cart.getProductManagement().getProduct().getProductId();
            Product product = productRepository.findById(productId).orElse(null);

            if (product != null) {
                if (!productNamesBuilder.isEmpty()) {
                    productNamesBuilder.append(", ");
                }
                productNamesBuilder.append(product.getProductName());
            }
        }
        return productNamesBuilder.toString();
    }

    // 회원 전화번호를 가져오는 메서드
    private String getMemberPhoneNumber(List<Cart> carts) {
        Long memberId = carts.get(0).getMember().getId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND));
        return (member != null && member.getPhone() != null) ? member.getPhone() : null;
    }

    // 총 가격을 계산하는 메서드
    private BigDecimal calculateTotalPrice(List<Cart> carts) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Cart cart : carts) {
            BigDecimal cartPrice = BigDecimal.valueOf(cart.getPrice());
            totalPrice = totalPrice.add(cartPrice);
        }
        return totalPrice;
    }

    /**
     * 주문 테이블 저장
     * @param temporaryOrder 세션에 저장된 주문서
     * @param orders 사용자에게 입력받은 주문 정보
     * @return 주문 테이블 저장
     */
    public Orders orderConfirm(Orders temporaryOrder, OrderDto orders) {
        verifyUserIdMatch(temporaryOrder.getMember().getId()); // 로그인 된 사용자와 요청 사용자 비교


        String merchantUid = generateMerchantUid(); //주문번호 생성

        // 세션 주문서와 사용자에게 입력받은 정보 합치기
        temporaryOrder.orderConfirm(merchantUid, orders);

        return orderRepository.save(temporaryOrder);
    }

    // 주문번호 생성 메서드
    private String generateMerchantUid() {
        // 현재 날짜와 시간을 포함한 고유한 문자열 생성
        String uniqueString = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDay = today.format(formatter).replace("-", "");

        // 무작위 문자열과 현재 날짜/시간을 조합하여 주문번호 생성
        return formattedDay +'-'+ uniqueString;
    }


}
