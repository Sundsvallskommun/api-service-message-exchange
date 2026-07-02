package se.sundsvall.messageexchange.api.model;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class ReadByCountTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(ReadByCount.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		final var identifier = Identifier.create().withType("adAccount").withValue("joe01doe");
		final var count = 4L;

		// Act
		final var result = ReadByCount.create()
			.withIdentifier(identifier)
			.withCount(count);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getIdentifier()).isEqualTo(identifier);
		assertThat(result.getCount()).isEqualTo(count);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ReadByCount.create()).hasAllNullFieldsOrProperties();
		assertThat(new ReadByCount()).hasAllNullFieldsOrProperties();
	}
}
