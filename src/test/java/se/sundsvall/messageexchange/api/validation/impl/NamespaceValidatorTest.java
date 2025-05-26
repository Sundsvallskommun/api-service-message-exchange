package se.sundsvall.messageexchange.api.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NamespaceValidatorTest {

	private NamespaceValidator validator;

	@BeforeEach
	void setUp() {
		validator = new NamespaceValidator();
	}

	@Test
	void testValidNamespace() {
		assertThat(validator.isValid("valid-namespace_123", null)).isTrue();
	}

	@Test
	void testInvalidNamespace() {
		assertThat(validator.isValid("invalid namespace!", null)).isFalse();
	}

	@Test
	void testNullNamespace() {
		assertThat(validator.isValid(null, null)).isFalse();
	}

	@Test
	void testEmptyNamespace() {
		assertThat(validator.isValid("", null)).isFalse();
	}

	@Test
	void testNamespaceWithSpecialCharacters() {
		assertThat(validator.isValid("invalid@namespace", null)).isFalse();
	}
}
