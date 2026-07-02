package se.sundsvall.messageexchange.api.model;

import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class ReadByStatisticsTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(ReadByStatistics.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		final var messageCount = 13L;
		final var readByCount = List.of(ReadByCount.create().withCount(4L));
		final var readByPartCount = List.of(ReadByPartCount.create().withPart("errand-123").withCount(6L));

		// Act
		final var result = ReadByStatistics.create()
			.withMessageCount(messageCount)
			.withReadByCount(readByCount)
			.withReadByPartCount(readByPartCount);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getMessageCount()).isEqualTo(messageCount);
		assertThat(result.getReadByCount()).isEqualTo(readByCount);
		assertThat(result.getReadByPartCount()).isEqualTo(readByPartCount);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ReadByStatistics.create()).hasAllNullFieldsOrProperties();
		assertThat(new ReadByStatistics()).hasAllNullFieldsOrProperties();
	}
}
