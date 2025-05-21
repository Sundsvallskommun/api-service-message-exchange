package se.sundsvall.chat.service;

import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.chat.service.Mapper.toConversation;
import static se.sundsvall.chat.service.Mapper.toConversationEntity;
import static se.sundsvall.chat.service.Mapper.updateConversationEntity;

import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.chat.api.model.Conversation;
import se.sundsvall.chat.api.model.ConversationRequest;
import se.sundsvall.chat.integration.db.ConversationRepository;
import se.sundsvall.chat.integration.db.model.ConversationEntity;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@Service
public class ConversationService {

	private final ConversationRepository conversationRepository;

	public ConversationService(final ConversationRepository conversationRepository) {
		this.conversationRepository = conversationRepository;
	}

	public Conversation readConversation(final String namespace, final String municipalityId, final String conversationId) {

		final var entity = findExistingConversation(municipalityId, namespace, conversationId);

		return toConversation(entity);
	}

	public String createConversation(final String namespace, final String municipalityId, final ConversationRequest conversation) {

		final var entity = toConversationEntity(municipalityId, namespace, conversation);

		return conversationRepository.save(entity).getId();
	}

	public Conversation updateConversation(final String namespace, @ValidMunicipalityId final String municipalityId, @ValidUuid final String conversationId, final ConversationRequest conversation) {

		final var entity = findExistingConversation(municipalityId, namespace, conversationId);

		return toConversation(conversationRepository.save(updateConversationEntity(entity, conversation)));
	}

	public void deleteConversation(final String namespace, @ValidMunicipalityId final String municipalityId, @ValidUuid final String conversationId) {

		findExistingConversation(municipalityId, namespace, conversationId);

		conversationRepository.deleteById(conversationId);
	}

	private ConversationEntity findExistingConversation(final String municipalityId, final String namespace, final String conversationId) {
		return conversationRepository.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Conversation with id %s not found".formatted(conversationId)));
	}

}
