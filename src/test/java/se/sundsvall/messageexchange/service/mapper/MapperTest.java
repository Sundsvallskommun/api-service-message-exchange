package se.sundsvall.messageexchange.service.mapper;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.mockStatic;
import static se.sundsvall.dept44.support.Identifier.Type.PARTY_ID;

import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.messageexchange.api.model.Conversation;
import se.sundsvall.messageexchange.api.model.Identifier;
import se.sundsvall.messageexchange.api.model.KeyValues;
import se.sundsvall.messageexchange.api.model.Message;
import se.sundsvall.messageexchange.api.model.ReadBy;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;
import se.sundsvall.messageexchange.integration.db.model.ExternalReferencesEntity;
import se.sundsvall.messageexchange.integration.db.model.IdentifierEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageType;
import se.sundsvall.messageexchange.integration.db.model.MetadataEntity;
import se.sundsvall.messageexchange.integration.db.model.ReadByEntity;
import se.sundsvall.messageexchange.integration.db.model.SequenceEntity;

@ExtendWith(MockitoExtension.class)
class MapperTest {

	@Test
	void toConversations() {

		// Arrange
		final var id = "id";
		final var municipalityId = "municipalityId";
		final var namespace = "namespace";
		final var topic = "topic";
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";
		final var type = "type";
		final var value = "value";
		final var externalReferenceId = "externalReferenceId";
		final var externalReferenceType = "externalReferenceType";

		final var entity = ConversationEntity.create()
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withNamespace(namespace)
			.withTopic(topic)
			.withParticipants(List.of(IdentifierEntity.create().withType(type).withValue(value)))
			.withMetadata(List.of(MetadataEntity.create().withKey(key).withValues(List.of(value1, value2))))
			.withExternalReferences(List.of(ExternalReferencesEntity.create().withKey(externalReferenceId).withValues(List.of(externalReferenceType))));

		// Act
		final var result = Mapper.toConversations(List.of(entity));

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).isNotNull().hasNoNullFieldsOrPropertiesExcept("messages", "latestSequenceNumber");
		assertThat(result.getFirst().getId()).isEqualTo(id);
		assertThat(result.getFirst().getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getFirst().getNamespace()).isEqualTo(namespace);
		assertThat(result.getFirst().getTopic()).isEqualTo(topic);
		assertThat(result.getFirst().getParticipants()).hasSize(1);
		assertThat(result.getFirst().getMetadata()).hasSize(1);
		assertThat(result.getFirst().getExternalReferences()).hasSize(1);
		assertThat(result.getFirst().getExternalReferences().getFirst().getKey()).isEqualTo(externalReferenceId);
		assertThat(result.getFirst().getExternalReferences().getFirst().getValues()).containsExactly(externalReferenceType);
	}

	@Test
	void toConversationsEmpty() {
		// Act
		final var result = Mapper.toConversations(List.of());

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toConversationsNull() {
		// Act
		final var result = Mapper.toConversations(null);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toConversationEntity() {
		// Arrange
		final var municipalityId = "municipalityId";
		final var namespace = "namespace";
		final var topic = "topic";
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";
		final var type = "type";
		final var value = "value";
		final var externalReferenceId = "externalReferenceId";
		final var externalReferenceType = "externalReferenceType";

		final var conversationRequest = Conversation.create()
			.withTopic(topic)
			.withParticipants(List.of(Identifier.create().withType(type).withValue(value)))
			.withMetadata(List.of(KeyValues.create().withKey(key).withValues(List.of(value1, value2))))
			.withExternalReferences(List.of(KeyValues.create().withKey(externalReferenceId).withValues(List.of(externalReferenceType))));

		// Act
		final var result = Mapper.toConversationEntity(municipalityId, namespace, conversationRequest);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id", "messages");
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getNamespace()).isEqualTo(namespace);
		assertThat(result.getTopic()).isEqualTo(topic);
		assertThat(result.getParticipants()).hasSize(1);
		assertThat(result.getMetadata()).hasSize(1);
		assertThat(result.getExternalReferences()).hasSize(1);
		assertThat(result.getExternalReferences().getFirst().getKey()).isEqualTo(externalReferenceId);
		assertThat(result.getExternalReferences().getFirst().getValues()).containsExactly(externalReferenceType);
	}

	@Test
	void toConversation() {
		// Arrange
		final var id = "id";
		final var municipalityId = "municipalityId";
		final var namespace = "namespace";
		final var topic = "topic";
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";
		final var type = "type";
		final var value = "value";
		final var externalReferenceId = "externalReferenceId";
		final var externalReferenceType = "externalReferenceType";

		final var entity = ConversationEntity.create()
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withNamespace(namespace)
			.withTopic(topic)
			.withParticipants(List.of(IdentifierEntity.create().withType(type).withValue(value)))
			.withMetadata(List.of(MetadataEntity.create().withKey(key).withValues(List.of(value1, value2))))
			.withExternalReferences(List.of(ExternalReferencesEntity.create().withKey(externalReferenceId).withValues(List.of(externalReferenceType))));

		// Act
		final var result = Mapper.toConversation(entity);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("messages", "latestSequenceNumber");
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getNamespace()).isEqualTo(namespace);
		assertThat(result.getTopic()).isEqualTo(topic);
		assertThat(result.getParticipants()).hasSize(1);
		assertThat(result.getMetadata()).hasSize(1);
		assertThat(result.getExternalReferences()).hasSize(1);
		assertThat(result.getExternalReferences().getFirst().getKey()).isEqualTo(externalReferenceId);
		assertThat(result.getExternalReferences().getFirst().getValues()).containsExactly(externalReferenceType);
	}

	@Test
	void updateConversationEntity() {
		// Arrange
		final var topic = "topic";
		final var type = "type";
		final var value = "value";
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";
		final var externalReferenceId = "externalReferenceId";
		final var externalReferenceType = "externalReferenceType";

		final var conversationRequest = Conversation.create()
			.withTopic(topic)
			.withParticipants(List.of(Identifier.create().withType(type).withValue(value)))
			.withMetadata(List.of(KeyValues.create().withKey(key).withValues(List.of(value1, value2))))
			.withExternalReferences(List.of(KeyValues.create().withKey(externalReferenceId).withValues(List.of(externalReferenceType))));

		final var entity = ConversationEntity.create();

		// Act
		final var result = Mapper.updateConversationEntity(entity, conversationRequest);

		// Assert
		assertThat(result.getTopic()).isEqualTo(topic);
		assertThat(result.getParticipants()).hasSize(1);
		assertThat(result.getMetadata()).hasSize(1);
		assertThat(result.getExternalReferences()).hasSize(1);
	}

	@Test
	void toMessages() {
		// Arrange
		final var id = "id";
		final var sequenceNumber = SequenceEntity.create().withId(222L);
		final var inReplyTo = "inReplyTo";
		final var createdByType = "type";
		final var createdByValue = "value";
		final var content = "content";
		final var type = MessageType.SYSTEM_CREATED;

		final var entities = List.of(MessageEntity.create()
			.withId(id)
			.withSequenceNumber(sequenceNumber)
			.withInReplyToMessageId(inReplyTo)
			.withCreatedBy(IdentifierEntity.create().withType(createdByType).withValue(createdByValue))
			.withContent(content)
			.withType(type));

		// Act
		final var result = Mapper.toMessages(entities);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo(id);
		assertThat(result.getFirst().getSequenceNumber()).isEqualTo(sequenceNumber.getId());
		assertThat(result.getFirst().getInReplyToMessageId()).isEqualTo(inReplyTo);
		assertThat(result.getFirst().getCreatedBy().getType()).isEqualTo(createdByType);
		assertThat(result.getFirst().getCreatedBy().getValue()).isEqualTo(createdByValue);
		assertThat(result.getFirst().getContent()).isEqualTo(content);
		assertThat(result.getFirst().getType()).isEqualTo(se.sundsvall.messageexchange.api.model.MessageType.SYSTEM_CREATED);
	}

	@Test
	void toMessage() {
		// Arrange
		final var id = "id";
		final var sequenceNumber = SequenceEntity.create().withId(222L);
		final var inReplyTo = "inReplyTo";
		final var createdByType = "type";
		final var createdByValue = "value";
		final var content = "content";
		final var type = MessageType.SYSTEM_CREATED;

		final var entity = MessageEntity.create()
			.withId(id)
			.withSequenceNumber(sequenceNumber)
			.withInReplyToMessageId(inReplyTo)
			.withCreatedBy(IdentifierEntity.create().withType(createdByType).withValue(createdByValue))
			.withContent(content)
			.withType(type);

		// Act
		final var result = Mapper.toMessage(entity);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("created");
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getSequenceNumber()).isEqualTo(sequenceNumber.getId());
		assertThat(result.getInReplyToMessageId()).isEqualTo(inReplyTo);
		assertThat(result.getCreatedBy().getType()).isEqualTo(createdByType);
		assertThat(result.getCreatedBy().getValue()).isEqualTo(createdByValue);
		assertThat(result.getContent()).isEqualTo(content);
		assertThat(result.getType()).isEqualTo(se.sundsvall.messageexchange.api.model.MessageType.SYSTEM_CREATED);
	}

	@Test
	void toMessageEntity() {
		// Arrange
		final var inReplyTo = "inReplyTo";
		final var createdByType = "PARTY_ID";
		final var createdByValue = "value";
		final var content = "content";
		final var dept44Identifier = se.sundsvall.dept44.support.Identifier.create()
			.withType(PARTY_ID)
			.withValue(createdByValue);

		try (final var mockedStatic = mockStatic(se.sundsvall.dept44.support.Identifier.class)) {
			mockedStatic.when(se.sundsvall.dept44.support.Identifier::get).thenReturn(dept44Identifier);

			final var conversationEntity = ConversationEntity.create().withId("conversationId");
			final var message = Message.create()
				.withInReplyToMessageId(inReplyTo)
				.withCreatedBy(Identifier.create().withType(createdByType).withValue(createdByValue))
				.withContent(content);

			// Act
			final var result = Mapper.toMessageEntity(conversationEntity, message);

			// Assert
			assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id", "created", "attachments", "sequenceNumber");
			assertThat(result.getInReplyToMessageId()).isEqualTo(inReplyTo);
			assertThat(result.getCreatedBy().getType()).isEqualTo(createdByType);
			assertThat(result.getCreatedBy().getValue()).isEqualTo(createdByValue);
			assertThat(result.getContent()).isEqualTo(content);
			assertThat(result.getConversation()).isEqualTo(conversationEntity);

		}
	}

	@Test
	void toIdentifiers() {
		// Arrange
		final var type = "type";
		final var value = "value";

		final var entities = List.of(IdentifierEntity.create().withType(type).withValue(value));

		// Act
		final var result = Mapper.toIdentifiers(entities);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getType()).isEqualTo(type);
		assertThat(result.getFirst().getValue()).isEqualTo(value);
	}

	@Test
	void toIdentifiersEmpty() {
		// Act
		final var result = Mapper.toIdentifiers(List.of());

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toIdentifiersNull() {
		// Act
		final var result = Mapper.toIdentifiers(null);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toIdentifier() {
		// Arrange
		final var type = "type";
		final var value = "value";

		final var entity = IdentifierEntity.create().withType(type).withValue(value);

		// Act
		final var result = Mapper.toIdentifier(entity);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getType()).isEqualTo(type);
		assertThat(result.getValue()).isEqualTo(value);
	}

	@Test
	void toIdentifierEmpty() {
		// Act
		final var result = Mapper.toIdentifier(null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toIdentifierEntities() {
		// Arrange
		final var type = "type";
		final var value = "value";

		final var participants = List.of(Identifier.create().withType(type).withValue(value));

		// Act
		final var result = Mapper.toIdentifierEntities(participants);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getType()).isEqualTo(type);
		assertThat(result.getFirst().getValue()).isEqualTo(value);
	}

	@Test
	void toIdentifierEntitiesEmpty() {
		// Act
		final var result = Mapper.toIdentifierEntities(List.of());

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toIdentifierEntitiesNull() {
		// Act
		final var result = Mapper.toIdentifierEntities(null);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toIdentifierEntity() {
		// Arrange
		final var type = "type";
		final var value = "value";

		final var participant = Identifier.create().withType(type).withValue(value);

		// Act
		final var result = Mapper.toIdentifierEntity(participant);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(result.getType()).isEqualTo(type);
		assertThat(result.getValue()).isEqualTo(value);
	}

	@Test
	void toIdentifierEntityEmpty() {
		// Act
		final var result = Mapper.toIdentifierEntity((Identifier) null);

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

		final var metadata = KeyValues.create().withKey(key).withValues(List.of(value1, value2));

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

		final var metadataList = List.of(KeyValues.create().withKey(key).withValues(List.of(value1, value2)));

		// Act
		final var result = Mapper.toMetadataEntities(metadataList);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
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

	@Test
	void toReadByEntitiesFromDept44Identifier() {

		// Arrange
		final var type = "PARTY_ID";
		final var value = "value";
		final var identifier = se.sundsvall.dept44.support.Identifier.create()
			.withType(PARTY_ID)
			.withValue(value);

		// Act
		final var result = Mapper.toReadByEntities(identifier);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(result.getFirst().getIdentifier().getType()).isEqualTo(type);
		assertThat(result.getFirst().getIdentifier().getValue()).isEqualTo(value);
	}

	@Test
	void toIdentifierEntityFromDept44Identifier() {
		// Arrange
		final var type = "PARTY_ID";
		final var value = "value";

		final var identifier = se.sundsvall.dept44.support.Identifier.create()
			.withType(PARTY_ID)
			.withValue(value);

		// Act
		final var result = Mapper.toIdentifierEntity(identifier);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(result.getType()).isEqualTo(type);
		assertThat(result.getValue()).isEqualTo(value);
	}

	@Test
	void toIdentifierEntityFromDept44IdentifierButNull() {
		// Act
		final var result = Mapper.toIdentifierEntity((se.sundsvall.dept44.support.Identifier) null);

		// Assert
		assertThat(result).isNull();

	}

	@Test
	void toReadByEntityFromDept44Identifier() {
		// Arrange
		final var type = "PARTY_ID";
		final var value = "value";

		final var identifier = se.sundsvall.dept44.support.Identifier.create()
			.withType(PARTY_ID)
			.withValue(value);

		// Act
		final var result = Mapper.toReadByEntity(identifier);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(result.getIdentifier().getType()).isEqualTo(type);
		assertThat(result.getIdentifier().getValue()).isEqualTo(value);
		assertThat(result.getReadAt()).isCloseTo(OffsetDateTime.now(), within(5, SECONDS));
	}

	@Test
	void toReadByListEntityFromDept44IdentifierButNull() {
		// Act
		final var result = Mapper.toReadByEntity((se.sundsvall.dept44.support.Identifier) null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toReadByListEntityFromReadByList() {
		// Arrange
		final var type = "PARTY_ID";
		final var value = "value";

		final var readBy = ReadBy.create()
			.withIdentifier(Identifier.create()
				.withType(type)
				.withValue(value))
			.withReadAt(OffsetDateTime.now());

		// Act
		final var result = Mapper.toReadByEntities(List.of(readBy));

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(result.getFirst().getIdentifier().getType()).isEqualTo(type);
		assertThat(result.getFirst().getIdentifier().getValue()).isEqualTo(value);
		assertThat(result.getFirst().getReadAt()).isCloseTo(OffsetDateTime.now(), within(5, SECONDS));
	}

	@Test
	void toReadByListEntityFromReadByListButNull() {
		// Act
		final var result = Mapper.toReadByEntities((List<ReadBy>) null);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toReadByEntitiesEmpty() {
		// Act
		final var result = Mapper.toReadByEntities(List.of());

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toReadByEntityFromReadByList() {
		// Arrange
		final var type = "PARTY_ID";
		final var value = "value";

		final var readBy = ReadBy.create()
			.withIdentifier(Identifier.create()
				.withType(type)
				.withValue(value))
			.withReadAt(OffsetDateTime.now());

		// Act
		final var result = Mapper.toReadByEntity(readBy);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(result.getIdentifier().getType()).isEqualTo(type);
		assertThat(result.getIdentifier().getValue()).isEqualTo(value);
		assertThat(result.getReadAt()).isCloseTo(OffsetDateTime.now(), within(5, SECONDS));
	}

	@Test
	void toReadByEntitiesNull() {
		// Act
		final var result = Mapper.toReadByEntity((ReadBy) null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toReadByList() {
		// Arrange
		final var type = "PARTY_ID";
		final var value = "value";

		final var readByEntity = ReadByEntity.create()
			.withIdentifier(IdentifierEntity.create()
				.withType(type)
				.withValue(value))
			.withReadAt(OffsetDateTime.now());

		// Act
		final var result = Mapper.toReadByList(List.of(readByEntity));

		// Assert
		assertThat(result).isNotNull().hasSize(1);
		assertThat(result.getFirst()).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getFirst().getIdentifier().getType()).isEqualTo(type);
		assertThat(result.getFirst().getIdentifier().getValue()).isEqualTo(value);
		assertThat(result.getFirst().getReadAt()).isCloseTo(OffsetDateTime.now(), within(5, SECONDS));
	}

	@Test
	void toReadByListNull() {
		// Act
		final var result = Mapper.toReadByList(null);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toReadByListEmpty() {
		// Act
		final var result = Mapper.toReadByList(List.of());

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toReadBy() {
		// Arrange
		final var type = "PARTY_ID";
		final var value = "value";

		final var readByEntity = ReadByEntity.create()
			.withIdentifier(IdentifierEntity.create()
				.withType(type)
				.withValue(value))
			.withReadAt(OffsetDateTime.now());

		// Act
		final var result = Mapper.toReadBy(readByEntity);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getIdentifier().getType()).isEqualTo(type);
		assertThat(result.getIdentifier().getValue()).isEqualTo(value);
		assertThat(result.getReadAt()).isCloseTo(OffsetDateTime.now(), within(5, SECONDS));
	}

	@Test
	void toReadByNull() {
		// Act
		final var result = Mapper.toReadBy(null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toExternalReferencesEntities() {

		// Arrange
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";
		final var externalReferences = KeyValues.create().withKey(key).withValues(List.of(value1, value2));

		// Act
		final var result = Mapper.toExternalReferencesEntities(List.of(externalReferences));

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(result.getFirst().getKey()).isEqualTo(key);
		assertThat(result.getFirst().getValues()).containsExactly(value1, value2);

	}

	@Test
	void toExternalReferencesEntitiesNull() {

		// Act
		final var result = Mapper.toExternalReferencesEntities(null);

		// Assert
		assertThat(result).isEmpty();

	}

	@Test
	void toExternalReferencesEntitiesEmpty() {

		// Act
		final var result = Mapper.toExternalReferencesEntities(List.of());

		// Assert
		assertThat(result).isEmpty();

	}

	@Test
	void toExternalReferencesEntity() {

		// Arrange
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";
		final var externalReferences = KeyValues.create().withKey(key).withValues(List.of(value1, value2));

		// Act
		final var result = Mapper.toExternalReferencesEntity(externalReferences);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(result.getKey()).isEqualTo(key);
		assertThat(result.getValues()).containsExactly(value1, value2);

	}

	@Test
	void toExternalReferencesEntityNull() {

		// Act
		final var result = Mapper.toExternalReferencesEntity(null);

		// Assert
		assertThat(result).isNull();

	}

	@Test
	void toExternalReferences() {

		// Arrange
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";
		final var externalReferencesEntities = List.of(ExternalReferencesEntity.create().withKey(key).withValues(List.of(value1, value2)));

		// Act
		final var result = Mapper.toExternalReferences(externalReferencesEntities);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getFirst().getKey()).isEqualTo(key);
		assertThat(result.getFirst().getValues()).containsExactly(value1, value2);

	}

	@Test
	void toExternalReferencesNull() {

		// Act
		final var result = Mapper.toExternalReferences(null);

		// Assert
		assertThat(result).isEmpty();

	}

	@Test
	void toExternalReferencesEmpty() {

		// Act
		final var result = Mapper.toExternalReferences(List.of());

		// Assert
		assertThat(result).isEmpty();

	}

	@Test
	void toExternalReference() {

		// Arrange
		final var key = "key";
		final var value1 = "value1";
		final var value2 = "value2";
		final var externalReferencesEntity = ExternalReferencesEntity.create().withKey(key).withValues(List.of(value1, value2));

		// Act
		final var result = Mapper.toExternalReference(externalReferencesEntity);

		// // Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getKey()).isEqualTo(key);
		assertThat(result.getValues()).containsExactly(value1, value2);

	}

	@Test
	void toExternalReferenceNull() {

		// Act
		final var result = Mapper.toExternalReference(null);

		// Assert
		assertThat(result).isNull();

	}

	@Test
	void toExternalReferenceEmpty() {

		// Act
		final var result = Mapper.toExternalReference(ExternalReferencesEntity.create());

		// Assert
		assertThat(result.getKey()).isNull();
		assertThat(result.getValues()).isNull();

	}

}
