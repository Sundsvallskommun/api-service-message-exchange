package se.sundsvall.messageexchange.service.mapper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.messageexchange.api.model.Attachment;
import se.sundsvall.messageexchange.integration.db.model.AttachmentDataEntity;
import se.sundsvall.messageexchange.integration.db.model.AttachmentEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ObjectUtils.anyNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static se.sundsvall.dept44.util.HashUtils.sha256Hex;
import static se.sundsvall.messageexchange.util.ServiceUtil.detectMimeTypeFromStream;

public final class AttachmentMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentMapper.class);

	private AttachmentMapper() {}

	public static AttachmentEntity toAttachmentEntity(final MultipartFile attachment, final MessageEntity messageEntity) {
		if (anyNull(attachment)) {
			return null;
		}

		try {
			final var hash = sha256Hex(attachment.getInputStream());

			return AttachmentEntity.create()
				.withMessageEntity(messageEntity)
				.withFileSize(Math.toIntExact(attachment.getSize()))
				.withHash(hash)
				.withAttachmentData(new AttachmentDataEntity().withFile(Hibernate.getLobHelper().createBlob(attachment.getInputStream(), attachment.getSize())))
				.withFileName(attachment.getOriginalFilename())
				.withMimeType(detectMimeTypeFromStream(attachment.getOriginalFilename(), attachment.getInputStream()));
		} catch (final IOException e) {
			LOGGER.warn("Exception when reading file", e);
			throw Problem.valueOf(BAD_REQUEST, "Could not read input stream!");
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
				.withFileSize(e.getFileSize())
				.withCreated(e.getCreated())
				.withId(e.getId())
				.withMimeType(e.getMimeType())
				.withHash(e.getHash()))
			.orElse(null);
	}

	public static List<AttachmentEntity> toAttachmentEntities(final List<MultipartFile> attachments, final MessageEntity messageEntity) {
		return Optional.ofNullable(attachments)
			.orElse(emptyList()).stream()
			.map(attachment -> AttachmentMapper.toAttachmentEntity(attachment, messageEntity))
			.toList();
	}
}
