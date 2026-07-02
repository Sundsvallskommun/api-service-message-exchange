package se.sundsvall.messageexchange.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;

@CircuitBreaker(name = "messageRepository")
public interface MessageRepository extends JpaRepository<MessageEntity, String>, JpaSpecificationExecutor<MessageEntity> {

	Page<MessageEntity> findByConversation(ConversationEntity conversation, Pageable pageable);

	Optional<MessageEntity> findTopByConversationIdOrderBySequenceNumberDesc(String conversationId);

	List<MessageEntity> findByConversationIdAndIdIn(String conversationId, Collection<String> ids);

	@Query("""
		select count(m) from MessageEntity m
		where m.conversation.id = :conversationId
		and (:includeSystemMessages = true or m.type <> se.sundsvall.messageexchange.integration.db.model.MessageType.SYSTEM_CREATED)
		""")
	long countMessages(@Param("conversationId") String conversationId, @Param("includeSystemMessages") boolean includeSystemMessages);

	@Query("""
		select r.identifier.type as type, r.identifier.value as value, count(m) as count from MessageEntity m
		join m.readBy r
		where m.conversation.id = :conversationId
		and (:includeSystemMessages = true or m.type <> se.sundsvall.messageexchange.integration.db.model.MessageType.SYSTEM_CREATED)
		group by r.identifier.type, r.identifier.value
		""")
	List<ReadByCountProjection> countReadByGroupedByIdentifier(@Param("conversationId") String conversationId, @Param("includeSystemMessages") boolean includeSystemMessages);

	@Query("""
		select rp.part as part, count(m) as count from MessageEntity m
		join m.readByPart rp
		where m.conversation.id = :conversationId
		and (:includeSystemMessages = true or m.type <> se.sundsvall.messageexchange.integration.db.model.MessageType.SYSTEM_CREATED)
		group by rp.part
		""")
	List<ReadByPartCountProjection> countReadByGroupedByPart(@Param("conversationId") String conversationId, @Param("includeSystemMessages") boolean includeSystemMessages);
}
