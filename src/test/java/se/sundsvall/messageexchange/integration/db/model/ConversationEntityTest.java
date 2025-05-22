package se.sundsvall.messageexchange.integration.db.model;

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

class ConversationEntityTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(ConversationEntity.class, allOf(
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
		final var participants = List.of(IdentifierEntity.create());
		final var municipalityId = "municipalityId";
		final var namespace = "namespace";
		final var channelId = "channelId";
		final var metaData = List.of(MetadataEntity.create());
		final var topic = "topic";
		final var messages = List.of(MessageEntity.create());

		// Act
		final var result = ConversationEntity.create()
			.withId(id)
			.withParticipants(participants)
			.withMunicipalityId(municipalityId)
			.withNamespace(namespace)
			.withChannelId(channelId)
			.withMetadata(metaData)
			.withTopic(topic)
			.withMessages(messages);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getParticipants()).isEqualTo(participants);
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getNamespace()).isEqualTo(namespace);
		assertThat(result.getChannelId()).isEqualTo(channelId);
		assertThat(result.getMetadata()).isEqualTo(metaData);
		assertThat(result.getTopic()).isEqualTo(topic);
		assertThat(result.getMessages()).isEqualTo(messages);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ConversationEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new ConversationEntity()).hasAllNullFieldsOrProperties();
	}

}
