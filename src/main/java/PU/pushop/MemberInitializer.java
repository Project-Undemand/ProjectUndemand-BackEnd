package PU.pushop;


import PU.pushop.global.dummydata.util.MemberDataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("develop")
@Order(2) // 다른 순서 지정
@RequiredArgsConstructor
public  class MemberInitializer implements ApplicationRunner {
    private final MemberDataUtil memberDataUtil;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        memberDataUtil.generateMemberData();
    }
}
