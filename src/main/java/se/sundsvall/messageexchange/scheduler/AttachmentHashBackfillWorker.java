package se.sundsvall.messageexchange.scheduler;

import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.messageexchange.integration.db.AttachmentRepository;

@Component
public class AttachmentHashBackfillWorker {

	private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentHashBackfillWorker.class);

	private final AttachmentRepository attachmentRepository;

	public AttachmentHashBackfillWorker(final AttachmentRepository attachmentRepository) {
		this.attachmentRepository = attachmentRepository;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void computeAndSaveHash(final String attachmentId) {
		attachmentRepository.findById(attachmentId).ifPresentOrElse(attachment -> {
			try {
				final var digest = MessageDigest.getInstance("SHA-256");
				try (var stream = attachment.getAttachmentData().getFile().getBinaryStream();
					var dis = new DigestInputStream(stream, digest)) {
					dis.transferTo(OutputStream.nullOutputStream());
				}
				attachment.setHash(HexFormat.of().formatHex(digest.digest()));
				attachmentRepository.save(attachment);
			} catch (final Exception e) {
				LOGGER.warn("Failed to compute hash for attachment {}", attachmentId, e);
			}
		}, () -> LOGGER.warn("Attachment {} not found, skipping", attachmentId));
	}
}
