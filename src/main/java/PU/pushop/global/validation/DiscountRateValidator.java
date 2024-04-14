package PU.pushop.global.validation;

import PU.pushop.product.model.ProductCreateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DiscountRateValidator implements ConstraintValidator<ValidDiscountRate, ProductCreateDto> {
    @Override
    public boolean isValid(ProductCreateDto value, ConstraintValidatorContext context) {
        // isDiscount가 true이고 discountRate가 null이거나 0보다 작으면 유효하지 않음
        if (value.getIsDiscount() && (value.getDiscountRate() == null || value.getDiscountRate() < 1)) {
            return false;
        }
        // isDiscount가 false이고 discountRate가 null이 아니면 유효하지 않음
        if (!value.getIsDiscount() && value.getDiscountRate() != null) {
            return false;
        }
        return true;
    }

}
