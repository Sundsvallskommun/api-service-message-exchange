package se.sundsvall.messageexchange.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class SequenceEntityTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(SequenceEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange

		// Act
		final var result = SequenceEntity.create()
			.withId(123L);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(123L);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(SequenceEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new SequenceEntity()).hasAllNullFieldsOrProperties();
	}

}
