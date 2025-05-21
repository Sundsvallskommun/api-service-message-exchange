package se.sundsvall.chat.service;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.time.OffsetDateTime;
import java.util.List;
import org.hibernate.LobHelper;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Problem;
import se.sundsvall.chat.integration.db.model.AttachmentEntity;
import se.sundsvall.chat.integration.db.model.MessageEntity;

@ExtendWith(MockitoExtension.class)
class AttachmentMapperTest {

	private static final String ATTACHMENT_ID = "attachmentId";

	private static final String FILE_NAME = "fileName";

	private static final String MIME_TYPE = "mimeType";

	private static final OffsetDateTime CREATED = now().minusWeeks(1);

	@Mock
	private EntityManager entityManagerMock;

	@Mock
	private MultipartFile multipartFileMock;

	@Mock
	private Session sessionMock;

	@Mock
	private LobHelper lobHelperMock;

	@Mock
	private Blob blobMock;

	@Test
	void toAttachmentEntity() throws IOException {

		final var entity = MessageEntity.create();

		when(entityManagerMock.unwrap(Session.class)).thenReturn(sessionMock);
		when(sessionMock.getLobHelper()).thenReturn(lobHelperMock);
		when(lobHelperMock.createBlob(any(), anyLong())).thenReturn(blobMock);
		when(multipartFileMock.getOriginalFilename()).thenReturn(FILE_NAME);
		when(multipartFileMock.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));

		final var result = AttachmentMapper.toAttachmentEntity(multipartFileMock, entityManagerMock, entity);

		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id", "created");
		assertThat(result.getFileName()).isEqualTo(FILE_NAME);
		assertThat(result.getAttachmentData().getFile()).isSameAs(blobMock);
		assertThat(result.getMimeType()).isEqualTo("text/plain");
		assertThat(result.getMessageEntity()).isSameAs(entity);
	}

	@Test
	void toAttachmentEntityThrowsIOException() throws IOException {

		when(entityManagerMock.unwrap(Session.class)).thenReturn(sessionMock);
		when(sessionMock.getLobHelper()).thenReturn(lobHelperMock);
		when(multipartFileMock.getInputStream()).thenThrow(new IOException("test exception"));

		assertThatThrownBy(() -> AttachmentMapper.toAttachmentEntity(multipartFileMock, entityManagerMock, MessageEntity.create()))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Could not read input stream!");
	}

	@Test
	void toAttachmentEntityAllNulls() {

		assertThat(AttachmentMapper.toAttachmentEntity(null, null, null)).isNull();
	}

	@Test
	void toErrandAttachments() {

		final var result = AttachmentMapper.toErrandAttachments(List.of(AttachmentEntity.create()
			.withId(ATTACHMENT_ID)
			.withFileName(FILE_NAME)
			.withAttachmentData(null)
			.withCreated(CREATED)
			.withMimeType(MIME_TYPE)));

		assertThat(result).isNotNull();
		assertThat(result.getFirst().getId()).isEqualTo(ATTACHMENT_ID);
		assertThat(result.getFirst().getFileName()).isEqualTo(FILE_NAME);
		assertThat(result.getFirst().getMimeType()).isEqualTo(MIME_TYPE);
		assertThat(result.getFirst().getCreated()).isCloseTo(CREATED, within(5, SECONDS));
	}

	@Test
	void toErrandAttachmentsFromNullInput() {

		assertThat(AttachmentMapper.toErrandAttachments(null))
			.isNotNull().isEmpty();
	}
}
