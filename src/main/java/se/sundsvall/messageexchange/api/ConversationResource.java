package se.sundsvall.messageexchange.api;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.messageexchange.api.model.Conversation;
import se.sundsvall.messageexchange.api.validation.ValidNamespace;
import se.sundsvall.messageexchange.integration.db.model.ConversationEntity;
import se.sundsvall.messageexchange.service.ConversationService;

@RestController
@Validated
@RequestMapping("/{municipalityId}/{namespace}/conversations")
@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
	Problem.class, ConstraintViolationProblem.class
})))
@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@Tag(name = "Conversation resource", description = "Conversation operations")
class ConversationResource {

	private final ConversationService service;

	ConversationResource(final ConversationService service) {
		this.service = service;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(description = "Get all conversations with or without filters. The resource allows the client a wide range of variations on how to filter the result.", responses = {
		@ApiResponse(responseCode = "200", description = "OK - Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<Page<Conversation>> getConversations(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "namespace", description = "Namespace", example = "MY_NAMESPACE") @ValidNamespace @PathVariable final String namespace,
		@Parameter(description = "Syntax description: [spring-filter](https://github.com/turkraft/spring-filter/blob/85730f950a5f8623159cc0eb4d737555f9382bb7/README.md#syntax)",
			example = "topic:'My topic' and messages.createdBy.value:'joe01doe' and messages.created>'2023-01-01T00:00:00Z'",
			schema = @Schema(implementation = String.class)) @Nullable @Filter final Specification<ConversationEntity> filter,
		@ParameterObject final Pageable pageable) {
		return ResponseEntity.ok(service.readConversations(namespace, municipalityId, filter, pageable));
	}

	@GetMapping(path = "/{conversationId}", produces = APPLICATION_JSON_VALUE)
	@Operation(description = "Get a conversation", responses = {
		@ApiResponse(responseCode = "200", description = "OK - Successful operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Conversation> getConversation(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "namespace", description = "Namespace", example = "MY_NAMESPACE") @ValidNamespace @PathVariable final String namespace,
		@Parameter(name = "conversationId", description = "Conversation ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String conversationId) {

		return ResponseEntity.ok(service.readConversation(namespace, municipalityId, conversationId));
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = ALL_VALUE)
	@Operation(description = "Create a conversation", responses = {
		@ApiResponse(responseCode = "201", description = "Created - Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<Void> createConversation(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "namespace", description = "Namespace", example = "MY_NAMESPACE") @ValidNamespace @PathVariable final String namespace,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Conversation to be created") @RequestBody final Conversation conversation) {

		final var id = service.createConversation(namespace, municipalityId, conversation);
		return created(fromPath("/{municipalityId}/{namespace}/conversations/{id}")
			.buildAndExpand(municipalityId, namespace, id).toUri())
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@PatchMapping(path = "/{conversationId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@Operation(description = "Update a conversation", responses = {
		@ApiResponse(responseCode = "200", description = "OK - Successful operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Conversation> updateConversation(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "namespace", description = "Namespace", example = "MY_NAMESPACE") @ValidNamespace @PathVariable final String namespace,
		@Parameter(name = "conversationId", description = "Conversation ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String conversationId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Conversation to be updated") @RequestBody final Conversation conversation) {

		return ResponseEntity.ok(service.updateConversation(namespace, municipalityId, conversationId, conversation));
	}

	@DeleteMapping(path = "/{conversationId}", produces = APPLICATION_JSON_VALUE)
	@Operation(description = "Delete a conversation", responses = {
		@ApiResponse(responseCode = "204", description = "No Content - Successful operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Void> deleteConversation(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "namespace", description = "Namespace", example = "MY_NAMESPACE") @ValidNamespace @PathVariable final String namespace,
		@Parameter(name = "conversationId", description = "Conversation ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String conversationId) {

		service.deleteConversation(namespace, municipalityId, conversationId);
		return ResponseEntity.noContent().build();
	}

}
