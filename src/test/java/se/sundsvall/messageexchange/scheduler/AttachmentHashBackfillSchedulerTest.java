package se.sundsvall.messageexchange.scheduler;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.config.Task;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import se.sundsvall.messageexchange.integration.db.AttachmentRepository;
import se.sundsvall.messageexchange.integration.db.model.AttachmentEntity;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachmentHashBackfillSchedulerTest {

	@Mock
	private AttachmentRepository attachmentRepositoryMock;

	@Mock
	private AttachmentHashBackfillWorker workerMock;

	@Mock
	private ScheduledTaskHolder scheduledTaskHolderMock;

	@Mock
	private ScheduledTask scheduledTaskMock;

	@Mock
	private Task taskMock;

	@Mock
	private ScheduledMethodRunnable runnableMock;

	@InjectMocks
	private AttachmentHashBackfillScheduler scheduler;

	@Test
	void backfillCancelsSelfWhenNothingToDo() {
		when(attachmentRepositoryMock.findAllByHashIsNull()).thenReturn(List.of());
		setupTaskHolderMock();

		scheduler.backfillAttachmentHashes();

		verify(workerMock, never()).computeAndSaveHash(org.mockito.ArgumentMatchers.any());
		verify(scheduledTaskMock).cancel();
	}

	@Test
	void backfillDelegatesEachAttachmentToWorker() {
		final var att1 = AttachmentEntity.create().withId("att-1");
		final var att2 = AttachmentEntity.create().withId("att-2");

		when(attachmentRepositoryMock.findAllByHashIsNull())
			.thenReturn(List.of(att1, att2))
			.thenReturn(List.of());
		setupTaskHolderMock();

		scheduler.backfillAttachmentHashes();

		verify(workerMock).computeAndSaveHash("att-1");
		verify(workerMock).computeAndSaveHash("att-2");
		verify(scheduledTaskMock).cancel();
	}

	@Test
	void backfillDoesNotCancelIfRemainingWork() {
		final var att = AttachmentEntity.create().withId("att-fail");

		when(attachmentRepositoryMock.findAllByHashIsNull())
			.thenReturn(List.of(att))
			.thenReturn(List.of(att));

		scheduler.backfillAttachmentHashes();

		verify(scheduledTaskMock, never()).cancel();
	}

	private void setupTaskHolderMock() {
		try {
			final var method = AttachmentHashBackfillScheduler.class.getMethod("backfillAttachmentHashes");
			when(runnableMock.getMethod()).thenReturn(method);
			when(taskMock.getRunnable()).thenReturn(runnableMock);
			when(scheduledTaskMock.getTask()).thenReturn(taskMock);
			when(scheduledTaskHolderMock.getScheduledTasks()).thenReturn(Set.of(scheduledTaskMock));
		} catch (final NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}
