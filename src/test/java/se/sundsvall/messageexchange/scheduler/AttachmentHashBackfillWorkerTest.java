package se.sundsvall.messageexchange.scheduler;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.messageexchange.integration.db.AttachmentRepository;
import se.sundsvall.messageexchange.integration.db.model.AttachmentDataEntity;
import se.sundsvall.messageexchange.integration.db.model.AttachmentEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachmentHashBackfillWorkerTest {

	@Mock
	private AttachmentRepository attachmentRepositoryMock;

	@Mock
	private Blob blobMock;

	@InjectMocks
	private AttachmentHashBackfillWorker worker;

	@Captor
	private ArgumentCaptor<AttachmentEntity> attachmentCaptor;

	@Test
	void computeAndSaveHashSavesCorrectHash() throws Exception {
		final var content = "file content".getBytes();
		when(blobMock.getBinaryStream()).thenReturn(new ByteArrayInputStream(content));

		final var attachment = AttachmentEntity.create()
			.withId("att-1")
			.withAttachmentData(new AttachmentDataEntity().withFile(blobMock));

		when(attachmentRepositoryMock.findById("att-1")).thenReturn(Optional.of(attachment));

		worker.computeAndSaveHash("att-1");

		verify(attachmentRepositoryMock).save(attachmentCaptor.capture());
		assertThat(attachmentCaptor.getValue().getHash())
			.isNotNull()
			.hasSize(64)
			.matches("[0-9a-f]{64}");
	}

	@Test
	void computeAndSaveHashSkipsWhenNotFound() {
		when(attachmentRepositoryMock.findById("missing")).thenReturn(Optional.empty());

		worker.computeAndSaveHash("missing");

		verify(attachmentRepositoryMock, never()).save(attachmentCaptor.capture());
	}

	@Test
	void computeAndSaveHashContinuesOnBlobError() throws Exception {
		when(blobMock.getBinaryStream()).thenThrow(new RuntimeException("DB error"));

		final var attachment = AttachmentEntity.create()
			.withId("att-err")
			.withAttachmentData(new AttachmentDataEntity().withFile(blobMock));

		when(attachmentRepositoryMock.findById("att-err")).thenReturn(Optional.of(attachment));

		worker.computeAndSaveHash("att-err");

		verify(attachmentRepositoryMock, never()).save(attachmentCaptor.capture());
	}
}
