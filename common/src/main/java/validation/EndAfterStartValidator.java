package validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

public class EndAfterStartValidator implements ConstraintValidator<EndAfterStart, BookingCreateDto> {

    @Override
    public void initialize(EndAfterStart constraintAnnotation) {

    }

    @Override
    public boolean isValid(BookingCreateDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        if (dto.getStart() == null || dto.getEnd() == null) {
            return true;
        }
        return dto.getEnd().isAfter(dto.getStart());
    }
}
