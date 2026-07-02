package se.sundsvall.messageexchange.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import se.sundsvall.messageexchange.api.validation.impl.MarkAsReadRequestValidator;

@Constraint(validatedBy = MarkAsReadRequestValidator.class)
@Target({
	ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMarkAsReadRequest {
	String message() default "At least one of 'identifier' or 'part' must be provided";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
