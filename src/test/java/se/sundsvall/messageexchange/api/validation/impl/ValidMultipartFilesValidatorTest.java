package se.sundsvall.messageexchange.api.validation.impl;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class ValidMultipartFilesValidatorTest {

	private ValidMultipartFilesValidator validator;

	@BeforeEach
	void setUp() {
		validator = new ValidMultipartFilesValidator();
	}

	@Test
	void testValidFiles() {

		// Arrange
		List<MultipartFile> files = List.of(
			new MockMultipartFile("file1", "hello.txt", "text/plain", "Hello".getBytes()),
			new MockMultipartFile("file2", "world.txt", "text/plain", "World".getBytes()));

		// Act
		final var result = validator.isValid(files, null);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	void testEmptyFile() {

		// Arrange
		List<MultipartFile> files = List.of(
			new MockMultipartFile("file1", "empty.txt", "text/plain", new byte[0]),
			new MockMultipartFile("file2", "world.txt", "text/plain", "World".getBytes()));

		// Act
		final var result = validator.isValid(files, null);

		// Assert
		assertThat(result).isFalse();

	}

	@Test
	void testNullFileInList() {

		// Arrange
		List<MultipartFile> files = new ArrayList<>();
		files.add(null);
		files.add(new MockMultipartFile("file2", "world.txt", "text/plain", "World".getBytes()));

		// Act
		final var result = validator.isValid(files, null);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	void testEmptyList() {

		// Act
		final var result = validator.isValid(emptyList(), null);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	void testNullList() {

		// Act
		final var result = validator.isValid(null, null);

		// Assert
		assertThat(result).isTrue();
	}
}
