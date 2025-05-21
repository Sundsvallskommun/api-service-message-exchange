package se.sundsvall.chat.service;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;
import se.sundsvall.chat.api.model.Conversation;
import se.sundsvall.chat.api.model.ConversationRequest;
import se.sundsvall.chat.api.model.Message;
import se.sundsvall.chat.api.model.MessageRequest;
import se.sundsvall.chat.api.model.MetaData;
import se.sundsvall.chat.api.model.Participant;
import se.sundsvall.chat.integration.db.model.ConversationEntity;
import se.sundsvall.chat.integration.db.model.MessageEntity;
import se.sundsvall.chat.integration.db.model.MetaDataEntity;
import se.sundsvall.chat.integration.db.model.ParticipantEntity;

public final class Mapper {

	private Mapper() {
		// Prevent instantiation
	}

	static ConversationEntity toConversationEntity(final String municipalityId, final String namespace, final ConversationRequest conversation) {

		return ConversationEntity.create()
			.withParticipants(toParticipantEntities(conversation.getParticipants()))
			.withMunicipalityId(municipalityId)
			.withNamespace(namespace)
			.withChannelId(conversation.getChannelId())
			.withMetaData(toMetaDataEntities(conversation.getMetaData()))
			.withTopic(conversation.getTopic());
	}

	static Conversation toConversation(final ConversationEntity entity) {

		return Conversation.create()
			.withId(entity.getId())
			.withParticipants(toParticipants(entity.getParticipants()))
			.withMunicipalityId(entity.getMunicipalityId())
			.withNamespace(entity.getNamespace())
			.withChannelId(entity.getChannelId())
			.withMetaData(toMetaData(entity.getMetaData()))
			.withTopic(entity.getTopic());
	}

	static ConversationEntity updateConversationEntity(final ConversationEntity entity, final ConversationRequest conversation) {

		Optional.ofNullable(conversation.getChannelId()).ifPresent(entity::setChannelId);
		Optional.ofNullable(conversation.getTopic()).ifPresent(entity::setTopic);
		Optional.ofNullable(conversation.getParticipants()).ifPresent(participants -> entity.setParticipants(toParticipantEntities(participants)));
		Optional.ofNullable(conversation.getMetaData()).ifPresent(metaData -> entity.setMetaData(toMetaDataEntities(metaData)));
		return entity;
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
			.withAttachments(AttachmentMapper.toErrandAttachments(entity.getAttachments()));
	}

	static MessageEntity toMessageEntity(final ConversationEntity entity, final MessageRequest message) {

		return MessageEntity.create()
			.withSequenceNumber(message.getSequenceNumber())
			.withInReplyTo(message.getInReplyTo())
			.withCreatedBy(toParticipantEntity(message.getCreatedBy()))
			.withContent(message.getContent())
			.withConversation(entity)
			.withReadBy(toParticipantEntities(message.getReadBy()));
	}

	static List<Participant> toParticipants(final List<ParticipantEntity> entities) {
		return Optional.ofNullable(entities)
			.orElse(emptyList()).stream()
			.map(Mapper::toParticipant)
			.toList();
	}

	static Participant toParticipant(final ParticipantEntity entity) {
		return Optional.ofNullable(entity)
			.map(p -> Participant.create()
				.withType(entity.getType())
				.withValue(entity.getValue()))
			.orElse(null);
	}

	static List<ParticipantEntity> toParticipantEntities(final List<Participant> participants) {
		return Optional.ofNullable(participants)
			.orElse(emptyList()).stream()
			.map(Mapper::toParticipantEntity)
			.toList();
	}

	static ParticipantEntity toParticipantEntity(final Participant participant) {
		return Optional.ofNullable(participant)
			.map(p -> ParticipantEntity.create()
				.withType(p.getType())
				.withValue(p.getValue()))
			.orElse(null);
	}

	static MetaData toMetaData(final MetaDataEntity entity) {
		return MetaData.create()
			.withKey(entity.getKey())
			.withValues(entity.getValues());
	}

	static List<MetaData> toMetaData(final List<MetaDataEntity> entities) {
		return Optional.ofNullable(entities)
			.orElse(emptyList()).stream()
			.map(Mapper::toMetaData)
			.toList();
	}

	static MetaDataEntity toMetaDataEntity(final MetaData metaData) {
		return MetaDataEntity.create()
			.withKey(metaData.getKey())
			.withValues(metaData.getValues());
	}

	static List<MetaDataEntity> toMetaDataEntities(final List<MetaData> metaData) {
		return Optional.ofNullable(metaData).orElse(emptyList()).stream()
			.map(Mapper::toMetaDataEntity)
			.toList();
	}
}
