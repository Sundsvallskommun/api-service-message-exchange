package se.sundsvall.messageexchange.integration.db;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.messageexchange.integration.db.model.MessageSequenceEntity;

public interface MessageSequenceRepository extends JpaRepository<MessageSequenceEntity, String> {

	Optional<MessageSequenceEntity> findByNamespaceAndMunicipalityId(String namespace, String municipalityId);

}
