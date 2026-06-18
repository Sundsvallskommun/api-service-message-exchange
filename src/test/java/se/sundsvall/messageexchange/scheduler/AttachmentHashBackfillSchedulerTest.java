package se.sundsvall.messageexchange.scheduler;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.config.Task;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import se.sundsvall.messageexchange.integration.db.AttachmentRepository;
import se.sundsvall.messageexchange.integration.db.model.AttachmentDataEntity;
import se.sundsvall.messageexchange.integration.db.model.AttachmentEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachmentHashBackfillSchedulerTest {

	@Mock
	private AttachmentRepository attachmentRepositoryMock;

	@Mock
	private ScheduledTaskHolder scheduledTaskHolderMock;

	@Mock
	private ScheduledTask scheduledTaskMock;

	@Mock
	private Task taskMock;

	@Mock
	private ScheduledMethodRunnable runnableMock;

	@Mock
	private Blob blobMock;

	@InjectMocks
	private AttachmentHashBackfillScheduler scheduler;

	@Captor
	private ArgumentCaptor<AttachmentEntity> attachmentCaptor;

	@Test
	void backfillCancelsSelfWhenNothingToDo() {
		when(attachmentRepositoryMock.findAllByHashIsNull()).thenReturn(List.of());
		setupTaskHolderMock();

		scheduler.backfillAttachmentHashes();

		verify(scheduledTaskMock).cancel();
		verify(attachmentRepositoryMock, never()).save(attachmentCaptor.capture());
	}

	@Test
	void backfillComputesAndSavesHash() throws Exception {
		final var content = "file content".getBytes();
		when(blobMock.getBinaryStream()).thenReturn(new ByteArrayInputStream(content));

		final var attachment = AttachmentEntity.create()
			.withId("att-1")
			.withAttachmentData(new AttachmentDataEntity().withFile(blobMock));

		when(attachmentRepositoryMock.findAllByHashIsNull())
			.thenReturn(List.of(attachment))
			.thenReturn(List.of());
		setupTaskHolderMock();

		scheduler.backfillAttachmentHashes();

		verify(attachmentRepositoryMock).save(attachmentCaptor.capture());
		assertThat(attachmentCaptor.getValue().getHash())
			.isNotNull()
			.hasSize(64)
			.matches("[0-9a-f]{64}");
		verify(scheduledTaskMock).cancel();
	}

	@Test
	void backfillContinuesOnErrorAndDoesNotCancelIfRemainingWork() throws Exception {
		when(blobMock.getBinaryStream()).thenThrow(new RuntimeException("DB error"));

		final var failing = AttachmentEntity.create()
			.withId("att-fail")
			.withAttachmentData(new AttachmentDataEntity().withFile(blobMock));

		when(attachmentRepositoryMock.findAllByHashIsNull())
			.thenReturn(List.of(failing))
			.thenReturn(List.of(failing));

		scheduler.backfillAttachmentHashes();

		verify(attachmentRepositoryMock, never()).save(attachmentCaptor.capture());
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
