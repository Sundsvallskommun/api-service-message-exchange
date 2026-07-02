package se.sundsvall.messageexchange.api.validation.impl;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import se.sundsvall.messageexchange.api.model.Identifier;
import se.sundsvall.messageexchange.api.model.MarkAsReadRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MarkAsReadRequestValidatorTest {

	private final MarkAsReadRequestValidator validator = new MarkAsReadRequestValidator();
	private final ConstraintValidatorContext contextMock = mock(ConstraintValidatorContext.class);

	@Test
	void validWithIdentifier() {
		final var request = MarkAsReadRequest.create().withIdentifier(Identifier.create().withType("adAccount").withValue("joe01doe"));
		assertThat(validator.isValid(request, contextMock)).isTrue();
	}

	@Test
	void validWithPart() {
		final var request = MarkAsReadRequest.create().withPart("errand-123");
		assertThat(validator.isValid(request, contextMock)).isTrue();
	}

	@Test
	void invalidWithNeither() {
		final var request = MarkAsReadRequest.create();
		assertThat(validator.isValid(request, contextMock)).isFalse();
	}

	@Test
	void invalidWithBlankPart() {
		final var request = MarkAsReadRequest.create().withPart("  ");
		assertThat(validator.isValid(request, contextMock)).isFalse();
	}

	@Test
	void validWhenNull() {
		assertThat(validator.isValid(null, contextMock)).isTrue();
	}
}
