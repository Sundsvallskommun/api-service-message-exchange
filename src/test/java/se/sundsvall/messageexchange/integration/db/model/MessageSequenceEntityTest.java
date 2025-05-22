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

class MessageSequenceEntityTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(MessageSequenceEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "municipalityId";
		final var lastSequenceNumber = 1L;

		// Act
		final var result = MessageSequenceEntity.create()
			.withNamespace(namespace)
			.withMunicipalityId(municipalityId)
			.withLastSequenceNumber(lastSequenceNumber);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getNamespace()).isEqualTo(namespace);
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getLastSequenceNumber()).isEqualTo(lastSequenceNumber);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(MessageSequenceEntity.create()).hasAllNullFieldsOrPropertiesExcept("lastSequenceNumber")
			.satisfies(bean -> assertThat(bean.getLastSequenceNumber()).isZero());

		assertThat(new MessageSequenceEntity()).hasAllNullFieldsOrPropertiesExcept("lastSequenceNumber")
			.satisfies(bean -> assertThat(bean.getLastSequenceNumber()).isZero());
	}

}
