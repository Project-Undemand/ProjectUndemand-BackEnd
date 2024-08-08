package PU.pushop.global.dummydata.util;

import PU.pushop.InventoryProduct.entity.InventoryProduct;
import PU.pushop.InventoryProduct.repository.InventoryProductRepository;
import PU.pushop.global.ResponseMessageConstants;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.order.entity.Orders;
import PU.pushop.order.entity.enums.PayMethod;
import PU.pushop.order.repository.OrderRepository;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.payment.entity.Status;
import PU.pushop.payment.repository.PaymentRepository;
import PU.pushop.product.entity.Product;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.repository.ProductManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class PaymentHistoryDataUtil {

    private final PaymentRepository paymentHistoryRepository;
    private final OrderRepository orderRepository;
    private final MemberRepositoryV1 memberRepositoryV1;
    private final ProductManagementRepository productManagementRepository;
    private final InventoryProductRepository inventoryProductRepository;
    private final Random random = new Random();

    @Transactional
    public void createPaymentHistories() {
        createPaymentHistoriesForMember("user1@pushop.com");
        createPaymentHistoriesForMember("user2@pushop.com");
        createPaymentHistoriesForMember("user3@pushop.com");
    }

    @Transactional
    public void createPaymentHistoriesForMember(String email) {
        Member member = memberRepositoryV1.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(ResponseMessageConstants.MEMBER_NOT_FOUND));

        List<Orders> ordersList = orderRepository.findByMember(member);

        for (Orders order : ordersList) {
            for (ProductManagement pm : order.getProductManagements()) {
                Product product = pm.getProduct();
                String option = pm.getColor().getColor() + ", " + pm.getSize().toString();
                String address = "pushop 테스트 주소";
                long quantity = 1 + random.nextInt(5);

                InventoryProduct inventoryProduct = pm.getInventoryProduct();

                PaymentHistory paymentHistory = new PaymentHistory(
                        "impUid" + pm.getInventoryId(), member, order, product,
                        product.getProductName(), option, quantity,
                        pm.getProduct().getPriceToBigDecimal().intValue(),
                        pm.getProduct().getPriceToBigDecimal().intValue() * (int) quantity,
                        Status.COMPLETE_PAYMENT, PayMethod.card.toString(),"noBankCode", "noBankName",
                        address, member.getEmail());

                paymentHistoryRepository.save(paymentHistory);

                // 재고 감소 로직
                inventoryProduct.setProductStock(inventoryProduct.getProductStock() - quantity);
                inventoryProductRepository.save(inventoryProduct);
            }
        }
    }
}
