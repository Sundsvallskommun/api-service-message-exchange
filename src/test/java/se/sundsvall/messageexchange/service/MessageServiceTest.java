package se.sundsvall.messageexchange.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.zalando.problem.Status.NOT_FOUND;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Problem;
import se.sundsvall.messageexchange.api.model.Message;
import se.sundsvall.messageexchange.integration.db.AttachmentRepository;
import se.sundsvall.messageexchange.integration.db.ConversationRepository;
import se.sundsvall.messageexchange.integration.db.MessageRepository;
import se.sundsvall.messageexchange.integration.db.model.AttachmentDataEntity;
import se.sundsvall.messageexchange.integration.db.model.AttachmentEntity;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

	@Mock
	private AttachmentEntity attachmentMock;

	@Mock
	private Blob blobMock;

	@Mock
	private AttachmentDataEntity attachmentDataEntityMock;

	@Mock
	private HttpServletResponse httpServletResponseMock;

	@Mock
	private ServletOutputStream servletOutputStreamMock;

	@Mock
	private MessageRepository messageRepositoryMock;

	@Mock
	private ConversationRepository conversationRepositoryMock;

	@Mock
	private AttachmentRepository attachmentRepositoryMock;

	@InjectMocks
	private MessageService messageService;

	@Test
	void createMessage() {
		// Arrange
		final var municipalityId = "2281";
		final var namespace = "namespace";
		final var conversationId = "conversationId";
		final var messageRequest = new Message();
		final var conversationEntity = new ConversationEntity();
		final var messageEntity = new MessageEntity();
		messageEntity.setId("newMessageId");
		final var attachments = List.<MultipartFile>of();

		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.of(conversationEntity));
		when(messageRepositoryMock.saveAndFlush(any(MessageEntity.class))).thenReturn(messageEntity);

		// Act
		final var result = messageService.createMessage(municipalityId, namespace, conversationId, messageRequest, attachments);

		// Assert
		assertThat(result).isEqualTo("newMessageId");
		verify(conversationRepositoryMock).findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId);
		verify(messageRepositoryMock).saveAndFlush(any(MessageEntity.class));
	}

	@Test
	void createMessageConversationNotFound() {
		// Arrange
		final var municipalityId = "2281";
		final var namespace = "namespace";
		final var conversationId = "conversationId";
		final var messageRequest = new Message();
		final var attachments = List.<MultipartFile>of();

		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> messageService.createMessage(municipalityId, namespace, conversationId, messageRequest, attachments))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Conversation with id conversationId not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void getMessages() {
		// Arrange
		final var municipalityId = "2281";
		final var namespace = "namespace";
		final var conversationId = "conversationId";
		final var pageable = PageRequest.of(0, 10);
		final var conversationEntity = new ConversationEntity();
		final var messageEntity = new MessageEntity();
		final var messagePage = new PageImpl<>(List.of(messageEntity), pageable, 1);

		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.of(conversationEntity));
		when(messageRepositoryMock.findByConversation(conversationEntity, pageable)).thenReturn(messagePage);

		// Act
		final var result = messageService.getMessages(municipalityId, namespace, conversationId, pageable);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		verify(conversationRepositoryMock).findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId);
		verify(messageRepositoryMock).findByConversation(conversationEntity, pageable);
	}

	@Test
	void getMessagesConversationNotFound() {
		// Arrange
		final var municipalityId = "2281";
		final var namespace = "namespace";
		final var conversationId = "conversationId";
		final var pageable = PageRequest.of(0, 10);

		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> messageService.getMessages(municipalityId, namespace, conversationId, pageable))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Conversation with id conversationId not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void deleteMessage() {
		// Arrange
		final var municipalityId = "2281";
		final var namespace = "namespace";
		final var conversationId = "conversationId";
		final var messageId = "messageId";
		final var conversationEntity = new ConversationEntity();

		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.of(conversationEntity));

		// Act
		messageService.deleteMessage(municipalityId, namespace, conversationId, messageId);

		// Assert
		verify(conversationRepositoryMock).findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId);
		verify(messageRepositoryMock).deleteById(messageId);
	}

	@Test
	void deleteMessageConversationNotFound() {
		// Arrange
		final var municipalityId = "2281";
		final var namespace = "namespace";
		final var conversationId = "conversationId";
		final var messageId = "messageId";

		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> messageService.deleteMessage(municipalityId, namespace, conversationId, messageId))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Conversation with id conversationId not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void readErrandAttachment() throws SQLException, IOException {
		// Arrange
		final var municipalityId = "2281";
		final var namespace = "namespace";
		final var conversationId = "conversationId";
		final var messageId = "messageId";
		final var attachmentId = "attachmentId";

		final var conversationEntity = new ConversationEntity();
		final var attachmentEntity = AttachmentEntity.create()
			.withId(attachmentId)
			.withFileSize(100)
			.withAttachmentData(AttachmentDataEntity.create()
				.withFile(blobMock));

		final byte[] fileContent = "file content".getBytes();
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);

		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.of(conversationEntity));
		when(httpServletResponseMock.getOutputStream()).thenReturn(servletOutputStreamMock);
		when(blobMock.getBinaryStream()).thenReturn(inputStream);
		when(attachmentRepositoryMock.findByIdAndMessageEntityId(attachmentId, messageId)).thenReturn(attachmentEntity);

		// Act
		messageService.readErrandAttachment(namespace, municipalityId, conversationId, messageId, attachmentId, httpServletResponseMock);

		// Assert
		verify(conversationRepositoryMock).findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId);
		verify(attachmentRepositoryMock).findByIdAndMessageEntityId(attachmentId, messageId);
	}

	@Test
	void streamAttachmentDataSuccess() throws IOException, SQLException {
		final byte[] fileContent = "file content".getBytes();
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);

		when(httpServletResponseMock.getOutputStream()).thenReturn(servletOutputStreamMock);
		when(attachmentMock.getAttachmentData()).thenReturn(attachmentDataEntityMock);
		when(attachmentDataEntityMock.getFile()).thenReturn(blobMock);
		when(blobMock.getBinaryStream()).thenReturn(inputStream);
		when(attachmentMock.getMimeType()).thenReturn("application/pdf");
		when(attachmentMock.getFileName()).thenReturn("test.pdf");
		when(attachmentMock.getFileSize()).thenReturn(fileContent.length);

		messageService.streamAttachmentData(attachmentMock, httpServletResponseMock);

		verify(httpServletResponseMock).addHeader(CONTENT_TYPE, "application/pdf");
		verify(httpServletResponseMock).addHeader(CONTENT_DISPOSITION, "attachment; filename=\"test.pdf\"");
		verify(httpServletResponseMock).setContentLength(fileContent.length);
		verify(servletOutputStreamMock).write(any(byte[].class), eq(0), eq(fileContent.length));
	}

	@Test
	void streamAttachmentDataThrowsSQLException() throws SQLException {
		final byte[] fileContent = "file content".getBytes();
		when(attachmentMock.getAttachmentData()).thenReturn(attachmentDataEntityMock);
		when(attachmentDataEntityMock.getFile()).thenReturn(blobMock);
		when(blobMock.getBinaryStream()).thenThrow(new SQLException("Test SQLException"));
		when(attachmentMock.getFileSize()).thenReturn(fileContent.length);

		assertThatThrownBy(() -> messageService.streamAttachmentData(attachmentMock, httpServletResponseMock))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("SQLException occurred when copying file with attachment id");

		verify(httpServletResponseMock, never()).addHeader(eq(CONTENT_TYPE), anyString());
	}

	@Test
	void streamAttachmentDataFileSizeZero() {
		when(attachmentMock.getFileSize()).thenReturn(0);

		assertThatThrownBy(() -> messageService.streamAttachmentData(attachmentMock, httpServletResponseMock))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Attachment with id '%s' has no data".formatted(attachmentMock.getId()));

		verify(httpServletResponseMock, never()).addHeader(eq(CONTENT_TYPE), anyString());
		verify(httpServletResponseMock, never()).addHeader(eq(CONTENT_DISPOSITION), anyString());
		verify(httpServletResponseMock, never()).setContentLength(anyInt());
	}

}
