package se.sundsvall.messageexchange.util;

import static java.util.Objects.nonNull;

import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;

public class MessageSpecificationBuilder<T> {

	private static final MessageSpecificationBuilder<MessageEntity> MESSAGE_ENTITY_SPECIFICATION_BUILDER = new MessageSpecificationBuilder<>();

	public static Specification<MessageEntity> withConversation(final se.sundsvall.messageexchange.integration.db.model.ConversationEntity conversationEntity) {
		return MESSAGE_ENTITY_SPECIFICATION_BUILDER.buildEqualFilter("conversation", conversationEntity);
	}

	/**
	 * Method builds an equal filter if value is not null. If value is null, method returns an always-true predicate
	 * (meaning no filtering will be applied for sent in attribute)
	 *
	 * @param  attribute name that will be used in filter
	 * @param  value     value (or null) to compare against
	 * @return           Specification<T> matching sent in comparison
	 */
	private Specification<T> buildEqualFilter(final String attribute, final Object value) {
		return (entity, cq, cb) -> nonNull(value) ? cb.equal(entity.get(attribute), value) : cb.and();
	}
}
