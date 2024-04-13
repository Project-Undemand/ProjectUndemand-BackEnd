package PU.pushop.global.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Null;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DiscountRateValidator.class)
public @interface ValidDiscountRate {

    // 커스텀 유효성 검사 애노테이션 - 할인

    String message() default "할인이 적용되면 할인율을 입력해야 하며, 할인이 적용되지 않으면 할인율을 입력할 수 업습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
