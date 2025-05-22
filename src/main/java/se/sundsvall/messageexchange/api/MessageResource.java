package se.sundsvall.messageexchange.api;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;
import static se.sundsvall.messageexchange.Constants.NAMESPACE_REGEXP;
import static se.sundsvall.messageexchange.Constants.NAMESPACE_VALIDATION_MESSAGE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.messageexchange.api.model.Message;
import se.sundsvall.messageexchange.service.MessageService;

@RestController
@Validated
@RequestMapping("/{municipalityId}/{namespace}/conversations/{conversationId}/messages")
@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
	Problem.class, ConstraintViolationProblem.class
})))
@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@Tag(name = "Message resource", description = "Message operations")
class MessageResource {

	private final MessageService service;

	MessageResource(final MessageService service) {
		this.service = service;
	}

	@PostMapping(consumes = MULTIPART_FORM_DATA_VALUE, produces = ALL_VALUE)
	@Operation(description = "Post a message with attachment", responses = {
		@ApiResponse(responseCode = "201", description = "Created - Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<Void> postMessage(
		@Parameter(name = "namespace", description = "Namespace", example = "MY_NAMESPACE") @Pattern(regexp = NAMESPACE_REGEXP, message = NAMESPACE_VALIDATION_MESSAGE) @PathVariable final String namespace,
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "conversationId", description = "Conversation ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String conversationId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Message to be posted") @RequestPart("message") final Message message,
		@Parameter(name = "attachments", description = "List of attachments") @RequestPart(value = "attachments", required = false) final List<MultipartFile> attachments) {

		final var messageId = service.createMessage(municipalityId, namespace, conversationId, message, attachments);

		return created(fromPath("/{municipalityId}/{namespace}/conversations/{id}/messages/{messageId}")
			.buildAndExpand(municipalityId, namespace, conversationId, messageId).toUri())
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(description = "Get messages (lazy load, pageable, sortable)", responses = {
		@ApiResponse(responseCode = "200", description = "OK - Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<Page<Message>> getMessages(
		@Parameter(name = "namespace", description = "Namespace", example = "MY_NAMESPACE") @Pattern(regexp = NAMESPACE_REGEXP, message = NAMESPACE_VALIDATION_MESSAGE) @PathVariable final String namespace,
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "conversationId", description = "Conversation ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String conversationId,
		@ParameterObject final Pageable pageable) {

		return ResponseEntity.ok(service.getMessages(municipalityId, namespace, conversationId, pageable));
	}

	@DeleteMapping(path = "/{messageId}", produces = ALL_VALUE)
	@Operation(description = "Delete a message", responses = {
		@ApiResponse(responseCode = "204", description = "No Content - Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<Void> deleteMessage(
		@Parameter(name = "namespace", description = "Namespace", example = "MY_NAMESPACE") @Pattern(regexp = NAMESPACE_REGEXP, message = NAMESPACE_VALIDATION_MESSAGE) @PathVariable final String namespace,
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "conversationId", description = "Conversation ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String conversationId,
		@Parameter(name = "messageId", description = "Message ID", example = "d82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String messageId) {

		service.deleteMessage(municipalityId, namespace, conversationId, messageId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(path = "/{messageId}/attachments/{attachmentId}", produces = ALL_VALUE)
	@Operation(summary = "Read attachment", description = "Fetches the attachment that matches the provided message id and attachment id", responses = {
		@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
	})
	void readErrandAttachment(
		@Parameter(name = "namespace", description = "Namespace", example = "MY_NAMESPACE") @Pattern(regexp = NAMESPACE_REGEXP, message = NAMESPACE_VALIDATION_MESSAGE) @PathVariable final String namespace,
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "conversationId", description = "Conversation ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String conversationId,
		@Parameter(name = "messageId", description = "Message ID", example = "d82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String messageId,
		@Parameter(name = "attachmentId", description = "Errand attachment id", example = "5f79a808-0ef3-4985-99b9-b12f23e202a7") @ValidUuid @PathVariable("attachmentId") final String attachmentId,
		final HttpServletResponse response) {

		service.readErrandAttachment(namespace, municipalityId, conversationId, messageId, attachmentId, response);
	}

}
