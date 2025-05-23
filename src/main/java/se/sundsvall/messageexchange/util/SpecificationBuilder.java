package se.sundsvall.messageexchange.util;

import static java.util.Objects.nonNull;

import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;

public class SpecificationBuilder<T> {

	private static final SpecificationBuilder<ConversationEntity> CONVERSATION_ENTITY_SPECIFICATION_BUILDER = new SpecificationBuilder<>();

	public static Specification<ConversationEntity> withNamespace(final String namespace) {
		return CONVERSATION_ENTITY_SPECIFICATION_BUILDER.buildEqualFilter("namespace", namespace);
	}

	public static Specification<ConversationEntity> withMunicipalityId(final String municipalityId) {
		return CONVERSATION_ENTITY_SPECIFICATION_BUILDER.buildEqualFilter("municipalityId", municipalityId);
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
