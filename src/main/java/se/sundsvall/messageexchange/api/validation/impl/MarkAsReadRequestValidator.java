package se.sundsvall.messageexchange.api.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.messageexchange.api.model.MarkAsReadRequest;
import se.sundsvall.messageexchange.api.validation.ValidMarkAsReadRequest;

public class MarkAsReadRequestValidator implements ConstraintValidator<ValidMarkAsReadRequest, MarkAsReadRequest> {

	@Override
	public boolean isValid(final MarkAsReadRequest value, final ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		final var hasIdentifier = value.getIdentifier() != null;
		final var hasPart = value.getPart() != null && !value.getPart().isBlank();
		return hasIdentifier || hasPart;
	}
}
