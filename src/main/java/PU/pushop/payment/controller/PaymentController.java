package PU.pushop.payment.controller;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.order.entity.Orders;
import PU.pushop.order.repository.OrderRepository;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.payment.model.PaymentHistoryDto;
import PU.pushop.payment.model.PaymentRequestDto;
import PU.pushop.payment.repository.PaymentRepository;
import PU.pushop.payment.service.PaymentService;
import PU.pushop.product.entity.Product;
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
import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    public final OrderRepository orderRepository;
    private final PaymentService paymentService;
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

    @PostMapping("/order/payment/{imp_uid}")
    public IamportResponse<Payment> validateIamport(@PathVariable String imp_uid, @RequestBody PaymentRequestDto request) {
        IamportResponse<Payment> payment = null;
        try {
            payment = iamportClient.paymentByImpUid(imp_uid);
        } catch (IamportResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("결제 요청 응답. 결제 내역 - 주문 번호: {}", payment.getResponse().getMerchantUid());


        Long memberId = request.getMemberId();
        Long orderId = request.getOrderId();
        Long totalPrice = request.getPrice();
        List<Long> productIdList = request.getProductIdList();

        paymentService.processPaymentDone(memberId, orderId, totalPrice, productIdList);

        return payment;
    }

}
