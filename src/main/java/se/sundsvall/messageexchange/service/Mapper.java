package se.sundsvall.messageexchange.service;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import se.sundsvall.messageexchange.api.model.Conversation;
import se.sundsvall.messageexchange.api.model.Identifier;
import se.sundsvall.messageexchange.api.model.Message;
import se.sundsvall.messageexchange.api.model.Metadata;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;
import se.sundsvall.messageexchange.integration.db.model.IdentifierEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;
import se.sundsvall.messageexchange.integration.db.model.MetadataEntity;

public final class Mapper {

	private Mapper() {
		// Prevent instantiation
	}

	static ConversationEntity toConversationEntity(final String municipalityId, final String namespace, final Conversation conversation) {

		return ConversationEntity.create()
			.withParticipants(toParticipantEntities(conversation.getParticipants()))
			.withMunicipalityId(municipalityId)
			.withNamespace(namespace)
			.withChannelId(conversation.getChannelId())
			.withMetadata(toMetadataEntities(conversation.getMetadata()))
			.withTopic(conversation.getTopic());
	}

	static Conversation toConversation(final ConversationEntity entity) {

		return Conversation.create()
			.withId(entity.getId())
			.withParticipants(toParticipants(entity.getParticipants()))
			.withMunicipalityId(entity.getMunicipalityId())
			.withNamespace(entity.getNamespace())
			.withChannelId(entity.getChannelId())
			.withMetadata(toMetadata(entity.getMetadata()))
			.withTopic(entity.getTopic());
	}

	static ConversationEntity updateConversationEntity(final ConversationEntity entity, final Conversation conversation) {

		Optional.ofNullable(conversation.getChannelId()).ifPresent(entity::setChannelId);
		Optional.ofNullable(conversation.getTopic()).ifPresent(entity::setTopic);
		Optional.ofNullable(conversation.getParticipants()).ifPresent(participants -> updateParticipants(entity, participants));
		Optional.ofNullable(conversation.getMetadata()).ifPresent(metaData -> updateMetadata(entity, metaData));
		return entity;
	}

	private static void updateParticipants(final ConversationEntity entity, final List<Identifier> identifiers) {
		ofNullable(entity.getParticipants()).ifPresentOrElse(List::clear, () -> entity.setParticipants(new ArrayList<>()));
		entity.getParticipants().addAll(toParticipantEntities(identifiers));
	}

	private static void updateMetadata(final ConversationEntity entity, final List<Metadata> metadata) {
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
			.withInReplyTo(entity.getInReplyTo())
			.withCreated(entity.getCreated())
			.withCreatedBy(toParticipant(entity.getCreatedBy()))
			.withContent(entity.getContent())
			.withReadBy(toParticipants(entity.getReadBy()))
			.withAttachments(AttachmentMapper.toAttachments(entity.getAttachments()));
	}

	static MessageEntity toMessageEntity(final ConversationEntity entity, final Message message) {

		return MessageEntity.create()
			.withInReplyTo(message.getInReplyTo())
			.withCreatedBy(toParticipantEntity(message.getCreatedBy()))
			.withContent(message.getContent())
			.withConversation(entity)
			.withReadBy(toParticipantEntities(message.getReadBy()));
	}

	static List<Identifier> toParticipants(final List<IdentifierEntity> entities) {
		return Optional.ofNullable(entities)
			.orElse(emptyList()).stream()
			.map(Mapper::toParticipant)
			.toList();
	}

	static Identifier toParticipant(final IdentifierEntity entity) {
		return Optional.ofNullable(entity)
			.map(p -> Identifier.create()
				.withType(p.getType())
				.withValue(p.getValue()))
			.orElse(null);
	}

	static List<IdentifierEntity> toParticipantEntities(final List<Identifier> identifiers) {
		return Optional.ofNullable(identifiers)
			.orElse(emptyList()).stream()
			.map(Mapper::toParticipantEntity)
			.toList();
	}

	static IdentifierEntity toParticipantEntity(final Identifier identifier) {
		return Optional.ofNullable(identifier)
			.map(p -> IdentifierEntity.create()
				.withType(p.getType())
				.withValue(p.getValue()))
			.orElse(null);
	}

	static Metadata toMetadata(final MetadataEntity entity) {
		return Metadata.create()
			.withKey(entity.getKey())
			.withValues(entity.getValues());
	}

	static List<Metadata> toMetadata(final List<MetadataEntity> entities) {
		return Optional.ofNullable(entities)
			.orElse(emptyList()).stream()
			.map(Mapper::toMetadata)
			.toList();
	}

	static MetadataEntity toMetadataEntity(final Metadata metadata) {
		return MetadataEntity.create()
			.withKey(metadata.getKey())
			.withValues(metadata.getValues());
	}

	static List<MetadataEntity> toMetadataEntities(final List<Metadata> metadata) {
		return Optional.ofNullable(metadata).orElse(emptyList()).stream()
			.map(Mapper::toMetadataEntity)
			.toList();
	}
}
