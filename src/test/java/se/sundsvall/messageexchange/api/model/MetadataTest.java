package se.sundsvall.messageexchange.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class MetadataTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Metadata.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		final var key = "key";
		final var values = List.of("val1", "val2");
		// Act
		final var result = Metadata.create()
			.withKey(key)
			.withValues(values);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Metadata.create()).hasAllNullFieldsOrProperties();
		assertThat(new Metadata()).hasAllNullFieldsOrProperties();
	}

}
