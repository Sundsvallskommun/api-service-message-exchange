package se.sundsvall.chat.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MessageRequestTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(MessageRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {

		// Arrange
		final var sequenceNumber = "sequenceNumber";
		final var inReplyTo = "inReplyTo";
		final var createdBy = Participant.create();
		final var content = "content";
		final var readBy = List.of(Participant.create());

		// Act
		final var result = MessageRequest.create()
			.withSequenceNumber(sequenceNumber)
			.withInReplyTo(inReplyTo)
			.withCreatedBy(createdBy)
			.withContent(content)
			.withReadBy(readBy);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getSequenceNumber()).isEqualTo(sequenceNumber);
		assertThat(result.getInReplyTo()).isEqualTo(inReplyTo);
		assertThat(result.getCreatedBy()).isEqualTo(createdBy);
		assertThat(result.getContent()).isEqualTo(content);
		assertThat(result.getReadBy()).isEqualTo(readBy);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Message.create()).hasAllNullFieldsOrProperties();
		assertThat(new Message()).hasAllNullFieldsOrProperties();
	}

}
