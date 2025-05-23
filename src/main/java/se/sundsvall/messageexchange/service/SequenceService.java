package se.sundsvall.messageexchange.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class SequenceService {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public Long nextMessageSequence() {
		return ((Number) entityManager
			.createNativeQuery("SELECT NEXT VALUE FOR message_sequence_id_generator")
			.getSingleResult()).longValue();
	}
}
