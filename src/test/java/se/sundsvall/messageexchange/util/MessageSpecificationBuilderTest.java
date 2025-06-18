package se.sundsvall.messageexchange.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;

@SuppressWarnings({
	"unchecked"
})
class MessageSpecificationBuilderTest {

	@Test
	void withConversation_whenConversationIsNotNull() {
		// Arrange
		final var conversationEntity = mock(ConversationEntity.class);
		final var root = mock(Root.class);
		final var criteriaQuery = mock(CriteriaQuery.class);
		final var criteriaBuilder = mock(CriteriaBuilder.class);
		final var path = mock(Path.class);
		final var expectedPredicate = mock(Predicate.class);

		when(root.get("conversation")).thenReturn(path);
		when(criteriaBuilder.equal(path, conversationEntity)).thenReturn(expectedPredicate);

		// Act
		final var result = MessageSpecificationBuilder.withConversation(conversationEntity);
		final var predicate = result.toPredicate(root, criteriaQuery, criteriaBuilder);

		// Assert
		assertThat(result).isNotNull();
		assertThat(predicate).isSameAs(expectedPredicate);
		verify(root).get("conversation");
		verify(criteriaBuilder).equal(path, conversationEntity);
	}

	@Test
	void withConversation_whenConversationIsNull() {
		// Arrange
		final var root = mock(Root.class);
		final var criteriaQuery = mock(CriteriaQuery.class);
		final var criteriaBuilder = mock(CriteriaBuilder.class);
		final var emptyPredicate = mock(Predicate.class);

		when(criteriaBuilder.and()).thenReturn(emptyPredicate);

		// Act
		final var result = MessageSpecificationBuilder.withConversation(null);
		final var predicate = result.toPredicate(root, criteriaQuery, criteriaBuilder);

		// Assert
		assertThat(result).isNotNull();
		assertThat(predicate).isSameAs(emptyPredicate);
		verify(criteriaBuilder).and();
	}
}
