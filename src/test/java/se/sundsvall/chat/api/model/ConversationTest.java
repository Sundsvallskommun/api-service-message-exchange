package se.sundsvall.chat.api.model;

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

class ConversationTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Conversation.class, allOf(
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
		final var participants = List.of(Participant.create());
		final var municipalityId = "municipalityId";
		final var namespace = "namespace";
		final var channelId = "channelId";
		final var metaData = List.of(MetaData.create());
		final var topic = "topic";
		final var messages = List.of(Message.create());

		// Act
		final var result = Conversation.create()
			.withId(id)
			.withParticipants(participants)
			.withMunicipalityId(municipalityId)
			.withNamespace(namespace)
			.withChannelId(channelId)
			.withMetaData(metaData)
			.withTopic(topic)
			.withMessages(messages);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getParticipants()).isEqualTo(participants);
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getNamespace()).isEqualTo(namespace);
		assertThat(result.getChannelId()).isEqualTo(channelId);
		assertThat(result.getMetaData()).isEqualTo(metaData);
		assertThat(result.getTopic()).isEqualTo(topic);
		assertThat(result.getMessages()).isEqualTo(messages);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Conversation.create()).hasAllNullFieldsOrProperties();
		assertThat(new Conversation()).hasAllNullFieldsOrProperties();
	}

}
