package PU.pushop.global.dummydata.util;

import PU.pushop.global.ResponseMessageConstants;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.order.entity.Orders;
import PU.pushop.order.entity.enums.PayMethod;
import PU.pushop.order.repository.OrderRepository;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.repository.ProductManagementRepository;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Component
@RequiredArgsConstructor
public class OrderDataUtil {

    private final OrderRepository orderRepository;
    private final MemberRepositoryV1 memberRepositoryV1;
    private final ProductManagementRepository productManagementRepository;

    public void createOrders() {
        createOrdersForMember("user1@pushop.com", 1, 10);
        createOrdersForMember("user2@pushop.com", 11, 20);
        createOrdersForMember("user3@pushop.com", 21, 30);
    }

    private void createOrdersForMember(String email, int startProductId, int endProductId) {
        Member member = memberRepositoryV1.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(ResponseMessageConstants.MEMBER_NOT_FOUND));

        // 상품 ID 범위 내에서 3개의 상품을 고정적으로 선택
        for (int productId = startProductId; productId <= endProductId; productId += 3) {
            List<ProductManagement> productManagements = getProductManagements(productId, productId + 2);

            if (productManagements.size() < 3) {
                continue; // 상품이 3개 미만인 경우 건너뜀
            }

            // 3개의 상품을 포함하는 주문 생성
            Orders order = new Orders(member, productManagements, member.getNickname(),
                    productManagements.get(0).getProduct().getProductName(),
                    calculateTotalPrice(productManagements), member.getPhone(),
                    "123 Test Street", "Apartment 101", "38431", "testUid" + productId, PayMethod.card, true);

            orderRepository.save(order);
        }
    }

    private List<ProductManagement> getProductManagements(int startId, int endId) {
        return productManagementRepository.findAllById(LongStream.rangeClosed(startId, endId).boxed().collect(Collectors.toList()));
    }

    private BigDecimal calculateTotalPrice(List<ProductManagement> productManagements) {
        return productManagements.stream()
                .map(pm -> pm.getProduct().getPriceToBigDecimal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
