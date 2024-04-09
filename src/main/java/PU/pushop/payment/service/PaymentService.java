package PU.pushop.payment.service;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.order.entity.Orders;
import PU.pushop.order.repository.OrderRepository;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.payment.model.PaymentRequestDto;
import PU.pushop.payment.repository.PaymentRepository;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PaymentService {

    private final OrderRepository orderRepository;
    private final MemberRepositoryV1 memberRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepositoryV1 productRepository;

    public void processPaymentDone(PaymentRequestDto request) {

        Long orderId = request.getOrderId();
        Long memberId = request.getMemberId();
        Long totalPrice = request.getPrice();
        List<Long> productIdList = request.getProductIdList();

        //orders 테이블에서 해당 부분 결제true 처리
        Orders nowOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("주문 정보를 찾을 수 없습니다."));

        nowOrder.setPaymentStatus(true);

        // PaymentHistory 테이블 생성
        Member member = null;
        if (memberId != null) {

            member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다. Id : " + memberId));
        }
        Orders order = null;
        if (orderId != null) {
            order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NoSuchElementException("해당 주문서를 찾을 수 없습니다. Id : " + orderId));
        }

        for (Long productId : productIdList) {
            PaymentHistory paymentHistory = new PaymentHistory();

            Product product = null;
            if(productId != null){
                product = productRepository.findByProductId(productId)
                        .orElseThrow(() -> new NoSuchElementException("해당 상품을 찾을 수 없습니다. Id : " + productId));
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

