package se.sundsvall.messageexchange.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;
import se.sundsvall.messageexchange.integration.db.AttachmentRepository;

@Component
public class AttachmentHashBackfillScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentHashBackfillScheduler.class);

	private final AttachmentRepository attachmentRepository;
	private final AttachmentHashBackfillWorker worker;
	private final ScheduledTaskHolder scheduledTaskHolder;

	public AttachmentHashBackfillScheduler(
		final AttachmentRepository attachmentRepository,
		final AttachmentHashBackfillWorker worker,
		final ScheduledTaskHolder scheduledTaskHolder) {
		this.attachmentRepository = attachmentRepository;
		this.worker = worker;
		this.scheduledTaskHolder = scheduledTaskHolder;
	}

	@Dept44Scheduled(
		name = "attachment-hash-backfill",
		cron = "${scheduler.attachment-hash-backfill.cron}",
		lockAtMostFor = "${scheduler.attachment-hash-backfill.lock-at-most-for}")
	public void backfillAttachmentHashes() {
		final var ids = attachmentRepository.findAllByHashIsNull()
			.stream()
			.map(a -> a.getId())
			.toList();

		if (ids.isEmpty()) {
			cancelSelf();
			return;
		}

		LOGGER.info("Starting hash backfill for {} attachment(s)", ids.size());
		ids.forEach(worker::computeAndSaveHash);
		LOGGER.info("Hash backfill complete for {} attachment(s)", ids.size());

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
