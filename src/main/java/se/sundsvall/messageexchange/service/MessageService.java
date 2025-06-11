package se.sundsvall.messageexchange.service;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messageexchange.service.mapper.Mapper.toIdentifierEntity;
import static se.sundsvall.messageexchange.service.mapper.Mapper.toMessageEntity;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Problem;
import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.messageexchange.api.model.Message;
import se.sundsvall.messageexchange.integration.db.AttachmentRepository;
import se.sundsvall.messageexchange.integration.db.ConversationRepository;
import se.sundsvall.messageexchange.integration.db.MessageRepository;
import se.sundsvall.messageexchange.integration.db.model.AttachmentEntity;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;
import se.sundsvall.messageexchange.integration.db.model.IdentifierEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;
import se.sundsvall.messageexchange.integration.db.model.ReadByEntity;
import se.sundsvall.messageexchange.integration.db.model.SequenceEntity;
import se.sundsvall.messageexchange.service.mapper.AttachmentMapper;
import se.sundsvall.messageexchange.service.mapper.Mapper;

@Service
public class MessageService {

	private final MessageRepository messageRepository;
	private final ConversationRepository conversationRepository;
	private final EntityManager entityManager;
	private final AttachmentRepository attachmentRepository;

	public MessageService(
		final MessageRepository messageRepository,
		final ConversationRepository conversationRepository,
		final EntityManager entityManager,
		final AttachmentRepository attachmentRepository) {

		this.messageRepository = messageRepository;
		this.conversationRepository = conversationRepository;
		this.entityManager = entityManager;
		this.attachmentRepository = attachmentRepository;
	}

	public String createMessage(final String municipalityId, final String namespace, final String conversationId, final Message message, final List<MultipartFile> attachments) {

		final var conversationEntity = findExistingConversation(municipalityId, namespace, conversationId);
		final var entity = toMessageEntity(conversationEntity, message)
			.withSequenceNumber(new SequenceEntity());
		entity.setAttachments(AttachmentMapper.toAttachmentEntities(attachments, entityManager, entity));

		if (conversationEntity.getParticipants() == null) {
			conversationEntity.setParticipants(new ArrayList<>());
		}

		if (Identifier.get() != null && identifierNotPresent(conversationEntity.getParticipants().stream())) {
			conversationEntity.getParticipants().add(toIdentifierEntity(Identifier.get()));
			conversationRepository.save(conversationEntity);
		}

		return messageRepository.saveAndFlush(entity).getId();
	}

	public Page<Message> getMessages(final String municipalityId, final String namespace, final String conversationId, final Pageable pageable) {
		final var conversationEntity = findExistingConversation(municipalityId, namespace, conversationId);
		final var matches = messageRepository.findByConversation(conversationEntity, pageable);

		final var messages = Mapper.toMessages(matches.getContent());
		updateReadBy(matches);
		messageRepository.saveAll(matches);
		return new PageImpl<>(messages, pageable, matches.getTotalElements());
	}

	public void deleteMessage(final String municipalityId, final String namespace, final String conversationId, final String messageId) {
		findExistingConversation(municipalityId, namespace, conversationId);
		messageRepository.deleteById(messageId);
	}

	public void readErrandAttachment(final String namespace, final String municipalityId, final String conversationId, final String messageId, final String attachmentId, final HttpServletResponse response) {
		findExistingConversation(municipalityId, namespace, conversationId);
		final var attachmentEntity = attachmentRepository.findByIdAndMessageEntityId(attachmentId, messageId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Attachment with ID:'%s' not found".formatted(attachmentId)));

		streamAttachmentData(attachmentEntity, response);
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

	void updateReadBy(final Page<MessageEntity> matches) {
		if (matches == null || matches.isEmpty()) {
			return;
		}

		final var readByEntity = Mapper.toReadByEntity(Identifier.get());

		if (readByEntity != null && readByEntity.getIdentifier() != null) {
			matches.stream().forEach(message -> message.setReadBy(updateReadBy(message, readByEntity)));
		}
	}

	private List<ReadByEntity> updateReadBy(final MessageEntity message, final ReadByEntity readByEntity) {

		final var list = ofNullable(message.getReadBy())
			.orElseGet(ArrayList::new);

		if (identifierNotPresent(list.stream().map(ReadByEntity::getIdentifier))) {
			list.add(readByEntity);
		}
		return list;
	}

	private boolean identifierNotPresent(final Stream<IdentifierEntity> identifierEntityStream) {
		return identifierEntityStream.noneMatch(identifier -> identifier.getType().equals(Identifier.get().getType().name()) &&
			identifier.getValue().equals(Identifier.get().getValue()));
	}
}
