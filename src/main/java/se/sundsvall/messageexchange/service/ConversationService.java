package se.sundsvall.messageexchange.service;

import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messageexchange.service.Mapper.toConversation;
import static se.sundsvall.messageexchange.service.Mapper.toConversationEntity;
import static se.sundsvall.messageexchange.service.Mapper.updateConversationEntity;

import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.messageexchange.api.model.Conversation;
import se.sundsvall.messageexchange.integration.db.ConversationRepository;
import se.sundsvall.messageexchange.integration.db.MessageSequenceRepository;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageSequenceEntity;

@Service
public class ConversationService {

	private final ConversationRepository conversationRepository;
	private final MessageSequenceRepository messageSequenceRepository;

	public ConversationService(final ConversationRepository conversationRepository, final MessageSequenceRepository messageSequenceRepository) {
		this.conversationRepository = conversationRepository;
		this.messageSequenceRepository = messageSequenceRepository;
	}

	public Conversation readConversation(final String namespace, final String municipalityId, final String conversationId) {

		final var entity = findExistingConversation(municipalityId, namespace, conversationId);
		final var latestSequence = messageSequenceRepository.findByNamespaceAndMunicipalityId(namespace, municipalityId);

		return toConversation(entity).withLatestSequenceNumber(latestSequence.map(MessageSequenceEntity::getLastSequenceNumber).orElse(0L));
	}

	public String createConversation(final String namespace, final String municipalityId, final Conversation conversation) {

		final var entity = toConversationEntity(municipalityId, namespace, conversation);

		return conversationRepository.save(entity).getId();
	}

	public Conversation updateConversation(final String namespace, final String municipalityId, final String conversationId, final Conversation conversation) {

		final var entity = findExistingConversation(municipalityId, namespace, conversationId);

		return toConversation(conversationRepository.save(updateConversationEntity(entity, conversation)));
	}

	public void deleteConversation(final String namespace, final String municipalityId, final String conversationId) {

		findExistingConversation(municipalityId, namespace, conversationId);

		conversationRepository.deleteById(conversationId);
	}

	private ConversationEntity findExistingConversation(final String municipalityId, final String namespace, final String conversationId) {
		return conversationRepository.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Conversation with id %s not found".formatted(conversationId)));
	}

}
