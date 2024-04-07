package PU.pushop.payment.service;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.order.entity.Orders;
import PU.pushop.order.repository.OrderRepository;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.payment.repository.PaymentRepository;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.product.service.ProductServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final OrderRepository orderRepository;
    private final MemberRepositoryV1 memberRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepositoryV1 productRepository;
    public void processPaymentDone(Long memberId, Long orderId, Long totalPrice, List<Long> productIdList) {

        //orders 테이블에서 해당 부분 결제true 처리
        Orders nowOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

        nowOrder.setPaymentStatus(true);

        // PaymentHistory 테이블 생성
        Member member = null;
        if (memberId != null) {
            member = memberRepository.findById(memberId)
                    .orElse(null);
        }
        Orders order = null;
        if (orderId != null) {
            order = orderRepository.findById(orderId)
                    .orElse(null);
        }

        for (Long productId : productIdList) {
            PaymentHistory paymentHistory = new PaymentHistory();

            Product product = null;
            if(productId != null){
                product = productRepository.findByProductId(productId)
                        .orElse(null);
            }
            paymentHistory.setProduct(product);
            paymentHistory.setOrders(order);
            paymentHistory.setMember(member);
            paymentHistory.setTotalPrice(totalPrice);
            paymentHistory.setPrice(product.getPrice());

            paymentRepository.save(paymentHistory);

        }

    }


}

