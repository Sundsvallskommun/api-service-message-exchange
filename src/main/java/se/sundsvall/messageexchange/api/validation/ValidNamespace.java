package se.sundsvall.messageexchange.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import se.sundsvall.messageexchange.api.validation.impl.NamespaceValidator;

@Constraint(validatedBy = NamespaceValidator.class)
@Target({
	ElementType.FIELD, ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNamespace {
	String message() default "Invalid namespace. It can only contain A-Z, a-z, 0-9, - and _";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
