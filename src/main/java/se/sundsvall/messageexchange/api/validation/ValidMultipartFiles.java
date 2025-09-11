package se.sundsvall.messageexchange.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import se.sundsvall.messageexchange.api.validation.impl.ValidMultipartFilesValidator;

@Target({
	ElementType.PARAMETER, ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidMultipartFilesValidator.class)
public @interface ValidMultipartFiles {

	String message() default "all files must contain data";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
