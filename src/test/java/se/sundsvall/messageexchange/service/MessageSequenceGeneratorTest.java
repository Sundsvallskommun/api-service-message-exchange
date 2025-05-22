package se.sundsvall.messageexchange.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.messageexchange.integration.db.MessageSequenceRepository;
import se.sundsvall.messageexchange.integration.db.model.MessageSequenceEntity;

@ExtendWith(MockitoExtension.class)
class MessageSequenceGeneratorTest {

	@Mock
	private MessageSequenceRepository repositoryMock;

	@InjectMocks
	private MessageSequenceGenerator messageSequenceGenerator;

	@Test
	void generateSequence() {

		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "municipalityId";
		final var lastSequenceNumber = 1L;
		final var expectedSequenceNumber = 2L;

		when(repositoryMock.findByNamespaceAndMunicipalityId(namespace, municipalityId))
			.thenReturn(Optional.of(MessageSequenceEntity.create()
				.withNamespace(namespace)
				.withMunicipalityId(municipalityId)
				.withLastSequenceNumber(lastSequenceNumber)));

		// Act
		final var result = messageSequenceGenerator.generateSequence(namespace, municipalityId);

		// Assert
		assertThat(result).isNotNull().isGreaterThan(lastSequenceNumber).isEqualTo(expectedSequenceNumber);

		verify(repositoryMock).findByNamespaceAndMunicipalityId(namespace, municipalityId);
		verify(repositoryMock).saveAndFlush(any());
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void generateSequenceWithNewEntity() {

		// Arrange
		final var namespace = "namespace";
		final var municipalityId = "municipalityId";
		final var expectedSequenceNumber = 1L;

		when(repositoryMock.findByNamespaceAndMunicipalityId(namespace, municipalityId))
			.thenReturn(Optional.empty());

		// Act
		final var result = messageSequenceGenerator.generateSequence(namespace, municipalityId);

		// Assert
		assertThat(result).isNotNull().isEqualTo(expectedSequenceNumber);

		// Verify
		verify(repositoryMock).findByNamespaceAndMunicipalityId(namespace, municipalityId);
		verify(repositoryMock).saveAndFlush(any());
		verifyNoMoreInteractions(repositoryMock);
	}
}
