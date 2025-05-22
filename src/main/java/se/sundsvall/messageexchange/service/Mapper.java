package se.sundsvall.messageexchange.service;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import se.sundsvall.messageexchange.api.model.Conversation;
import se.sundsvall.messageexchange.api.model.Identifier;
import se.sundsvall.messageexchange.api.model.KeyValues;
import se.sundsvall.messageexchange.api.model.Message;
import se.sundsvall.messageexchange.api.model.ReadBy;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;
import se.sundsvall.messageexchange.integration.db.model.ExternalReferencesEntity;
import se.sundsvall.messageexchange.integration.db.model.IdentifierEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;
import se.sundsvall.messageexchange.integration.db.model.MetadataEntity;
import se.sundsvall.messageexchange.integration.db.model.ReadByEntity;

public final class Mapper {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Mapper.class);

	private Mapper() {
		// Prevent instantiation
	}

	static ConversationEntity toConversationEntity(final String municipalityId, final String namespace, final Conversation conversation) {

		return ConversationEntity.create()
			.withParticipants(toIdentifierEntities(conversation.getParticipants()))
			.withMunicipalityId(municipalityId)
			.withNamespace(namespace)
			.withMetadata(toMetadataEntities(conversation.getMetadata()))
			.withExternalReferences(toExternalReferencesEntities(conversation.getExternalReferences()))
			.withTopic(conversation.getTopic());
	}

	static Conversation toConversation(final ConversationEntity entity) {

		return Conversation.create()
			.withId(entity.getId())
			.withParticipants(toIdentifiers(entity.getParticipants()))
			.withMunicipalityId(entity.getMunicipalityId())
			.withNamespace(entity.getNamespace())
			.withMetadata(toMetadata(entity.getMetadata()))
			.withExternalReferences(toExternalReferences(entity.getExternalReferences()))
			.withTopic(entity.getTopic());
	}

	static ConversationEntity updateConversationEntity(final ConversationEntity entity, final Conversation conversation) {

		Optional.ofNullable(conversation.getTopic()).ifPresent(entity::setTopic);
		Optional.ofNullable(conversation.getParticipants()).ifPresent(participants -> updateParticipants(entity, participants));
		Optional.ofNullable(conversation.getMetadata()).ifPresent(metaData -> updateMetadata(entity, metaData));
		Optional.ofNullable(conversation.getExternalReferences()).ifPresent(externalReferences -> updateExternalReferences(entity, externalReferences));

		return entity;
	}

	private static void updateExternalReferences(final ConversationEntity entity, final List<KeyValues> externalReferences) {
		ofNullable(entity.getExternalReferences()).ifPresentOrElse(List::clear, () -> entity.setExternalReferences(new ArrayList<>()));
		entity.getExternalReferences().addAll(toExternalReferencesEntities(externalReferences));
	}

	private static void updateParticipants(final ConversationEntity entity, final List<Identifier> identifiers) {
		ofNullable(entity.getParticipants()).ifPresentOrElse(List::clear, () -> entity.setParticipants(new ArrayList<>()));
		entity.getParticipants().addAll(toIdentifierEntities(identifiers));
	}

	private static void updateMetadata(final ConversationEntity entity, final List<KeyValues> metadata) {
		ofNullable(entity.getMetadata()).ifPresentOrElse(List::clear, () -> entity.setMetadata(new ArrayList<>()));
		entity.getMetadata().addAll(toMetadataEntities(metadata));
	}

	static List<Message> toMessages(final List<MessageEntity> entities) {
		return entities.stream()
			.map(Mapper::toMessage)
			.toList();
	}

	static Message toMessage(final MessageEntity entity) {

		return Message.create()
			.withId(entity.getId())
			.withSequenceNumber(entity.getSequenceNumber())
			.withInReplyToMessageId(entity.getInReplyToMessageId())
			.withCreated(entity.getCreated())
			.withCreatedBy(toIdentifier(entity.getCreatedBy()))
			.withContent(entity.getContent())
			.withReadBy(toReadByList(entity.getReadBy()))
			.withAttachments(AttachmentMapper.toAttachments(entity.getAttachments()));
	}

	static MessageEntity toMessageEntity(final ConversationEntity entity, final Message message) {

		final var identifier = se.sundsvall.dept44.support.Identifier.get();

		return MessageEntity.create()
			.withInReplyToMessageId(message.getInReplyToMessageId())
			.withCreatedBy(toIdentifierEntity(identifier))
			.withReadBy(toReadByEntities(identifier))
			.withContent(message.getContent())
			.withConversation(entity);
	}

	static List<Identifier> toIdentifiers(final List<IdentifierEntity> entities) {
		return Optional.ofNullable(entities)
			.orElse(emptyList()).stream()
			.map(Mapper::toIdentifier)
			.toList();
	}

	static Identifier toIdentifier(final IdentifierEntity entity) {
		return Optional.ofNullable(entity)
			.map(p -> Identifier.create()
				.withType(p.getType())
				.withValue(p.getValue()))
			.orElse(null);
	}

	static List<IdentifierEntity> toIdentifierEntities(final List<Identifier> identifiers) {
		return Optional.ofNullable(identifiers)
			.orElse(emptyList()).stream()
			.map(Mapper::toIdentifierEntity)
			.toList();
	}

	static IdentifierEntity toIdentifierEntity(final Identifier identifier) {
		return Optional.ofNullable(identifier)
			.map(p -> IdentifierEntity.create()
				.withType(p.getType())
				.withValue(p.getValue()))
			.orElse(null);
	}

	static KeyValues toMetadata(final MetadataEntity entity) {
		return KeyValues.create()
			.withKey(entity.getKey())
			.withValues(entity.getValues());
	}

	static List<KeyValues> toMetadata(final List<MetadataEntity> entities) {
		return Optional.ofNullable(entities)
			.orElse(emptyList()).stream()
			.map(Mapper::toMetadata)
			.toList();
	}

	static MetadataEntity toMetadataEntity(final KeyValues keyValues) {
		return MetadataEntity.create()
			.withKey(keyValues.getKey())
			.withValues(keyValues.getValues());
	}

	static List<MetadataEntity> toMetadataEntities(final List<KeyValues> metadata) {
		return Optional.ofNullable(metadata).orElse(emptyList()).stream()
			.map(Mapper::toMetadataEntity)
			.toList();
	}

	static List<ReadByEntity> toReadByEntities(final se.sundsvall.dept44.support.Identifier identifier) {
		return Optional.ofNullable(identifier)
			.map(entity -> List.of(toReadByEntity(entity)))
			.orElse(null);
	}

	static ReadByEntity toReadByEntity(final se.sundsvall.dept44.support.Identifier identifier) {
		return Optional.ofNullable(identifier)
			.map(p -> ReadByEntity.create()
				.withIdentifier(toIdentifierEntity(identifier))
				.withReadAt(OffsetDateTime.now()))
			.orElse(null);
	}

	static IdentifierEntity toIdentifierEntity(final se.sundsvall.dept44.support.Identifier identifier) {

		return Optional.ofNullable(identifier)
			.map(p -> IdentifierEntity.create()
				.withType(p.getType().name())
				.withValue(p.getValue()))
			.orElse(null);
	}

	static List<ReadByEntity> toReadByEntities(final List<ReadBy> readBy) {

		return Optional.ofNullable(readBy)
			.orElse(emptyList()).stream()
			.map(Mapper::toReadByEntity)
			.toList();
	}

	static ReadByEntity toReadByEntity(final ReadBy readBy) {

		return Optional.ofNullable(readBy)
			.map(r -> ReadByEntity.create()
				.withIdentifier(toIdentifierEntity(r.getIdentifier()))
				.withReadAt(r.getReadAt()))
			.orElse(null);
	}

	static List<ReadBy> toReadByList(final List<ReadByEntity> readBy) {
		return Optional.ofNullable(readBy)
			.orElse(emptyList()).stream()
			.map(Mapper::toReadBy)
			.toList();
	}

	static ReadBy toReadBy(final ReadByEntity entity) {

		return Optional.ofNullable(entity)
			.map(e -> ReadBy.create()
				.withIdentifier(toIdentifier(e.getIdentifier()))
				.withReadAt(entity.getReadAt()))
			.orElse(null);
	}

	static List<ExternalReferencesEntity> toExternalReferencesEntities(final List<KeyValues> externalReferences) {
		return Optional.ofNullable(externalReferences)
			.orElse(emptyList()).stream()
			.map(Mapper::toExternalReferencesEntity)
			.toList();
	}

	static ExternalReferencesEntity toExternalReferencesEntity(final KeyValues keyValues) {
		return Optional.ofNullable(keyValues)
			.map(p -> ExternalReferencesEntity.create()
				.withKey(keyValues.getKey())
				.withValues(keyValues.getValues()))
			.orElse(null);
	}

	static List<KeyValues> toExternalReferences(final List<ExternalReferencesEntity> externalReferences) {
		return Optional.ofNullable(externalReferences)
			.orElse(emptyList()).stream()
			.map(Mapper::toExternalReference)
			.toList();
	}

	static KeyValues toExternalReference(final ExternalReferencesEntity externalReferencesEntity) {
		return Optional.ofNullable(externalReferencesEntity)
			.map(e -> KeyValues.create()
				.withKey(e.getKey())
				.withValues(e.getValues()))
			.orElse(null);
	}

}
