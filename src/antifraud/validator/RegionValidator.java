package antifraud.validator;

import antifraud.model.enums.Region;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class RegionValidator implements ConstraintValidator<RegionCheck, Region> {


    @Override
    public boolean isValid(Region value, ConstraintValidatorContext context) {
        return Arrays.stream(Region.values()).anyMatch(r -> r == value);
    }
}
