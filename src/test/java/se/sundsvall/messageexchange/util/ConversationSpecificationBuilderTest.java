package se.sundsvall.messageexchange.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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
class ConversationSpecificationBuilderTest {

	@Test
	void withNamespaceShouldReturnSpecificationThatFiltersByNamespace() {

		// Arrange
		final var spec = ConversationSpecificationBuilder.withNamespace("test-namespace");
		final var root = (Root<ConversationEntity>) mock(Root.class);
		final var query = mock(CriteriaQuery.class);
		final var cb = mock(CriteriaBuilder.class);
		final var path = mock(Path.class);
		final var predicate = mock(Predicate.class);
		when(root.get("namespace")).thenReturn(path);
		when(cb.equal(path, "test-namespace")).thenReturn(predicate);

		// Act
		final var result = spec.toPredicate(root, query, cb);

		// Assert
		assertThat(result).isEqualTo(predicate);
	}

	@Test
	void withNamespaceShouldReturnAlwaysTruePredicateWhenNull() {
		// Arrange
		final var spec = ConversationSpecificationBuilder.withNamespace(null);
		final var root = (Root<ConversationEntity>) mock(Root.class);
		final var query = mock(CriteriaQuery.class);
		final var cb = mock(CriteriaBuilder.class);
		final var alwaysTrue = mock(Predicate.class);
		when(cb.and()).thenReturn(alwaysTrue);

		// Act
		final var result = spec.toPredicate(root, query, cb);

		// Assert
		assertThat(result).isEqualTo(alwaysTrue);
	}

	@Test
	void withMunicipalityIdShouldReturnSpecificationThatFiltersByMunicipalityId() {
		// Arrange
		final var spec = ConversationSpecificationBuilder.withMunicipalityId("123");
		final var root = (Root<ConversationEntity>) mock(Root.class);
		final var query = mock(CriteriaQuery.class);
		final var cb = mock(CriteriaBuilder.class);
		final var path = mock(Path.class);
		final var predicate = mock(Predicate.class);
		when(root.get("municipalityId")).thenReturn(path);
		when(cb.equal(path, "123")).thenReturn(predicate);

		// Act
		final var result = spec.toPredicate(root, query, cb);

		// Assert
		assertThat(result).isEqualTo(predicate);
	}

	@Test
	void withMunicipalityIdShouldReturnAlwaysTruePredicateWhenNull() {
		final var spec = ConversationSpecificationBuilder.withMunicipalityId(null);
		final var root = (Root<ConversationEntity>) mock(Root.class);
		final var query = mock(CriteriaQuery.class);
		final var cb = mock(CriteriaBuilder.class);
		final var alwaysTrue = mock(Predicate.class);
		when(cb.and()).thenReturn(alwaysTrue);

		// Act
		final var result = spec.toPredicate(root, query, cb);

		// Assert
		assertThat(result).isEqualTo(alwaysTrue);
	}
}
