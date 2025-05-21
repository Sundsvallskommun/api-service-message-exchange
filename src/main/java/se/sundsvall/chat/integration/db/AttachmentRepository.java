package se.sundsvall.chat.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.chat.integration.db.model.AttachmentEntity;

public interface AttachmentRepository extends JpaRepository<AttachmentEntity, String> {

	AttachmentEntity findByIdAndMessageEntityId(String id, String messageId);
}
