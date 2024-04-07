package PU.pushop.order.controller;

import PU.pushop.order.entity.Orders;
import PU.pushop.order.entity.enums.PayMethod;
import PU.pushop.order.model.OrderDto;
import PU.pushop.order.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final HttpSession httpSession;

    /**
     * 주문서에 나타낼 정보
     * @param payload  "cartIds" : [1,2,3]
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> payload) {
        List<Long> cartIds = (List<Long>) payload.get("cartIds");
        Orders temporaryOrder = orderService.createOrder(cartIds);

        // 세션에 임시 주문 정보를 저장
        httpSession.setAttribute("temporaryOrder", temporaryOrder);

        return ResponseEntity.ok(httpSession.getAttribute("temporaryOrder"));
    }

    /**
     * 주문서에서 입력받아 최종 주문 테이블 생성
     * @param request
     * @return
     */
    @PostMapping("/done")
    public ResponseEntity<?> completeOrder(@RequestBody OrderDto request) {

        Orders orders = OrderDto.RequestForm(request);

        // 세션에서 임시 주문 정보를 가져옴
        Orders temporaryOrder = (Orders) httpSession.getAttribute("temporaryOrder");
        if (temporaryOrder == null) {
            return ResponseEntity.badRequest().body("임시 주문 정보를 찾을 수 없습니다.");
        }

        Orders completedOrder = orderService.orderConfirm(temporaryOrder, orders);

        // 세션에서 임시 주문 정보 삭제
        httpSession.removeAttribute("temporaryOrder");

        // 장바구니에서 삭제하는 로직 추가해야 함

        return ResponseEntity.ok(completedOrder);
    }

}
