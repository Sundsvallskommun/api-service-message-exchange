package se.sundsvall.messageexchange.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.messageexchange.api.model.Conversation;
import se.sundsvall.messageexchange.integration.db.ConversationRepository;
import se.sundsvall.messageexchange.integration.db.MessageRepository;
import se.sundsvall.messageexchange.integration.db.ReadByCountProjection;
import se.sundsvall.messageexchange.integration.db.ReadByPartCountProjection;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageType;
import se.sundsvall.messageexchange.integration.db.model.SequenceEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

	@Mock
	private MessageRepository messageRepositoryMock;

	@Mock
	private ConversationRepository conversationRepositoryMock;

	@Captor
	private ArgumentCaptor<MessageEntity> messageEntityArgumentCaptor;

	@InjectMocks
	private ConversationService conversationService;

	@Test
	void readConversation() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var conversationId = "conversationId";
		final var entity = new ConversationEntity();

		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.of(entity));
		when(messageRepositoryMock.findTopByConversationIdOrderBySequenceNumberDesc(conversationId))
			.thenReturn(Optional.ofNullable(MessageEntity.create().withSequenceNumber(SequenceEntity.create().withId(123L))));

		// Act
		final var result = conversationService.readConversation(namespace, municipalityId, conversationId);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getLatestSequenceNumber()).isEqualTo(123L);
		verify(conversationRepositoryMock).findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId);
	}

	@Test
	void readConversationNotFound() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var conversationId = "conversationId";
		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> conversationService.readConversation(namespace, municipalityId, conversationId))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not Found: Conversation with id conversationId not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void readConversationWithNoSequenceNumber() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var conversationId = "conversationId";
		final var entity = new ConversationEntity();

		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.of(entity));

		when(messageRepositoryMock.findTopByConversationIdOrderBySequenceNumberDesc(conversationId))
			.thenReturn(Optional.of(MessageEntity.create()));
		// Act
		final var result = conversationService.readConversation(namespace, municipalityId, conversationId);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getLatestSequenceNumber()).isNull();
		verify(messageRepositoryMock).findTopByConversationIdOrderBySequenceNumberDesc(conversationId);
		verify(conversationRepositoryMock).findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId);
	}

	@Test
	void createConversation() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var request = new Conversation();
		final var entity = new ConversationEntity();
		entity.setId("newConversationId");
		when(conversationRepositoryMock.save(any(ConversationEntity.class))).thenReturn(entity);
		se.sundsvall.dept44.support.Identifier.set(Identifier.create().withType(Identifier.Type.AD_ACCOUNT).withValue("adUser"));

		// Act
		final var result = conversationService.createConversation(namespace, municipalityId, request);

		// Assert

		assertThat(result).isEqualTo("newConversationId");
		verify(conversationRepositoryMock).save(any(ConversationEntity.class));
		verify(messageRepositoryMock).save(messageEntityArgumentCaptor.capture());
		assertThat(messageEntityArgumentCaptor.getValue()).satisfies(message -> {
			assertThat(message.getType()).isEqualTo(MessageType.SYSTEM_CREATED);
			assertThat(message.getConversation()).isSameAs(entity);
			assertThat(message.getSequenceNumber()).isNotNull();
			assertThat(message.getContent()).isEqualTo("Konversation skapad");
			assertThat(message.getCreatedBy().getType()).isEqualTo("adAccount");
			assertThat(message.getCreatedBy().getValue()).isEqualTo("adUser");
		});
	}

	@Test
	void updateConversation() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var conversationId = "conversationId";
		final var request = new Conversation();
		final var entity = new ConversationEntity();
		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.of(entity));
		when(conversationRepositoryMock.save(entity)).thenReturn(entity);

		// Act
		final var result = conversationService.updateConversation(namespace, municipalityId, conversationId, request);

		// Assert
		assertThat(result).isNotNull();
		verify(conversationRepositoryMock).findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId);
		verify(conversationRepositoryMock).save(entity);
		verifyNoInteractions(messageRepositoryMock);
	}

	@Test
	void updateConversationWithDiff() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var conversationId = "conversationId";
		final var request = new Conversation().withTopic("x");
		final var entity = new ConversationEntity().withTopic("y");
		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.of(entity));
		when(conversationRepositoryMock.save(entity)).thenReturn(entity);
		se.sundsvall.dept44.support.Identifier.set(Identifier.create().withType(Identifier.Type.AD_ACCOUNT).withValue("adUser"));

		// Act
		final var result = conversationService.updateConversation(namespace, municipalityId, conversationId, request);

		// Assert
		assertThat(result).isNotNull();
		verify(conversationRepositoryMock).findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId);
		verify(conversationRepositoryMock).save(entity);
		verify(messageRepositoryMock).save(messageEntityArgumentCaptor.capture());
		assertThat(messageEntityArgumentCaptor.getValue()).satisfies(message -> {
			assertThat(message.getType()).isEqualTo(MessageType.SYSTEM_CREATED);
			assertThat(message.getConversation()).isSameAs(entity);
			assertThat(message.getSequenceNumber()).isNotNull();
			assertThat(message.getContent()).isEqualTo("Ämnesrad ändrad från 'y' till 'x'.");
			assertThat(message.getCreatedBy().getType()).isEqualTo("adAccount");
			assertThat(message.getCreatedBy().getValue()).isEqualTo("adUser");
		});
	}

	@Test
	void updateConversationNotFound() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var conversationId = "conversationId";
		final var request = new Conversation();
		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> conversationService.updateConversation(namespace, municipalityId, conversationId, request))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not Found: Conversation with id conversationId not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void deleteConversation() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var conversationId = "conversationId";
		final var entity = new ConversationEntity();
		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.of(entity));

		// Act
		conversationService.deleteConversation(namespace, municipalityId, conversationId);

		// Assert
		verify(conversationRepositoryMock).findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId);
		verify(conversationRepositoryMock).deleteById(conversationId);
	}

	@Test
	void deleteConversationNotFound() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var conversationId = "conversationId";
		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> conversationService.deleteConversation(namespace, municipalityId, conversationId))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not Found: Conversation with id conversationId not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void countReadBy() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var conversationId = "conversationId";
		final var entity = new ConversationEntity();

		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.of(entity));
		when(messageRepositoryMock.countMessages(conversationId, false)).thenReturn(13L);
		when(messageRepositoryMock.countReadByGroupedByIdentifier(conversationId, false))
			.thenReturn(List.of(readByCountProjection("adAccount", "joe01doe", 4L)));
		when(messageRepositoryMock.countReadByGroupedByPart(conversationId, false))
			.thenReturn(List.of(readByPartCountProjection("errand-123", 6L)));

		// Act
		final var result = conversationService.countReadBy(namespace, municipalityId, conversationId, false);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getMessageCount()).isEqualTo(13L);
		assertThat(result.getReadByCount()).hasSize(1).satisfies(counts -> {
			assertThat(counts.getFirst().getIdentifier().getType()).isEqualTo("adAccount");
			assertThat(counts.getFirst().getIdentifier().getValue()).isEqualTo("joe01doe");
			assertThat(counts.getFirst().getCount()).isEqualTo(4L);
		});
		assertThat(result.getReadByPartCount()).hasSize(1).satisfies(counts -> {
			assertThat(counts.getFirst().getPart()).isEqualTo("errand-123");
			assertThat(counts.getFirst().getCount()).isEqualTo(6L);
		});
		verify(conversationRepositoryMock).findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId);
		verify(messageRepositoryMock).countMessages(conversationId, false);
		verify(messageRepositoryMock).countReadByGroupedByIdentifier(conversationId, false);
		verify(messageRepositoryMock).countReadByGroupedByPart(conversationId, false);
	}

	@Test
	void countReadByIncludingSystemMessages() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var conversationId = "conversationId";
		final var entity = new ConversationEntity();

		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.of(entity));
		when(messageRepositoryMock.countMessages(conversationId, true)).thenReturn(14L);
		when(messageRepositoryMock.countReadByGroupedByIdentifier(conversationId, true)).thenReturn(List.of());
		when(messageRepositoryMock.countReadByGroupedByPart(conversationId, true)).thenReturn(List.of());

		// Act
		final var result = conversationService.countReadBy(namespace, municipalityId, conversationId, true);

		// Assert
		assertThat(result.getMessageCount()).isEqualTo(14L);
		assertThat(result.getReadByCount()).isEmpty();
		assertThat(result.getReadByPartCount()).isEmpty();
		verify(messageRepositoryMock).countMessages(conversationId, true);
		verify(messageRepositoryMock).countReadByGroupedByIdentifier(conversationId, true);
		verify(messageRepositoryMock).countReadByGroupedByPart(conversationId, true);
	}

	@Test
	void countReadByConversationNotFound() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var conversationId = "conversationId";
		when(conversationRepositoryMock.findByNamespaceAndMunicipalityIdAndId(namespace, municipalityId, conversationId))
			.thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> conversationService.countReadBy(namespace, municipalityId, conversationId, false))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not Found: Conversation with id conversationId not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	private static ReadByCountProjection readByCountProjection(final String type, final String value, final long count) {
		return new ReadByCountProjection() {
			@Override
			public String getType() {
				return type;
			}

			@Override
			public String getValue() {
				return value;
			}

			@Override
			public long getCount() {
				return count;
			}
		};
	}

	private static ReadByPartCountProjection readByPartCountProjection(final String part, final long count) {
		return new ReadByPartCountProjection() {
			@Override
			public String getPart() {
				return part;
			}

			@Override
			public long getCount() {
				return count;
			}
		};
	}
}
