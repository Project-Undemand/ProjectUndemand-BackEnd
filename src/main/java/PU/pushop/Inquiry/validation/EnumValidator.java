package PU.pushop.Inquiry.validation;

import PU.pushop.Inquiry.entity.enums.InquiryType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<EnumConstraint, Enum<?>> {
    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        try {
            // Check if the enum value is contained in the defined enum constants
            for (InquiryType enumValue : InquiryType.values()) {
                if (enumValue == value) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }
}
