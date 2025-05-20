package se.sundsvall.chat.service;

import static java.util.Collections.emptyList;

import java.util.List;
import org.springframework.stereotype.Service;
import se.sundsvall.chat.api.model.Message;
import se.sundsvall.chat.api.model.MessageRequest;
import se.sundsvall.dept44.util.jacoco.ExcludeFromJacocoGeneratedCoverageReport;

@Service
@ExcludeFromJacocoGeneratedCoverageReport // TODO: Remove when implemented
public class MessageService {

	public String createMessage(final String municipalityId, final String namespace, final String id, final MessageRequest message) {
		return null;
	}

	public List<Message> getMessages(final String municipalityId, final String namespace, final String id, final Integer page, final Integer size, final String sortBy) {
		return emptyList();
	}

	public void deleteMessage(final String municipalityId, final String namespace, final String id, final String messageId) {
		// Implementation here
	}
}
