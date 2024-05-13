package PU.pushop.order.controller;

import PU.pushop.order.entity.Orders;
import PU.pushop.order.model.OrderDto;
import PU.pushop.order.model.OrderResponseDto;
import PU.pushop.order.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final HttpSession httpSession;
    private final ModelMapper modelMapper;

    /**
     * 주문서에 나타낼 정보
     * @param payload  "cartIds" : [1,2,3]
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody Map<String, Object> payload) {
        List<Integer> cartIdsInteger = (List<Integer>) payload.get("cartIds");
        List<Long> cartIds = cartIdsInteger.stream().map(Long::valueOf).collect(Collectors.toList());
        Orders temporaryOrder = orderService.createOrder(cartIds);

        // 세션에 임시 주문 정보를 저장
        httpSession.setAttribute("temporaryOrder", temporaryOrder);
        httpSession.setAttribute("cartIds", cartIds); // 장바구니 id 저장

        Object cartIdsAttribute = httpSession.getAttribute("cartIds");

        return ResponseEntity.ok("주문 임시 저장 완료");
    }


    /**
     * 주문서에서 입력받아 최종 주문 테이블 생성
     * @param request
     * @return
     */
    @PostMapping("/done")
    public ResponseEntity<Object> completeOrder(@Valid @RequestBody OrderDto request) {


        OrderDto orders = modelMapper.map(request, OrderDto.class);

        // 세션에서 임시 주문 정보를 가져옴
        Orders temporaryOrder = (Orders) httpSession.getAttribute("temporaryOrder");

        if (temporaryOrder == null) {
            return ResponseEntity.badRequest().body("임시 주문 정보를 찾을 수 없습니다.");
        }

        Orders completedOrder = orderService.orderConfirm(temporaryOrder, orders);

        OrderResponseDto orderResponseDto = new OrderResponseDto(completedOrder);

        return ResponseEntity.ok(orderResponseDto);

    }

}
