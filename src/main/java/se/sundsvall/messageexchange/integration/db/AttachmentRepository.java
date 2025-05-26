package se.sundsvall.messageexchange.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.messageexchange.integration.db.model.AttachmentEntity;

@CircuitBreaker(name = "attachmentRepository")
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, String> {

	AttachmentEntity findByIdAndMessageEntityId(String id, String messageId);
}
