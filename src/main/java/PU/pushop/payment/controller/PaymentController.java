package PU.pushop.payment.controller;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.order.entity.Orders;
import PU.pushop.order.repository.OrderRepository;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.payment.model.PaymentHistoryDto;
import PU.pushop.payment.repository.PaymentRepository;
import PU.pushop.payment.service.PaymentService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    public final OrderRepository orderRepository;
    private final MemberRepositoryV1 memberRepository;
    private final PaymentRepository paymentRepository;
    private IamportClient iamportClient;

    @Value("${IMP_API_KEY}")
    private String apiKey;

    @Value("${imp.api.secretkey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    private PaymentHistory RequestForm(PaymentHistoryDto request) {
        PaymentHistory paymentHistory = new PaymentHistory();
        Member member = null;
        if (request.getMemberId() != null) {
            Long memberId = request.getMemberId();
            member = memberRepository.findById(memberId)
                    .orElse(null);
        }
        Orders order = null;
        if (request.getOrderId() != null) {
            order = orderRepository.findById(request.getOrderId())
                    .orElse(null);
        }
        paymentHistory.setOrders(order);
        paymentHistory.setMember(member);
        paymentHistory.setPrice(request.getPrice());

        return paymentHistory;
    }

    @PostMapping("/order/payment/{imp_uid}")
    public IamportResponse<Payment> validateIamport(@PathVariable String imp_uid, @RequestBody PaymentHistoryDto request) {
        IamportResponse<Payment> payment = null;
        try {
            payment = iamportClient.paymentByImpUid(imp_uid);
        } catch (IamportResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("결제 요청 응답. 결제 내역 - 주문 번호: {}", payment.getResponse().getMerchantUid());

        PaymentHistory payRequest = RequestForm(request);

        //orders 테이블에서 해당 부분 결제true 처리
        Orders nowOrder = orderRepository.findById(payRequest.getOrders().getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

        nowOrder.setPaymentStatus(true);
        PaymentHistory paymentHistory = RequestForm(request);
        paymentRepository.save(paymentHistory);

        return payment;
    }

}
