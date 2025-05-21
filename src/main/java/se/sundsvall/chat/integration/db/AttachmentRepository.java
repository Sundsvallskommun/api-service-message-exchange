package se.sundsvall.chat.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.chat.integration.db.model.AttachmentEntity;

@CircuitBreaker(name = "attachmentRepository")
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, String> {

	AttachmentEntity findByIdAndMessageEntityId(String id, String messageId);
}
