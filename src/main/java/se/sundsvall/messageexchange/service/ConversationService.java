package se.sundsvall.messageexchange.service;

import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messageexchange.service.Mapper.toConversation;
import static se.sundsvall.messageexchange.service.Mapper.toConversationEntity;
import static se.sundsvall.messageexchange.service.Mapper.toConversations;
import static se.sundsvall.messageexchange.service.Mapper.updateConversationEntity;
import static se.sundsvall.messageexchange.util.SpecificationBuilder.withMunicipalityId;
import static se.sundsvall.messageexchange.util.SpecificationBuilder.withNamespace;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.messageexchange.api.model.Conversation;
import se.sundsvall.messageexchange.integration.db.ConversationRepository;
import se.sundsvall.messageexchange.integration.db.MessageRepository;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;

@Service
public class ConversationService {

	private final ConversationRepository conversationRepository;
	private final MessageRepository messageRepository;

	public ConversationService(final ConversationRepository conversationRepository, final MessageRepository messageRepository) {
		this.conversationRepository = conversationRepository;
		this.messageRepository = messageRepository;
	}

	public Page<Conversation> readConversations(final String namespace, final String municipalityId, final Specification<ConversationEntity> filter, final Pageable pageable) {

		final var fullFilter = withNamespace(namespace).and(withMunicipalityId(municipalityId)).and(filter);
		final var matches = conversationRepository.findAll(fullFilter, pageable);
		final var conversations = toConversations(matches.getContent());
		conversations.forEach(conversation -> messageRepository.findTopByConversationIdOrderBySequenceNumberDesc(conversation.getId())
			.ifPresent(message -> conversation.setLatestSequenceNumber(message.getSequenceNumber())));

		return new PageImpl<>(conversations, pageable, matches.getTotalElements());
	}

	public Conversation readConversation(final String namespace, final String municipalityId, final String conversationId) {

		final var entity = findExistingConversation(municipalityId, namespace, conversationId);
		final var latestSequence = messageRepository.findTopByConversationIdOrderBySequenceNumberDesc(conversationId);

		return toConversation(entity).withLatestSequenceNumber(latestSequence.map(MessageEntity::getSequenceNumber).orElse(null));
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
