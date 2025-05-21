package se.sundsvall.chat.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.chat.integration.db.model.ConversationEntity;
import se.sundsvall.chat.integration.db.model.MessageEntity;

@CircuitBreaker(name = "messageRepository")
public interface MessageRepository extends JpaRepository<MessageEntity, String>, JpaSpecificationExecutor<MessageEntity> {

	Page<MessageEntity> findByConversation(ConversationEntity conversation, Pageable pageable);
}
