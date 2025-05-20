package se.sundsvall.chat.service;

import org.springframework.stereotype.Service;
import se.sundsvall.chat.api.model.Conversation;
import se.sundsvall.chat.api.model.ConversationRequest;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.dept44.util.jacoco.ExcludeFromJacocoGeneratedCoverageReport;

@Service
@ExcludeFromJacocoGeneratedCoverageReport // TODO: Remove when implemented
public class ConversationService {

	public Conversation readConversation(final String namespace, final String municipalityId, final String id) {
		return null;
	}

	public String createConversation(final String namespace, final String municipalityId, final ConversationRequest conversation) {
		return null;
	}

	public Conversation updateConversation(final String namespace, @ValidMunicipalityId final String municipalityId, @ValidUuid final String id, final ConversationRequest conversation) {
		return null;
	}

	public void deleteConversation(final String namespace, @ValidMunicipalityId final String municipalityId, @ValidUuid final String id) {
		// Implementation here
	}
}
