package se.sundsvall.messageexchange.service;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messageexchange.service.Mapper.toMessageEntity;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Problem;
import se.sundsvall.messageexchange.api.model.Message;
import se.sundsvall.messageexchange.integration.db.AttachmentRepository;
import se.sundsvall.messageexchange.integration.db.ConversationRepository;
import se.sundsvall.messageexchange.integration.db.MessageRepository;
import se.sundsvall.messageexchange.integration.db.model.AttachmentEntity;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;

@Service
public class MessageService {

	private final MessageRepository messageRepository;
	private final ConversationRepository conversationRepository;
	private final EntityManager entityManager;
	private final AttachmentRepository attachmentRepository;

	public MessageService(final MessageRepository messageRepository, final ConversationRepository conversationRepository, final EntityManager entityManager, final AttachmentRepository attachmentRepository) {
		this.messageRepository = messageRepository;
		this.conversationRepository = conversationRepository;
		this.entityManager = entityManager;
		this.attachmentRepository = attachmentRepository;
	}

	public String createMessage(final String municipalityId, final String namespace, final String conversationId, final Message message, final List<MultipartFile> attachments) {

		final var conversationEntity = findExistingConversation(municipalityId, namespace, conversationId);

		final var entity = toMessageEntity(conversationEntity, message);
		entity.setAttachments(AttachmentMapper.toAttachmentEntities(attachments, entityManager, entity));

		return messageRepository.saveAndFlush(entity).getId();
	}

	public Page<Message> getMessages(final String municipalityId, final String namespace, final String conversationId, final Pageable pageable) {
		final var conversationEntity = findExistingConversation(municipalityId, namespace, conversationId);
		final var matches = messageRepository.findByConversation(conversationEntity, pageable);
		return new PageImpl<>(Mapper.toMessages(matches.getContent()), pageable, matches.getTotalElements());
	}

	public void deleteMessage(final String municipalityId, final String namespace, final String conversationId, final String messageId) {
		findExistingConversation(municipalityId, namespace, conversationId);
		messageRepository.deleteById(messageId);
	}

	public void readErrandAttachment(final String namespace, final String municipalityId, final String conversationId, final String messageId, final String attachmentId, final HttpServletResponse response) {
		findExistingConversation(municipalityId, namespace, conversationId);
		streamAttachmentData(attachmentRepository.findByIdAndMessageEntityId(attachmentId, messageId), response);
	}

	void streamAttachmentData(final AttachmentEntity attachment, final HttpServletResponse response) {
		final var fileSize = attachment.getFileSize();

		if (fileSize == 0) {
			throw Problem.valueOf(NOT_FOUND, "Attachment with id '%s' has no data".formatted(attachment.getId()));
		}

		try {
			response.addHeader(CONTENT_TYPE, attachment.getMimeType());
			response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"");
			response.setContentLength(fileSize);
			StreamUtils.copy(attachment.getAttachmentData().getFile().getBinaryStream(), response.getOutputStream());
		} catch (final IOException | SQLException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "%s occurred when copying file with attachment id '%s' to response: %s".formatted(e.getClass().getSimpleName(), attachment.getId(), e.getMessage()));
		}
	}

	private ConversationEntity findExistingConversation(final String municipalityId, final String namespace, final String conversationId) {
		return conversationRepository.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Conversation with id %s not found".formatted(conversationId)));

	}
}
