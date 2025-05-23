package se.sundsvall.messageexchange.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;

@CircuitBreaker(name = "conversationRepository")
public interface ConversationRepository extends JpaRepository<ConversationEntity, String>, JpaSpecificationExecutor<ConversationEntity> {

	Optional<ConversationEntity> findByNamespaceAndMunicipalityIdAndId(String namespace, String municipalityId, String id);

	boolean existsByNamespaceAndMunicipalityIdAndId(String namespace, String municipalityId, String id);
}
