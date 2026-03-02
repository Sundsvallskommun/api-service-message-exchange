package se.sundsvall.messageexchange.configuration;

import com.turkraft.springfilter.parser.InvalidSyntaxException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionHandlerConfigTest {

	@Test
	void handleInvalidSyntaxException() {
		// Arrange
		final var errorMessage = "Invalid filter syntax!";
		final var exception = new InvalidSyntaxException(errorMessage);
		final var handler = new ExceptionHandlerConfig.ControllerExceptionHandler();

		// Act
		final var response = handler.handleInvalidSyntaxException(exception);
		final var problem = response.getBody();

		// Assert
		assertThat(response.getStatusCode().is4xxClientError()).isTrue();
		assertThat(problem).isNotNull();
		assertThat(problem.getTitle()).isEqualTo("Invalid Filter Content");
		assertThat(problem.getStatus()).isNotNull();
		assertThat(problem.getStatus().value()).isEqualTo(400);
		assertThat(problem.getDetail()).isEqualTo(errorMessage);
	}

	@Test
	void handleInvalidSyntaxExceptionWithNull() {
		// Arrange
		final var exception = new InvalidSyntaxException(null);
		final var handler = new ExceptionHandlerConfig.ControllerExceptionHandler();

		// Act
		final var response = handler.handleInvalidSyntaxException(exception);
		final var problem = response.getBody();

		// Assert
		assertThat(problem).isNotNull();
		assertThat(problem.getDetail()).contains("InvalidSyntaxException");
	}
}
