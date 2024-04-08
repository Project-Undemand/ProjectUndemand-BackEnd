package PU.pushop.payment.controller;

import PU.pushop.cart.entity.Cart;
import PU.pushop.cart.repository.CartRepository;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.order.repository.OrderRepository;
import PU.pushop.payment.model.PaymentRequestDto;
import PU.pushop.payment.repository.PaymentRepository;
import PU.pushop.payment.service.PaymentService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
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
    private final HttpSession httpSession;


    public final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final CartRepository cartRepository;
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

        paymentService.processPaymentDone(request);

        return payment;
    }

    // 결제 완료 화면에서 세션 저장값, 장바구니 삭제하는 로직 - 프론트에서 확인해야 함
    @GetMapping("/order/paymentconfirm")
    public void deleteSession() {
        List<Long>cartIds = (List) httpSession.getAttribute("cartIds");
        System.out.println("cartIds: " + cartIds);

        for(Long cartId : cartIds){
            Cart cart = cartRepository.findById(cartId).orElse(null);
            cartRepository.delete(cart);
        }
        // 세션에서 임시 주문 정보 삭제
        httpSession.removeAttribute("temporaryOrder");
        httpSession.removeAttribute("cardIds");

    }

}
