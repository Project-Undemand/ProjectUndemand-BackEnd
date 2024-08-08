package PU.pushop;

import PU.pushop.global.dummydata.util.OrderDataUtil;
import PU.pushop.global.dummydata.util.PaymentHistoryDataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("develop")
@Order(3) // 다른 순서 지정
@RequiredArgsConstructor
public class PaymentInitializer implements ApplicationRunner {

    private final OrderDataUtil orderDataUtil;
    private final PaymentHistoryDataUtil paymentHistoryDataUtil;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        orderDataUtil.createOrders();
        paymentHistoryDataUtil.createPaymentHistories();
    }
}
