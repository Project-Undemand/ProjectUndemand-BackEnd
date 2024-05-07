package PU.pushop.payment.service;

import PU.pushop.global.ResponseMessageConstants;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.order.entity.Orders;
import PU.pushop.order.entity.enums.PayMethod;
import PU.pushop.order.repository.OrderRepository;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.payment.entity.PaymentRefund;
import PU.pushop.payment.entity.Status;
import PU.pushop.payment.model.PaymentCancelDto;
import PU.pushop.payment.model.PaymentHistoryDto;
import PU.pushop.payment.model.PaymentRequestDto;
import PU.pushop.payment.repository.PaymentRefundRepository;
import PU.pushop.payment.repository.PaymentRepository;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.repository.ProductManagementRepository;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static PU.pushop.global.authorization.MemberAuthorizationUtil.verifyUserIdMatch;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PaymentService {

    private final OrderRepository orderRepository;
    private final MemberRepositoryV1 memberRepository;
    private final PaymentRepository paymentRepository;
    private final ProductManagementRepository productMgtRepository;
    private final PaymentRefundRepository paymentRefundRepository;

    public void processPaymentDone(Payment response, PaymentRequestDto request) {

        Long orderId = request.getOrderId();
        Long memberId = request.getMemberId();
        verifyUserIdMatch(memberId); // 로그인 된 사용자와 요청 사용자 비교
        Integer totalPrice = request.getPrice().intValue();
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
        createPaymentHistory(response, productMgtIdList, order, member, totalPrice);

    }

    // 결제내역 저장하는 메서드
    private void createPaymentHistory(Payment response, List<Long> productMgtIdList, Orders order, Member member, Integer totalPrice) {
        for (Long productMgtId : productMgtIdList) {

            String impUid = response.getImpUid();
            String payMethod = response.getPayMethod();
            BigDecimal payAmount = response.getAmount();
            String bankCode = response.getBankCode();
            String bankName = response.getBankName();
            String buyerAddr = response.getBuyerAddr();
            String buyerEmail = response.getBuyerEmail();

            ProductManagement productMgt = productMgtRepository.findById(productMgtId)
                    .orElseThrow(() -> new NoSuchElementException(ResponseMessageConstants.PRODUCT_NOT_FOUND));

            Product product = productMgt.getProduct();
            String option = productMgt.getColor().getColor() + ", " + productMgt.getSize().toString(); // 상품옵션 문자열로 저장

            PaymentHistory paymentHistory = new PaymentHistory(impUid, member, order, product, product.getProductName(),option,product.getPrice() ,payAmount.intValue(), Status.COMPLETE_PAYMENT, payMethod, bankCode, bankName, buyerAddr, buyerEmail);

            paymentRepository.save(paymentHistory);

        }
    }

    /**
     * 내 결제내역 모아보기
     * @param memberId
     * @return
     */
    public List<PaymentHistoryDto> paymentHistoryList(Long memberId) {

        verifyUserIdMatch(memberId); // 로그인 된 사용자와 요청 사용자 비교

        List<PaymentHistory> paymentHistories = paymentRepository.findByMemberId(memberId);

        List<PaymentHistoryDto> paymentHistoryDtos = new ArrayList<>();

        for (PaymentHistory paymentHistory : paymentHistories) {
            PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto(paymentHistory);
            paymentHistoryDtos.add(paymentHistoryDto);
        }

        return paymentHistoryDtos;
    }




    public PaymentRefund getRefundInfo( PaymentHistory paymentHistory) {

        // 환불 전 검증
        String impUid = paymentHistory.getImpUid();
        Integer beforeChecksum = paymentHistory.getTotalPrice();
        Integer refundAmount = paymentHistory.getPrice();
        if (beforeChecksum == 0) {
            throw new IllegalArgumentException("이미 전액 환불 완료된 주문건입니다");
        }
        return new PaymentRefund(impUid, refundAmount, beforeChecksum);

    }

    public PaymentRefund setRefundInfo(PaymentCancelDto requestDto, PaymentHistory paymentHistory, PaymentRefund paymentInfo) {
        // 환불

        PaymentRefund paymentRefund = null;

        String impUid = paymentInfo.getImpUid();
        Integer amount = paymentInfo.getAmount();
        String refundTel = paymentHistory.getOrders().getPhoneNumber();
        Integer checksum = paymentInfo.getChecksum();
        String reason = requestDto.getReason();
        String refundHolder = requestDto.getRefundHolder();
        String refundBank = requestDto.getRefundBank();
        String refundAccount = requestDto.getRefundAccount();


        if (requestDto.getPayMethod() == PayMethod.VBANK) {
            paymentRefund = new PaymentRefund(paymentHistory, impUid, amount, refundTel, checksum, reason, refundHolder, refundBank, refundAccount);
        } else {
            paymentRefund = new PaymentRefund(paymentHistory,impUid, amount, refundTel, checksum, reason);
        }

        paymentRefundRepository.save(paymentRefund);
        paymentHistory.setStatusType(Status.CANCELED);

        List<PaymentHistory> paymentHistoriesWithSameUid = paymentRepository.findByImpUid(impUid);

        Integer afterChecksum = checksum - amount;

        for (PaymentHistory history : paymentHistoriesWithSameUid) {
            history.setTotalPrice(afterChecksum);
        }


        return paymentRefund;
    }



}

