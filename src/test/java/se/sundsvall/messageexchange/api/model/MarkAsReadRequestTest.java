package se.sundsvall.messageexchange.api.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class MarkAsReadRequestTest {

	private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void testBean() {
		MatcherAssert.assertThat(MarkAsReadRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		final var messageIds = List.of(randomUUID().toString());
		final var identifier = Identifier.create().withType("adAccount").withValue("joe01doe");
		final var part = "errand-123";

		// Act
		final var result = MarkAsReadRequest.create()
			.withMessageIds(messageIds)
			.withIdentifier(identifier)
			.withPart(part);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getMessageIds()).isEqualTo(messageIds);
		assertThat(result.getIdentifier()).isEqualTo(identifier);
		assertThat(result.getPart()).isEqualTo(part);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(MarkAsReadRequest.create()).hasAllNullFieldsOrProperties();
		assertThat(new MarkAsReadRequest()).hasAllNullFieldsOrProperties();
	}

	@Test
	void validRequestWithIdentifier() {
		final var request = MarkAsReadRequest.create()
			.withMessageIds(List.of(randomUUID().toString()))
			.withIdentifier(Identifier.create().withType("adAccount").withValue("joe01doe"));

		assertThat(VALIDATOR.validate(request)).isEmpty();
	}

	@Test
	void validRequestWithPart() {
		final var request = MarkAsReadRequest.create()
			.withMessageIds(List.of(randomUUID().toString()))
			.withPart("errand-123");

		assertThat(VALIDATOR.validate(request)).isEmpty();
	}

	@Test
	void invalidWhenNeitherIdentifierNorPart() {
		final var request = MarkAsReadRequest.create()
			.withMessageIds(List.of(randomUUID().toString()));

		assertThat(VALIDATOR.validate(request))
			.extracting("message")
			.containsExactly("At least one of 'identifier' or 'part' must be provided");
	}

	@Test
	void invalidWhenMessageIdsEmpty() {
		final var request = MarkAsReadRequest.create()
			.withMessageIds(List.of())
			.withPart("errand-123");

		assertThat(VALIDATOR.validate(request))
			.extracting("propertyPath")
			.anyMatch(path -> path.toString().equals("messageIds"));
	}
}
