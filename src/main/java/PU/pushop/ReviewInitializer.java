package PU.pushop;

import PU.pushop.global.dummydata.util.ReviewDataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("develop")
@Order(5) // 다른 순서 지정
@RequiredArgsConstructor
public class ReviewInitializer implements ApplicationRunner {

    private final ReviewDataUtil reviewDataUtil;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        reviewDataUtil.createReviews();
    }
}
