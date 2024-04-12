package PU.pushop.payment.service;

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
import com.siot.IamportRestClient.response.Payment;
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
    private final ProductRepositoryV1 productRepository;
    private final ProductManagementRepository productMgtRepository;

    public void processPaymentDone(PaymentRequestDto request) {

        Long orderId = request.getOrderId();
        Long memberId = request.getMemberId();
        Long totalPrice = request.getPrice();
        List<Long> inventoryIdList = request.getInventoryIdList();

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

        for (Long inventoryId : inventoryIdList) {
            PaymentHistory paymentHistory = new PaymentHistory();

            ProductManagement inventory = productMgtRepository.findById(inventoryId)
                    .orElseThrow(() -> new NoSuchElementException("해당 상품을 찾을 수 없습니다. Id : " + inventoryId));

            Product product = inventory.getProduct();

            String option = inventory.getColor().getColor() + ", " + inventory.getSize().toString();

            paymentHistory.setProduct(product);
            paymentHistory.setProductName(product.getProductName());
            paymentHistory.setProductOption(option);
            paymentHistory.setOrders(order);
            paymentHistory.setMember(member);
            paymentHistory.setTotalPrice(totalPrice);
            paymentHistory.setPrice(product.getPrice());

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

