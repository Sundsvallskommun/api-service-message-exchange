package se.sundsvall.messageexchange.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.dept44.support.Identifier.Type.AD_ACCOUNT;
import static se.sundsvall.dept44.support.Identifier.Type.PARTY_ID;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Problem;
import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.messageexchange.api.model.Message;
import se.sundsvall.messageexchange.integration.db.AttachmentRepository;
import se.sundsvall.messageexchange.integration.db.ConversationRepository;
import se.sundsvall.messageexchange.integration.db.MessageRepository;
import se.sundsvall.messageexchange.integration.db.model.AttachmentDataEntity;
import se.sundsvall.messageexchange.integration.db.model.AttachmentEntity;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;
import se.sundsvall.messageexchange.integration.db.model.IdentifierEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;
import se.sundsvall.messageexchange.integration.db.model.ReadByEntity;
import se.sundsvall.messageexchange.util.MessageSpecificationBuilder;

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

	@Captor
	private ArgumentCaptor<Page<MessageEntity>> messageEntityCaptor;

	@Captor
	private ArgumentCaptor<ConversationEntity> conversationEntityCaptor;

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
		final var dept44Identifier = se.sundsvall.dept44.support.Identifier.create()
			.withType(PARTY_ID)
			.withValue("da012da");

		try (final var mockedStatic = mockStatic(se.sundsvall.dept44.support.Identifier.class)) {
			mockedStatic.when(se.sundsvall.dept44.support.Identifier::get).thenReturn(dept44Identifier);
			when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
				.thenReturn(Optional.of(conversationEntity));
			when(messageRepositoryMock.saveAndFlush(any(MessageEntity.class))).thenReturn(messageEntity);

			// Act
			final var result = messageService.createMessage(municipalityId, namespace, conversationId, messageRequest, attachments);

			// Assert
			assertThat(result).isEqualTo("newMessageId");
			verify(conversationRepositoryMock).findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId);
			verify(messageRepositoryMock).saveAndFlush(any(MessageEntity.class));
			verify(conversationRepositoryMock).save(conversationEntityCaptor.capture());
			assertThat(conversationEntityCaptor.getValue().getParticipants())
				.hasSize(1).extracting(IdentifierEntity::getType, IdentifierEntity::getValue)
				.containsExactly(Tuple.tuple(PARTY_ID.name(), "da012da"));
		}
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
		final var messageEntity = new MessageEntity().withReadBy(new ArrayList<>(List.of(ReadByEntity.create().withIdentifier(IdentifierEntity.create().withType(PARTY_ID.name()).withValue("ad012ad")))));
		final var messagePage = new PageImpl<>(List.of(messageEntity), pageable, 1);
		final var filter = MessageSpecificationBuilder.withConversation(conversationEntity);

		final var dept44Identifier = se.sundsvall.dept44.support.Identifier.create()
			.withType(PARTY_ID)
			.withValue("da012da");

		try (final var mockedStatic = mockStatic(se.sundsvall.dept44.support.Identifier.class)) {
			mockedStatic.when(se.sundsvall.dept44.support.Identifier::get).thenReturn(dept44Identifier);

			when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
				.thenReturn(Optional.of(conversationEntity));
			when(messageRepositoryMock.findAll(any(Specification.class), eq(pageable))).thenReturn(messagePage);

			// Act
			final var result = messageService.getMessages(municipalityId, namespace, conversationId, filter, pageable);

			// Assert
			assertThat(result).isNotNull();
			assertThat(result.getContent()).hasSize(1);
			assertThat(result.getContent().getFirst().getReadBy()).hasSize(1);
			verify(conversationRepositoryMock).findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId);
			verify(messageRepositoryMock).findAll(any(Specification.class), eq(pageable));
			verify(messageRepositoryMock).saveAll(messageEntityCaptor.capture());
			assertThat(messageEntityCaptor.getValue().getContent().getFirst().getReadBy()).hasSize(2);

		}
	}

	@Test
	void getMessagesAlreadyRead() {
		// Arrange
		final var municipalityId = "2281";
		final var namespace = "namespace";
		final var conversationId = "conversationId";
		final var pageable = PageRequest.of(0, 10);
		final var conversationEntity = new ConversationEntity();
		final var messageEntity = new MessageEntity().withReadBy(new ArrayList<>(List.of(ReadByEntity.create().withIdentifier(IdentifierEntity.create().withType(PARTY_ID.name()).withValue("value")))));
		final var messagePage = new PageImpl<>(List.of(messageEntity), pageable, 1);
		final var filter = MessageSpecificationBuilder.withConversation(conversationEntity);

		final var dept44Identifier = se.sundsvall.dept44.support.Identifier.create()
			.withType(PARTY_ID)
			.withValue("value");

		try (final var mockedStatic = mockStatic(se.sundsvall.dept44.support.Identifier.class)) {
			mockedStatic.when(se.sundsvall.dept44.support.Identifier::get).thenReturn(dept44Identifier);

			when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
				.thenReturn(Optional.of(conversationEntity));
			when(messageRepositoryMock.findAll(any(Specification.class), eq(pageable))).thenReturn(messagePage);

			// Act
			final var result = messageService.getMessages(municipalityId, namespace, conversationId, filter, pageable);

			// Assert
			assertThat(result).isNotNull();
			assertThat(result.getContent()).hasSize(1);
			assertThat(result.getContent().getFirst().getReadBy()).hasSize(1);
			verify(conversationRepositoryMock).findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId);
			verify(messageRepositoryMock).findAll(any(Specification.class), eq(pageable));
			verify(messageRepositoryMock).saveAll(messageEntityCaptor.capture());
			assertThat(messageEntityCaptor.getValue().getContent().getFirst().getReadBy()).hasSize(1);

		}
	}

	@Test
	void getMessagesConversationNotFound() {
		// Arrange
		final var municipalityId = "2281";
		final var namespace = "namespace";
		final var conversationId = "conversationId";
		final var pageable = PageRequest.of(0, 10);
		final var filter = MessageSpecificationBuilder.withConversation(new ConversationEntity());
		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> messageService.getMessages(municipalityId, namespace, conversationId, filter, pageable))
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
		when(attachmentRepositoryMock.findByIdAndMessageEntityId(attachmentId, messageId)).thenReturn(Optional.of(attachmentEntity));

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

	@Test
	void updateReadByAddsReadByEntity() {
		// Arrange
		final var identifier = Identifier.create().withValue("testIdentifier").withType(PARTY_ID);

		final var messageEntity = new MessageEntity().withReadBy(new ArrayList<>(List.of(ReadByEntity.create().withIdentifier(IdentifierEntity.create().withType(AD_ACCOUNT.name()).withValue("existingIdentifier")))));
		final var messagePage = new PageImpl<>(List.of(messageEntity));

		try (final var mockedStatic = mockStatic(Identifier.class)) {
			mockedStatic.when(Identifier::get).thenReturn(identifier);

			// Act
			messageService.updateReadBy(messagePage);

			// Assert
			assertThat(messageEntity.getReadBy()).isNotNull();
			assertThat(messageEntity.getReadBy()).hasSize(2);
			assertThat(messageEntity.getReadBy().getFirst().getIdentifier().getValue()).isEqualTo("existingIdentifier");
			assertThat(messageEntity.getReadBy().getFirst().getIdentifier().getType()).isEqualTo(AD_ACCOUNT.name());
			assertThat(messageEntity.getReadBy().getLast().getIdentifier().getValue()).isEqualTo("testIdentifier");
			assertThat(messageEntity.getReadBy().getLast().getIdentifier().getType()).isEqualTo(PARTY_ID.name());
		}
	}

	@Test
	void updateReadByDoesNothingForEmptyPage() {
		// Arrange
		final var emptyPage = new PageImpl<MessageEntity>(List.of());

		// Act
		messageService.updateReadBy(emptyPage);

		// Assert
		// No exception should be thrown, and no changes should occur
		assertThat(emptyPage.getContent()).isEmpty();
	}

}
