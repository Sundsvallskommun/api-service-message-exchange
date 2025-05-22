package se.sundsvall.messageexchange.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.messageexchange.api.model.Conversation;
import se.sundsvall.messageexchange.api.model.Identifier;
import se.sundsvall.messageexchange.api.model.Message;
import se.sundsvall.messageexchange.api.model.Metadata;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;
import se.sundsvall.messageexchange.integration.db.model.IdentifierEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;
import se.sundsvall.messageexchange.integration.db.model.MetadataEntity;

@ExtendWith(MockitoExtension.class)
class MapperTest {

	@Mock
	private EntityManager entityManagerMock;

	@Test
	void toConversationEntity() {
		// Arrange
		final var municipalityId = "municipalityId";
		final var namespace = "namespace";
		final var channelId = "channelId";
		final var topic = "topic";
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";
		final var type = "type";
		final var value = "value";

		final var conversationRequest = Conversation.create()
			.withChannelId(channelId)
			.withTopic(topic)
			.withParticipants(List.of(Identifier.create().withType(type).withValue(value)))
			.withMetadata(List.of(Metadata.create().withKey(key).withValues(List.of(value1, value2))));

		// Act
		final var result = Mapper.toConversationEntity(municipalityId, namespace, conversationRequest);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id", "messages");
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getNamespace()).isEqualTo(namespace);
		assertThat(result.getChannelId()).isEqualTo(channelId);
		assertThat(result.getTopic()).isEqualTo(topic);
		assertThat(result.getParticipants()).hasSize(1);
		assertThat(result.getMetadata()).hasSize(1);
	}

	@Test
	void toConversation() {
		// Arrange
		final var id = "id";
		final var municipalityId = "municipalityId";
		final var namespace = "namespace";
		final var channelId = "channelId";
		final var topic = "topic";
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";
		final var type = "type";
		final var value = "value";

		final var entity = ConversationEntity.create()
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withNamespace(namespace)
			.withChannelId(channelId)
			.withTopic(topic)
			.withParticipants(List.of(IdentifierEntity.create().withType(type).withValue(value)))
			.withMetadata(List.of(MetadataEntity.create().withKey(key).withValues(List.of(value1, value2))));

		// Act
		final var result = Mapper.toConversation(entity);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("messages");
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getNamespace()).isEqualTo(namespace);
		assertThat(result.getChannelId()).isEqualTo(channelId);
		assertThat(result.getTopic()).isEqualTo(topic);
		assertThat(result.getParticipants()).hasSize(1);
		assertThat(result.getMetadata()).hasSize(1);
	}

	@Test
	void updateConversationEntity() {
		// Arrange
		final var channelId = "channelId";
		final var topic = "topic";
		final var type = "type";
		final var value = "value";
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";

		final var conversationRequest = Conversation.create()
			.withChannelId(channelId)
			.withTopic(topic)
			.withParticipants(List.of(Identifier.create().withType(type).withValue(value)))
			.withMetadata(List.of(Metadata.create().withKey(key).withValues(List.of(value1, value2))));

		final var entity = ConversationEntity.create();

		// Act
		final var result = Mapper.updateConversationEntity(entity, conversationRequest);

		// Assert
		assertThat(result.getChannelId()).isEqualTo(channelId);
		assertThat(result.getTopic()).isEqualTo(topic);
		assertThat(result.getParticipants()).hasSize(1);
		assertThat(result.getMetadata()).hasSize(1);
	}

	@Test
	void toMessages() {
		// Arrange
		final var id = "id";
		final var sequenceNumber = "222";
		final var inReplyTo = "inReplyTo";
		final var createdByType = "type";
		final var createdByValue = "value";
		final var content = "content";

		final var entities = List.of(MessageEntity.create()
			.withId(id)
			.withSequenceNumber(sequenceNumber)
			.withInReplyTo(inReplyTo)
			.withCreatedBy(IdentifierEntity.create().withType(createdByType).withValue(createdByValue))
			.withContent(content));

		// Act
		final var result = Mapper.toMessages(entities);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo(id);
		assertThat(result.getFirst().getSequenceNumber()).isEqualTo(sequenceNumber);
		assertThat(result.getFirst().getInReplyTo()).isEqualTo(inReplyTo);
		assertThat(result.getFirst().getCreatedBy().getType()).isEqualTo(createdByType);
		assertThat(result.getFirst().getCreatedBy().getValue()).isEqualTo(createdByValue);
		assertThat(result.getFirst().getContent()).isEqualTo(content);
	}

	@Test
	void toMessage() {
		// Arrange
		final var id = "id";
		final var sequenceNumber = "222";
		final var inReplyTo = "inReplyTo";
		final var createdByType = "type";
		final var createdByValue = "value";
		final var content = "content";

		final var entity = MessageEntity.create()
			.withId(id)
			.withSequenceNumber(sequenceNumber)
			.withInReplyTo(inReplyTo)
			.withCreatedBy(IdentifierEntity.create().withType(createdByType).withValue(createdByValue))
			.withContent(content);

		// Act
		final var result = Mapper.toMessage(entity);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("created");
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getSequenceNumber()).isEqualTo(sequenceNumber);
		assertThat(result.getInReplyTo()).isEqualTo(inReplyTo);
		assertThat(result.getCreatedBy().getType()).isEqualTo(createdByType);
		assertThat(result.getCreatedBy().getValue()).isEqualTo(createdByValue);
		assertThat(result.getContent()).isEqualTo(content);
	}

	@Test
	void toMessageEntity() {
		// Arrange
		final var sequenceNumber = "222";
		final var inReplyTo = "inReplyTo";
		final var createdByType = "type";
		final var createdByValue = "value";
		final var content = "content";

		final var conversationEntity = ConversationEntity.create().withId("conversationId");
		final var message = Message.create()
			.withInReplyTo(inReplyTo)
			.withCreatedBy(Identifier.create().withType(createdByType).withValue(createdByValue))
			.withContent(content);

		// Act
		final var result = Mapper.toMessageEntity(conversationEntity, message);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id", "created", "attachments", "sequenceNumber");
		assertThat(result.getInReplyTo()).isEqualTo(inReplyTo);
		assertThat(result.getCreatedBy().getType()).isEqualTo(createdByType);
		assertThat(result.getCreatedBy().getValue()).isEqualTo(createdByValue);
		assertThat(result.getContent()).isEqualTo(content);
		assertThat(result.getConversation()).isEqualTo(conversationEntity);
	}

	@Test
	void toParticipants() {
		// Arrange
		final var type = "type";
		final var value = "value";

		final var entities = List.of(IdentifierEntity.create().withType(type).withValue(value));

		// Act
		final var result = Mapper.toParticipants(entities);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getType()).isEqualTo(type);
		assertThat(result.getFirst().getValue()).isEqualTo(value);
	}

	@Test
	void toParticipantsEmpty() {
		// Act
		final var result = Mapper.toParticipants(List.of());

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toParticipantsNull() {
		// Act
		final var result = Mapper.toParticipants(null);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toParticipant() {
		// Arrange
		final var type = "type";
		final var value = "value";

		final var entity = IdentifierEntity.create().withType(type).withValue(value);

		// Act
		final var result = Mapper.toParticipant(entity);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getType()).isEqualTo(type);
		assertThat(result.getValue()).isEqualTo(value);
	}

	@Test
	void toParticipantEmpty() {
		// Act
		final var result = Mapper.toParticipant(null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toParticipantEntities() {
		// Arrange
		final var type = "type";
		final var value = "value";

		final var participants = List.of(Identifier.create().withType(type).withValue(value));

		// Act
		final var result = Mapper.toParticipantEntities(participants);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getType()).isEqualTo(type);
		assertThat(result.getFirst().getValue()).isEqualTo(value);
	}

	@Test
	void toParticipantEntitiesEmpty() {
		// Act
		final var result = Mapper.toParticipantEntities(List.of());

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toParticipantEntitiesNull() {
		// Act
		final var result = Mapper.toParticipantEntities(null);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toParticipantEntity() {
		// Arrange
		final var type = "type";
		final var value = "value";

		final var participant = Identifier.create().withType(type).withValue(value);

		// Act
		final var result = Mapper.toParticipantEntity(participant);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getType()).isEqualTo(type);
		assertThat(result.getValue()).isEqualTo(value);
	}

	@Test
	void toParticipantEntityEmpty() {
		// Act
		final var result = Mapper.toParticipantEntity(null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toMetadata() {
		// Arrange
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";

		final var entity = MetadataEntity.create().withKey(key).withValues(List.of(value1, value2));

		// Act
		final var result = Mapper.toMetadata(entity);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getKey()).isEqualTo(key);
		assertThat(result.getValues()).containsExactly(value1, value2);
	}

	@Test
	void testToMetadata() {
		// Arrange
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";

		final var entities = List.of(MetadataEntity.create().withKey(key).withValues(List.of(value1, value2)));

		// Act
		final var result = Mapper.toMetadata(entities);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getKey()).isEqualTo(key);
		assertThat(result.getFirst().getValues()).containsExactly(value1, value2);
	}

	@Test
	void toMetadataEntity() {
		// Arrange
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";

		final var metadata = Metadata.create().withKey(key).withValues(List.of(value1, value2));

		// Act
		final var result = Mapper.toMetadataEntity(metadata);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(result.getKey()).isEqualTo(key);
		assertThat(result.getValues()).containsExactly(value1, value2);
	}

	@Test
	void toMetadataEntities() {
		// Arrange
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";

		final var metadataList = List.of(Metadata.create().withKey(key).withValues(List.of(value1, value2)));

		// Act
		final var result = Mapper.toMetadataEntities(metadataList);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getKey()).isEqualTo(key);
		assertThat(result.getFirst().getValues()).containsExactly(value1, value2);
	}

	@Test
	void toMetadataEntitiesEmpty() {
		// Act
		final var result = Mapper.toMetadataEntities(List.of());

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toMetadataEntitiesNull() {
		// Act
		final var result = Mapper.toMetadataEntities(null);

		// Assert
		assertThat(result).isEmpty();
	}
}
