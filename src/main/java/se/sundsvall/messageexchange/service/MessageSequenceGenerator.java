package se.sundsvall.messageexchange.service;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.messageexchange.integration.db.MessageSequenceRepository;
import se.sundsvall.messageexchange.integration.db.model.MessageSequenceEntity;

@Component
public class MessageSequenceGenerator {

	private final MessageSequenceRepository repository;

	public MessageSequenceGenerator(final MessageSequenceRepository messageSequenceRepository) {
		this.repository = messageSequenceRepository;
	}

	@Transactional(isolation = Isolation.SERIALIZABLE, propagation = REQUIRES_NEW)
	public Long generateSequence(final String namespace, final String municipalityId) {

		var sequence = repository.findByNamespaceAndMunicipalityId(namespace, municipalityId).orElse(null);

		if (sequence == null) {
			sequence = new MessageSequenceEntity()
				.withNamespace(namespace)
				.withMunicipalityId(municipalityId)
				.withLastSequenceNumber(0L);
		}

		sequence.setLastSequenceNumber(sequence.getLastSequenceNumber() + 1);

		repository.saveAndFlush(sequence);

		return sequence.getLastSequenceNumber();
	}
}
