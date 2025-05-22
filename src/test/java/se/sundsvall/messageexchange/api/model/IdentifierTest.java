package se.sundsvall.messageexchange.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class IdentifierTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Identifier.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		final var type = "type";
		final var value = "value";
		// Act
		final var result = Identifier.create()
			.withType(type)
			.withValue(value);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Identifier.create()).hasAllNullFieldsOrProperties();
		assertThat(new Identifier()).hasAllNullFieldsOrProperties();
	}

}
