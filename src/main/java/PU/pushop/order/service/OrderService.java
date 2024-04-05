package PU.pushop.order.service;


import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.order.entity.Cart;
import PU.pushop.order.entity.Orders;
import PU.pushop.order.repository.CartRepository;
import PU.pushop.order.repository.OrderRepository;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    public final CartRepository cartRepository;
    public final OrderRepository orderRepository;
    public final ProductRepositoryV1 productRepository;
    public final MemberRepositoryV1 memberRepository;

    /**
     * 주문서 화면에 나타날 정보
     * @param cartIds
     * @return
     */
    public Orders createOrder(List<Long> cartIds) {
        List<Cart> carts = cartRepository.findByCartIdIn(cartIds);

        List<Cart> cartslist = cartRepository.findAllById(cartIds);

        Long memberId = carts.get(0).getMember().getId();
        Member member = memberRepository.findById(memberId).orElse(null);

        // 모든 카트의 회원 ID가 동일한지 확인
        boolean sameMember = carts.stream()
                .allMatch(cart -> cart.getMember().getId().equals(memberId));
        if (!sameMember || member == null) {
            // 모든 카트가 동일한 회원에 속하지 않거나 회원이 존재하지 않는 경우, 주문 생성 실패
            return null;
        }

        Orders order = new Orders();
        order.setCarts(cartslist);
        order.setTotalPrice(calculateTotalPrice(carts));
        order.setProductName(getProductNames(carts));
        order.setPhoneNumber(getMemberPhoneNumber(carts));
        order.setMember(member);
        order.setOrdererName(member.getUsername());

        // 주문 반환
        return order;
    }

    // 주문 상품 이름들을 가져오는 메서드
    private String getProductNames(List<Cart> carts) {
        StringBuilder productNamesBuilder = new StringBuilder();
        for (Cart cart : carts) {
            Long productId = cart.getProduct().getProductId();
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                if (productNamesBuilder.length() > 0) {
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
        Member member = memberRepository.findById(memberId).orElse(null);
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
     * @param temporaryOrder
     * @param orders
     * @return
     */
    public Orders orderConfirm(Orders temporaryOrder, Orders orders) {

        String merchantUid = generateMerchantUid(); //주문번호 생성

        temporaryOrder.setMerchantUid(merchantUid);

        temporaryOrder.setPostCode(orders.getPostCode());
        temporaryOrder.setAddress(orders.getAddress());
        temporaryOrder.setDetailAddress(orders.getDetailAddress());
        temporaryOrder.setOrdererName(orders.getOrdererName());
        temporaryOrder.setPhoneNumber(orders.getPhoneNumber());
        temporaryOrder.setPayMethod(orders.getPayMethod());

        Orders completedOrder = orderRepository.save(temporaryOrder);

        return completedOrder;
    }

    // 주문번호 생성 메서드
    private String generateMerchantUid() {
        // 현재 날짜와 시간을 포함한 고유한 문자열 생성
        String uniqueString = UUID.randomUUID().toString().replace("-", "");
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDay = today.format(formatter).replace("-", "");
        // 무작위 문자열과 현재 날짜/시간을 조합하여 주문번호 생성
        String merchantUid = formattedDay +'-'+ uniqueString;
        return merchantUid;
    }

    public void deleteCart(List<Cart> carts) {
        for (Cart cart : carts) {
            cartRepository.delete(cart);
        }
    }
}
