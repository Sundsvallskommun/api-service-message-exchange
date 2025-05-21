package se.sundsvall.chat.service;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ObjectUtils.anyNull;
import static se.sundsvall.chat.util.ServiceUtil.detectMimeTypeFromStream;

import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import se.sundsvall.chat.api.model.Attachment;
import se.sundsvall.chat.integration.db.model.AttachmentDataEntity;
import se.sundsvall.chat.integration.db.model.AttachmentEntity;
import se.sundsvall.chat.integration.db.model.MessageEntity;

public final class AttachmentMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentMapper.class);

	private AttachmentMapper() {}

	public static AttachmentEntity toAttachmentEntity(final MultipartFile attachment, final EntityManager entityManager, final MessageEntity messageEntity) {
		if (anyNull(attachment)) {
			return null;
		}

		try {
			final Session session = entityManager.unwrap(Session.class);
			return AttachmentEntity.create()
				.withMessageEntity(messageEntity)
				.withFileSize(Math.toIntExact(attachment.getSize()))
				.withAttachmentData(new AttachmentDataEntity().withFile(session.getLobHelper().createBlob(attachment.getInputStream(), attachment.getSize())))
				.withFileName(attachment.getOriginalFilename())
				.withMimeType(detectMimeTypeFromStream(attachment.getOriginalFilename(), attachment.getInputStream()));
		} catch (final IOException e) {
			LOGGER.warn("Exception when reading file", e);
			throw Problem.valueOf(Status.BAD_REQUEST, "Could not read input stream!");
		}
	}

	public static List<Attachment> toAttachments(final List<AttachmentEntity> attachmentEntities) {
		return Optional.ofNullable(attachmentEntities).orElse(emptyList()).stream()
			.map(AttachmentMapper::toAttachment)
			.filter(Objects::nonNull)
			.toList();
	}

	public static Attachment toAttachment(final AttachmentEntity attachmentEntity) {
		return Optional.ofNullable(attachmentEntity)
			.map(e -> Attachment.create()
				.withFileName(e.getFileName())
				.withCreated(e.getCreated())
				.withId(e.getId())
				.withMimeType(e.getMimeType()))
			.orElse(null);
	}

	public static List<AttachmentEntity> toAttachmentEntities(final List<MultipartFile> attachments, final EntityManager entityManager, final MessageEntity messageEntity) {
		return Optional.ofNullable(attachments)
			.orElse(emptyList()).stream()
			.map(attachment -> AttachmentMapper.toAttachmentEntity(attachment, entityManager, messageEntity))
			.toList();
	}
}
