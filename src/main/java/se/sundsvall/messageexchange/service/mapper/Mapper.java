package se.sundsvall.messageexchange.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import se.sundsvall.messageexchange.api.model.Conversation;
import se.sundsvall.messageexchange.api.model.Identifier;
import se.sundsvall.messageexchange.api.model.KeyValues;
import se.sundsvall.messageexchange.api.model.Message;
import se.sundsvall.messageexchange.api.model.MessageType;
import se.sundsvall.messageexchange.api.model.ReadBy;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;
import se.sundsvall.messageexchange.integration.db.model.ExternalReferencesEntity;
import se.sundsvall.messageexchange.integration.db.model.IdentifierEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;
import se.sundsvall.messageexchange.integration.db.model.MetadataEntity;
import se.sundsvall.messageexchange.integration.db.model.ReadByEntity;
import se.sundsvall.messageexchange.integration.db.model.SequenceEntity;

public final class Mapper {

	private static final String TOPIC_CHANGED_MSG = "Ämnesrad ändrad från '%s' till '%s'";
	private static final String PARTICIPANT_ADDED_MSG = "%s deltagare tillagd";
	private static final String PARTICIPANT_REMOVED_MSG = "%s deltagare borttagen";
	private static final String EXTERNAL_REFERENCE_ADDED_MSG = "Referens tillagd i konversation";
	private static final String EXTERNAL_REFERENCE_REMOVED_MSG = "Referens borttagen i konversation";

	private Mapper() {
		// Prevent instantiation
	}

	public static ConversationEntity toConversationEntity(final String municipalityId, final String namespace, final Conversation conversation) {
		return ConversationEntity.create()
			.withParticipants(toIdentifierEntities(conversation.getParticipants()))
			.withMunicipalityId(municipalityId)
			.withNamespace(namespace)
			.withMetadata(toMetadataEntities(conversation.getMetadata()))
			.withExternalReferences(toExternalReferencesEntities(conversation.getExternalReferences()))
			.withTopic(conversation.getTopic());
	}

	public static List<Conversation> toConversations(final List<ConversationEntity> entities) {
		return Optional.ofNullable(entities)
			.orElse(emptyList())
			.stream()
			.map(Mapper::toConversation)
			.toList();
	}

	public static Conversation toConversation(final ConversationEntity entity) {
		return Conversation.create()
			.withId(entity.getId())
			.withParticipants(toIdentifiers(entity.getParticipants()))
			.withMunicipalityId(entity.getMunicipalityId())
			.withNamespace(entity.getNamespace())
			.withMetadata(toMetadata(entity.getMetadata()))
			.withExternalReferences(toExternalReferences(entity.getExternalReferences()))
			.withTopic(entity.getTopic());
	}

	public static ConversationEntity updateConversationEntity(final ConversationEntity entity, final Conversation conversation) {

		Optional.ofNullable(conversation.getTopic()).ifPresent(entity::setTopic);
		Optional.ofNullable(conversation.getParticipants()).ifPresent(participants -> updateParticipants(entity, participants));
		Optional.ofNullable(conversation.getMetadata()).ifPresent(metaData -> updateMetadata(entity, metaData));
		Optional.ofNullable(conversation.getExternalReferences()).ifPresent(externalReferences -> updateExternalReferences(entity, externalReferences));

		return entity;
	}

	public static Optional<String> conversationDiffMessage(final ConversationEntity entity, final Conversation conversation) {
		final var changes = new ArrayList<String>();

		checkTopicDiff(entity, conversation, changes);
		checkParticipantsDiff(entity, conversation, changes);
		checkExternalReferenceDiff(entity, conversation, changes);

		return changes.isEmpty() ? Optional.empty() : Optional.of(String.join(". ", changes).concat("."));
	}

	private static void checkExternalReferenceDiff(ConversationEntity entity, Conversation conversation, ArrayList<String> changes) {
		Optional.ofNullable(conversation.getExternalReferences()).ifPresent(externalReferences -> {
			var added = false;
			var removed = false;

			for (var key : externalReferences.stream().map(KeyValues::getKey).toList()) {
				var addedValuesForKey = addedObjects(getExternalReferenceValuesByKey(entity, key), getExternalReferenceValuesByKey(conversation, key), s -> s);
				if (!addedValuesForKey.isEmpty()) {
					added = true;
					break;
				}
			}

			for (var key : ofNullable(entity.getExternalReferences()).orElse(new ArrayList<>()).stream().map(ExternalReferencesEntity::getKey).toList()) {
				var removedValuesForKey = removedObjects(getExternalReferenceValuesByKey(entity, key), getExternalReferenceValuesByKey(conversation, key), s -> s);
				if (!removedValuesForKey.isEmpty()) {
					removed = true;
					break;
				}
			}

			if (added) {
				changes.add(EXTERNAL_REFERENCE_ADDED_MSG);
			}
			if (removed) {
				changes.add(EXTERNAL_REFERENCE_REMOVED_MSG);
			}
		});
	}

	private static void checkParticipantsDiff(ConversationEntity entity, Conversation conversation, ArrayList<String> changes) {
		Optional.ofNullable(conversation.getParticipants()).ifPresent(participants -> {
			var added = addedObjects(entity.getParticipants(), conversation.getParticipants(), Mapper::toIdentifier);
			if (!added.isEmpty()) {
				changes.add(PARTICIPANT_ADDED_MSG.formatted(added.size()));
			}
			var removed = removedObjects(entity.getParticipants(), conversation.getParticipants(), Mapper::toIdentifierEntity);
			if (!removed.isEmpty()) {
				changes.add(PARTICIPANT_REMOVED_MSG.formatted(removed.size()));
			}
		});
	}

	private static void checkTopicDiff(ConversationEntity entity, Conversation conversation, ArrayList<String> changes) {
		Optional.ofNullable(conversation.getTopic()).ifPresent(topic -> {
			if (!topic.equals(entity.getTopic())) {
				changes.add(TOPIC_CHANGED_MSG.formatted(entity.getTopic(), topic));
			}
		});
	}

	private static List<String> getExternalReferenceValuesByKey(ConversationEntity entity, String key) {
		return ofNullable(entity.getExternalReferences()).orElse(new ArrayList<>()).stream()
			.filter(item -> item.getKey().equals(key))
			.findFirst()
			.map(ExternalReferencesEntity::getValues)
			.orElse(new ArrayList<>());
	}

	private static List<String> getExternalReferenceValuesByKey(Conversation conversation, String key) {
		return ofNullable(conversation.getExternalReferences()).orElse(new ArrayList<>()).stream()
			.filter(item -> item.getKey().equals(key))
			.findFirst()
			.map(KeyValues::getValues)
			.orElse(new ArrayList<>());
	}

	private static <T, R> Set<R> addedObjects(List<T> before, List<R> after, Function<T, R> converter) {
		var added = new HashSet<>(after);
		added.removeAll(ofNullable(before).orElse(new ArrayList<>()).stream().map(converter).collect(Collectors.toSet()));
		return added;
	}

	private static <T, R> Set<R> removedObjects(List<R> before, List<T> after, Function<T, R> converter) {
		var removed = new HashSet<>(before);
		removed.removeAll(ofNullable(after).orElse(new ArrayList<>()).stream().map(converter).collect(Collectors.toSet()));
		return removed;
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

	public static List<Message> toMessages(final List<MessageEntity> entities) {
		return entities.stream()
			.map(Mapper::toMessage)
			.toList();
	}

	public static Message toMessage(final MessageEntity entity) {

		return Message.create()
			.withId(entity.getId())
			.withSequenceNumber(Optional.ofNullable(entity.getSequenceNumber()).map(SequenceEntity::getId).orElse(null))
			.withInReplyToMessageId(entity.getInReplyToMessageId())
			.withCreated(entity.getCreated())
			.withCreatedBy(toIdentifier(entity.getCreatedBy()))
			.withContent(entity.getContent())
			.withReadBy(toReadByList(entity.getReadBy()))
			.withAttachments(AttachmentMapper.toAttachments(entity.getAttachments()))
			.withType(MessageType.valueOf(entity.getType().toString()));

	}

	public static MessageEntity toMessageEntity(final ConversationEntity entity, final Message message) {

		final var identifier = se.sundsvall.dept44.support.Identifier.get();

		return MessageEntity.create()
			.withInReplyToMessageId(message.getInReplyToMessageId())
			.withCreatedBy(toIdentifierEntity(identifier))
			.withReadBy(toReadByEntities(identifier))
			.withContent(message.getContent())
			.withConversation(entity);
	}

	public static List<Identifier> toIdentifiers(final List<IdentifierEntity> entities) {
		return Optional.ofNullable(entities)
			.orElse(emptyList()).stream()
			.map(Mapper::toIdentifier)
			.toList();
	}

	public static Identifier toIdentifier(final IdentifierEntity entity) {
		return Optional.ofNullable(entity)
			.map(p -> Identifier.create()
				.withType(p.getType())
				.withValue(p.getValue()))
			.orElse(null);
	}

	public static List<IdentifierEntity> toIdentifierEntities(final List<Identifier> identifiers) {
		return Optional.ofNullable(identifiers)
			.orElse(emptyList()).stream()
			.map(Mapper::toIdentifierEntity)
			.toList();
	}

	public static IdentifierEntity toIdentifierEntity(final Identifier identifier) {
		return Optional.ofNullable(identifier)
			.map(p -> IdentifierEntity.create()
				.withType(p.getType())
				.withValue(p.getValue()))
			.orElse(null);
	}

	public static KeyValues toMetadata(final MetadataEntity entity) {
		return KeyValues.create()
			.withKey(entity.getKey())
			.withValues(entity.getValues());
	}

	public static List<KeyValues> toMetadata(final List<MetadataEntity> entities) {
		return Optional.ofNullable(entities)
			.orElse(emptyList()).stream()
			.map(Mapper::toMetadata)
			.toList();
	}

	public static MetadataEntity toMetadataEntity(final KeyValues keyValues) {
		return MetadataEntity.create()
			.withKey(keyValues.getKey())
			.withValues(keyValues.getValues());
	}

	public static List<MetadataEntity> toMetadataEntities(final List<KeyValues> metadata) {
		return Optional.ofNullable(metadata).orElse(emptyList()).stream()
			.map(Mapper::toMetadataEntity)
			.toList();
	}

	public static List<ReadByEntity> toReadByEntities(final se.sundsvall.dept44.support.Identifier identifier) {
		return Optional.ofNullable(identifier)
			.map(entity -> List.of(toReadByEntity(entity)))
			.orElse(null);
	}

	public static ReadByEntity toReadByEntity(final se.sundsvall.dept44.support.Identifier identifier) {
		return Optional.ofNullable(identifier)
			.map(p -> ReadByEntity.create()
				.withIdentifier(toIdentifierEntity(identifier))
				.withReadAt(OffsetDateTime.now()))
			.orElse(null);
	}

	public static IdentifierEntity toIdentifierEntity(final se.sundsvall.dept44.support.Identifier identifier) {

		return Optional.ofNullable(identifier)
			.map(p -> IdentifierEntity.create()
				.withType(p.getType().name())
				.withValue(p.getValue()))
			.orElse(null);
	}

	public static List<ReadByEntity> toReadByEntities(final List<ReadBy> readBy) {

		return Optional.ofNullable(readBy)
			.orElse(emptyList()).stream()
			.map(Mapper::toReadByEntity)
			.toList();
	}

	public static ReadByEntity toReadByEntity(final ReadBy readBy) {

		return Optional.ofNullable(readBy)
			.map(r -> ReadByEntity.create()
				.withIdentifier(toIdentifierEntity(r.getIdentifier()))
				.withReadAt(r.getReadAt()))
			.orElse(null);
	}

	public static List<ReadBy> toReadByList(final List<ReadByEntity> readBy) {
		return Optional.ofNullable(readBy)
			.orElse(emptyList()).stream()
			.map(Mapper::toReadBy)
			.toList();
	}

	public static ReadBy toReadBy(final ReadByEntity entity) {

		return Optional.ofNullable(entity)
			.map(e -> ReadBy.create()
				.withIdentifier(toIdentifier(e.getIdentifier()))
				.withReadAt(entity.getReadAt()))
			.orElse(null);
	}

	public static List<ExternalReferencesEntity> toExternalReferencesEntities(final List<KeyValues> externalReferences) {
		return Optional.ofNullable(externalReferences)
			.orElse(emptyList()).stream()
			.map(Mapper::toExternalReferencesEntity)
			.toList();
	}

	public static ExternalReferencesEntity toExternalReferencesEntity(final KeyValues keyValues) {
		return Optional.ofNullable(keyValues)
			.map(p -> ExternalReferencesEntity.create()
				.withKey(keyValues.getKey())
				.withValues(keyValues.getValues()))
			.orElse(null);
	}

	public static List<KeyValues> toExternalReferences(final List<ExternalReferencesEntity> externalReferences) {
		return Optional.ofNullable(externalReferences)
			.orElse(emptyList()).stream()
			.map(Mapper::toExternalReference)
			.toList();
	}

	public static KeyValues toExternalReference(final ExternalReferencesEntity externalReferencesEntity) {
		return Optional.ofNullable(externalReferencesEntity)
			.map(e -> KeyValues.create()
				.withKey(e.getKey())
				.withValues(e.getValues()))
			.orElse(null);
	}
}
