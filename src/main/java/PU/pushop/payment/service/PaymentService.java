package PU.pushop.payment.service;

import PU.pushop.global.ResponseMessageConstants;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.order.entity.Orders;
import PU.pushop.order.repository.OrderRepository;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.payment.model.PaymentHistoryDto;
import PU.pushop.payment.model.PaymentRequestDto;
import PU.pushop.payment.repository.PaymentRepository;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.repository.ProductManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PaymentService {

    private final OrderRepository orderRepository;
    private final MemberRepositoryV1 memberRepository;
    private final PaymentRepository paymentRepository;
    private final ProductManagementRepository productMgtRepository;

    public void processPaymentDone(PaymentRequestDto request) {

        Long orderId = request.getOrderId();
        Long memberId = request.getMemberId();
        Long totalPrice = request.getPrice();
        List<Long> productMgtIdList = request.getInventoryIdList();

        //orders 테이블에서 해당 부분 결제true 처리
        Orders currentOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("주문 정보를 찾을 수 없습니다."));
        currentOrder.setPaymentStatus(true);

        // PaymentHistory 테이블에 저장할 Member 객체
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(ResponseMessageConstants.MEMBER_NOT_FOUND));

        // PaymentHistory 테이블에 저장할 Orders 객체
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("해당 주문서를 찾을 수 없습니다. Id : " + orderId));

        // 주문한 상품들에 대해 각각 결제내역 저장
        createPaymentHistory(productMgtIdList, order, member, totalPrice);

    }

    // 결제내역 저장하는 메서드
    private void createPaymentHistory(List<Long> productMgtIdList, Orders order, Member member, Long totalPrice) {
        for (Long productMgtId : productMgtIdList) {

//            PaymentHistory paymentHistory = new PaymentHistory();

            ProductManagement productMgt = productMgtRepository.findById(productMgtId)
                    .orElseThrow(() -> new NoSuchElementException(ResponseMessageConstants.PRODUCT_NOT_FOUND));

            Product product = productMgt.getProduct();
            String option = productMgt.getColor().getColor() + ", " + productMgt.getSize().toString(); // 상품옵션 문자열로 저장

            PaymentHistory paymentHistory = new PaymentHistory(member, order, product, product.getProductName(),option,product.getPrice(),totalPrice);

            paymentRepository.save(paymentHistory);

        }
    }


    public List<PaymentHistoryDto> paymentHistoryList(Long memberId) {
        List<PaymentHistory> paymentHistories = paymentRepository.findByMemberId(memberId);

        List<PaymentHistoryDto> paymentHistoryDtos = new ArrayList<>();

        for (PaymentHistory paymentHistory : paymentHistories) {
            PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto(paymentHistory);
            paymentHistoryDtos.add(paymentHistoryDto);
        }

        return paymentHistoryDtos;
    }


}

