package se.sundsvall.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import se.sundsvall.chat.api.model.ConversationRequest;
import se.sundsvall.chat.integration.db.ConversationRepository;
import se.sundsvall.chat.integration.db.model.ConversationEntity;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

	@Mock
	private ConversationRepository conversationRepositoryMock;

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

		// Act
		final var result = conversationService.readConversation(namespace, municipalityId, conversationId);

		// Assert
		assertThat(result).isNotNull();
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
	void createConversation() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var request = new ConversationRequest();
		final var entity = new ConversationEntity();
		entity.setId("newConversationId");
		when(conversationRepositoryMock.save(any(ConversationEntity.class))).thenReturn(entity);

		// Act
		final var result = conversationService.createConversation(namespace, municipalityId, request);

		// Assert
		assertThat(result).isEqualTo("newConversationId");
		verify(conversationRepositoryMock).save(any(ConversationEntity.class));
	}

	@Test
	void updateConversation() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var conversationId = "conversationId";
		final var request = new ConversationRequest();
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
	}

	@Test
	void updateConversationNotFound() {
		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "2281";
		final var conversationId = "conversationId";
		final var request = new ConversationRequest();
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
}
