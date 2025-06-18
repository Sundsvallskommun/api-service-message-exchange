package se.sundsvall.messageexchange.api;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;
import static se.sundsvall.dept44.support.Identifier.HEADER_NAME;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.messageexchange.api.model.Message;
import se.sundsvall.messageexchange.api.validation.ValidNamespace;
import se.sundsvall.messageexchange.integration.db.model.MessageEntity;
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
		@Parameter(name = HEADER_NAME,
			description = "User ID of the request sender, used to log who sent the message. The value must follow the format: type=TYPE; VALUE. Valid types are 'adAccount' and 'partyId'.",
			example = "type=adAccount; joe01doe") @RequestHeader(name = HEADER_NAME, required = false) final String senderId,
		@PathVariable @ValidMunicipalityId @Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") final String municipalityId,
		@PathVariable @ValidNamespace @Parameter(name = "namespace", description = "Namespace", example = "MY_NAMESPACE") final String namespace,
		@PathVariable @ValidUuid @Parameter(name = "conversationId", description = "Conversation ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") final String conversationId,
		@RequestPart("message") @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Message to be posted") final Message message,
		@RequestPart(value = "attachments", required = false) @Parameter(name = "attachments", description = "List of attachments") final List<MultipartFile> attachments) {

		final var messageId = service.createMessage(municipalityId, namespace, conversationId, message, attachments);

		return created(fromPath("/{municipalityId}/{namespace}/conversations/{id}/messages/{messageId}")
			.buildAndExpand(municipalityId, namespace, conversationId, messageId).toUri())
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(description = "Get all messages for a conversation, paginated.", responses = {
		@ApiResponse(responseCode = "200", description = "OK - Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<Page<Message>> getMessages(
		@Parameter(name = HEADER_NAME,
			description = "User ID of the request sender, used to log who read the message. The value must follow the format: type=TYPE; VALUE. Valid types are 'adAccount' and 'partyId'.",
			example = "type=adAccount; joe01doe") @RequestHeader(name = HEADER_NAME, required = false) final String senderId,
		@PathVariable @ValidMunicipalityId @Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") final String municipalityId,
		@PathVariable @ValidNamespace @Parameter(name = "namespace", description = "Namespace", example = "MY_NAMESPACE") final String namespace,
		@PathVariable @ValidUuid @Parameter(name = "conversationId", description = "Conversation ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") final String conversationId,
		@Parameter(description = "Syntax description: [spring-filter](https://github.com/turkraft/spring-filter/blob/85730f950a5f8623159cc0eb4d737555f9382bb7/README.md#syntax)",
			example = "content:'My content' and createdBy.value:'joe01doe' and created>'2023-01-01T00:00:00Z'",
			schema = @Schema(implementation = String.class)) @Nullable @Filter final Specification<MessageEntity> filter,
		@ParameterObject final Pageable pageable) {

		return ResponseEntity.ok(service.getMessages(municipalityId, namespace, conversationId, filter, pageable));
	}

	@DeleteMapping(path = "/{messageId}", produces = ALL_VALUE)
	@Operation(description = "Delete a message", responses = {
		@ApiResponse(responseCode = "204", description = "No Content - Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<Void> deleteMessage(
		@PathVariable @ValidMunicipalityId @Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") final String municipalityId,
		@PathVariable @ValidNamespace @Parameter(name = "namespace", description = "Namespace", example = "MY_NAMESPACE") final String namespace,
		@PathVariable @ValidUuid @Parameter(name = "conversationId", description = "Conversation ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") final String conversationId,
		@PathVariable @ValidUuid @Parameter(name = "messageId", description = "Message ID", example = "d82bd8ac-1507-4d9a-958d-369261eecc15") final String messageId) {

		service.deleteMessage(municipalityId, namespace, conversationId, messageId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(path = "/{messageId}/attachments/{attachmentId}", produces = ALL_VALUE)
	@Operation(summary = "Read attachment", description = "Fetches the attachment that matches the provided message id and attachment id", responses = {
		@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
	})
	void readErrandAttachment(
		@PathVariable @ValidMunicipalityId @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") final String municipalityId,
		@PathVariable @ValidNamespace @Parameter(name = "namespace", description = "Namespace", example = "MY_NAMESPACE") final String namespace,
		@PathVariable @ValidUuid @Parameter(name = "conversationId", description = "Conversation ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") final String conversationId,
		@PathVariable @ValidUuid @Parameter(name = "messageId", description = "Message ID", example = "d82bd8ac-1507-4d9a-958d-369261eecc15") final String messageId,
		@PathVariable("attachmentId") @ValidUuid @Parameter(name = "attachmentId", description = "Errand attachment id", example = "5f79a808-0ef3-4985-99b9-b12f23e202a7") final String attachmentId,
		final HttpServletResponse response) {

		service.readErrandAttachment(namespace, municipalityId, conversationId, messageId, attachmentId, response);
	}
}
