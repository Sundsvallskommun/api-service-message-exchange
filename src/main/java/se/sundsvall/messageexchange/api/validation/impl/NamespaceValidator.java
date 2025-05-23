package se.sundsvall.messageexchange.api.validation.impl;

import static se.sundsvall.messageexchange.Constants.NAMESPACE_REGEXP;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.messageexchange.api.validation.ValidNamespace;

public class NamespaceValidator implements ConstraintValidator<ValidNamespace, String> {

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}
		return value.matches(NAMESPACE_REGEXP);
	}
}
