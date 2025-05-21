package se.sundsvall.chat.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.CoreMatchers.allOf;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MessageEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(MessageEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {

		// Arrange
		final var id = "id";
		final var sequenceNumber = "sequenceNumber";
		final var inReplyTo = "inReplyTo";
		final var created = now();
		final var createdBy = ParticipantEntity.create();
		final var content = "content";
		final var readBy = List.of(ParticipantEntity.create());
		final var conversation = ConversationEntity.create();
		final var attachments = List.of(AttachmentEntity.create());

		// Act
		final var result = MessageEntity.create()
			.withId(id)
			.withSequenceNumber(sequenceNumber)
			.withInReplyTo(inReplyTo)
			.withCreated(created)
			.withCreatedBy(createdBy)
			.withContent(content)
			.withReadBy(readBy)
			.withConversation(conversation)
			.withAttachments(attachments);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getSequenceNumber()).isEqualTo(sequenceNumber);
		assertThat(result.getInReplyTo()).isEqualTo(inReplyTo);
		assertThat(result.getCreated()).isEqualTo(created);
		assertThat(result.getCreatedBy()).isEqualTo(createdBy);
		assertThat(result.getContent()).isEqualTo(content);
		assertThat(result.getReadBy()).isEqualTo(readBy);
		assertThat(result.getConversation()).isEqualTo(conversation);
		assertThat(result.getAttachments()).isEqualTo(attachments);

	}

	@Test
	void testOnCreate() {
		final var entity = new MessageEntity();
		entity.onCreate();

		assertThat(entity.getCreated()).isCloseTo(now(), within(1, SECONDS));
		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("created");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(MessageEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new MessageEntity()).hasAllNullFieldsOrProperties();
	}

}
