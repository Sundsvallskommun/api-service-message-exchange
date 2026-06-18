package se.sundsvall.messageexchange.scheduler;

import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.messageexchange.integration.db.AttachmentRepository;

@Component
public class AttachmentHashBackfillScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentHashBackfillScheduler.class);

	private final AttachmentRepository attachmentRepository;
	private final ScheduledTaskHolder scheduledTaskHolder;

	public AttachmentHashBackfillScheduler(final AttachmentRepository attachmentRepository, final ScheduledTaskHolder scheduledTaskHolder) {
		this.attachmentRepository = attachmentRepository;
		this.scheduledTaskHolder = scheduledTaskHolder;
	}

	@Scheduled(cron = "${scheduler.attachment-hash-backfill.cron}")
	@SchedulerLock(name = "attachment_hash_backfill", lockAtMostFor = "${scheduler.attachment-hash-backfill.lock-at-most-for}")
	@Transactional
	public void backfillAttachmentHashes() {
		final var attachments = attachmentRepository.findAllByHashIsNull();

		if (attachments.isEmpty()) {
			cancelSelf();
			return;
		}

		LOGGER.info("Starting hash backfill for {} attachment(s)", attachments.size());
		var processed = 0;

		for (final var attachment : attachments) {
			try {
				final var digest = MessageDigest.getInstance("SHA-256");
				try (var stream = attachment.getAttachmentData().getFile().getBinaryStream();
					var dis = new DigestInputStream(stream, digest)) {
					dis.transferTo(OutputStream.nullOutputStream());
				}
				attachment.setHash(HexFormat.of().formatHex(digest.digest()));
				attachmentRepository.save(attachment);
				processed++;
			} catch (final Exception e) {
				LOGGER.warn("Failed to compute hash for attachment {}", attachment.getId(), e);
			}
		}

		LOGGER.info("Hash backfill complete, processed {}/{} attachment(s)", processed, attachments.size());

		if (attachmentRepository.findAllByHashIsNull().isEmpty()) {
			cancelSelf();
		}
	}

	private void cancelSelf() {
		scheduledTaskHolder.getScheduledTasks()
			.stream()
			.filter(t -> t.getTask().getRunnable() instanceof ScheduledMethodRunnable smr
				&& smr.getMethod().getDeclaringClass().equals(getClass()))
			.findFirst()
			.ifPresent(task -> {
				task.cancel();
				LOGGER.info("All attachments have hashes, backfill scheduler cancelled");
			});
	}
}
