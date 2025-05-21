package se.sundsvall.chat.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.chat.api.model.ConversationRequest;
import se.sundsvall.chat.api.model.MessageRequest;
import se.sundsvall.chat.api.model.MetaData;
import se.sundsvall.chat.api.model.Participant;
import se.sundsvall.chat.integration.db.model.ConversationEntity;
import se.sundsvall.chat.integration.db.model.MessageEntity;
import se.sundsvall.chat.integration.db.model.MetaDataEntity;
import se.sundsvall.chat.integration.db.model.ParticipantEntity;

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

		final var conversationRequest = ConversationRequest.create()
			.withChannelId(channelId)
			.withTopic(topic)
			.withParticipants(List.of(Participant.create().withType(type).withValue(value)))
			.withMetaData(List.of(MetaData.create().withKey(key).withValues(List.of(value1, value2))));

		// Act
		final var result = Mapper.toConversationEntity(municipalityId, namespace, conversationRequest);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id", "messages");
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getNamespace()).isEqualTo(namespace);
		assertThat(result.getChannelId()).isEqualTo(channelId);
		assertThat(result.getTopic()).isEqualTo(topic);
		assertThat(result.getParticipants()).hasSize(1);
		assertThat(result.getMetaData()).hasSize(1);
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
			.withParticipants(List.of(ParticipantEntity.create().withType(type).withValue(value)))
			.withMetaData(List.of(MetaDataEntity.create().withKey(key).withValues(List.of(value1, value2))));

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
		assertThat(result.getMetaData()).hasSize(1);
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

		final var conversationRequest = ConversationRequest.create()
			.withChannelId(channelId)
			.withTopic(topic)
			.withParticipants(List.of(Participant.create().withType(type).withValue(value)))
			.withMetaData(List.of(MetaData.create().withKey(key).withValues(List.of(value1, value2))));

		final var entity = ConversationEntity.create();

		// Act
		final var result = Mapper.updateConversationEntity(entity, conversationRequest);

		// Assert
		assertThat(result.getChannelId()).isEqualTo(channelId);
		assertThat(result.getTopic()).isEqualTo(topic);
		assertThat(result.getParticipants()).hasSize(1);
		assertThat(result.getMetaData()).hasSize(1);
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
			.withCreatedBy(ParticipantEntity.create().withType(createdByType).withValue(createdByValue))
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
			.withCreatedBy(ParticipantEntity.create().withType(createdByType).withValue(createdByValue))
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
		final var messageRequest = MessageRequest.create()
			.withSequenceNumber(sequenceNumber)
			.withInReplyTo(inReplyTo)
			.withCreatedBy(Participant.create().withType(createdByType).withValue(createdByValue))
			.withContent(content);

		// Act
		final var result = Mapper.toMessageEntity(conversationEntity, messageRequest);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id", "created", "attachments");
		assertThat(result.getSequenceNumber()).isEqualTo(sequenceNumber);
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

		final var entities = List.of(ParticipantEntity.create().withType(type).withValue(value));

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

		final var entity = ParticipantEntity.create().withType(type).withValue(value);

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

		final var participants = List.of(Participant.create().withType(type).withValue(value));

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

		final var participant = Participant.create().withType(type).withValue(value);

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
	void toMetaData() {
		// Arrange
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";

		final var entity = MetaDataEntity.create().withKey(key).withValues(List.of(value1, value2));

		// Act
		final var result = Mapper.toMetaData(entity);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getKey()).isEqualTo(key);
		assertThat(result.getValues()).containsExactly(value1, value2);
	}

	@Test
	void testToMetaData() {
		// Arrange
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";

		final var entities = List.of(MetaDataEntity.create().withKey(key).withValues(List.of(value1, value2)));

		// Act
		final var result = Mapper.toMetaData(entities);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getKey()).isEqualTo(key);
		assertThat(result.getFirst().getValues()).containsExactly(value1, value2);
	}

	@Test
	void toMetaDataEntity() {
		// Arrange
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";

		final var metaData = MetaData.create().withKey(key).withValues(List.of(value1, value2));

		// Act
		final var result = Mapper.toMetaDataEntity(metaData);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(result.getKey()).isEqualTo(key);
		assertThat(result.getValues()).containsExactly(value1, value2);
	}

	@Test
	void toMetaDataEntities() {
		// Arrange
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";

		final var metaDataList = List.of(MetaData.create().withKey(key).withValues(List.of(value1, value2)));

		// Act
		final var result = Mapper.toMetaDataEntities(metaDataList);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getKey()).isEqualTo(key);
		assertThat(result.getFirst().getValues()).containsExactly(value1, value2);
	}

	@Test
	void toMetaDataEntitiesEmpty() {
		// Act
		final var result = Mapper.toMetaDataEntities(List.of());

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toMetaDataEntitiesNull() {
		// Act
		final var result = Mapper.toMetaDataEntities(null);

		// Assert
		assertThat(result).isEmpty();
	}
}
