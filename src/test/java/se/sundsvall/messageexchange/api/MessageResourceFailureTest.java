package se.sundsvall.messageexchange.api;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.dept44.support.Identifier.HEADER_NAME;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.messageexchange.Application;
import se.sundsvall.messageexchange.api.model.Message;
import se.sundsvall.messageexchange.service.MessageService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class MessageResourceFailureTest {

	private static final String NAMESPACE = "namespace";

	private static final String MUNICIPALITY_ID = "2281";

	private static final String CONVERSATION_ID = randomUUID().toString();

	private static final String MESSAGE_ID = randomUUID().toString();

	private static final String PATH = "/{municipalityId}/{namespace}/conversations/{id}/messages";

	@MockitoBean
	private MessageService messageServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void postMessageWithInvalidMunicipalityId() {

		final var request = new Message();
		final var multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("message", request, APPLICATION_JSON);
		multipartBodyBuilder.part("attachments", "file").filename("file.txt").contentType(TEXT_PLAIN);
		multipartBodyBuilder.part("attachments", "file").filename("file2.txt").contentType(TEXT_PLAIN);
		final var body = multipartBodyBuilder.build();

		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", "invalidMunicipalityId", "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.contentType(MULTIPART_FORM_DATA)
			.body(BodyInserters.fromMultipartData(body))
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void postMessageWithInvalidNamespace() {
		final var request = new Message();
		final var multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("message", request, APPLICATION_JSON);
		multipartBodyBuilder.part("attachments", "file").filename("file.txt").contentType(TEXT_PLAIN);
		multipartBodyBuilder.part("attachments", "file").filename("file2.txt").contentType(TEXT_PLAIN);
		final var body = multipartBodyBuilder.build();

		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "#invalidNamespace", "id", CONVERSATION_ID))
			.contentType(MULTIPART_FORM_DATA)
			.body(BodyInserters.fromMultipartData(body))
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void postMessageWithInvalidConversationId() {

		final var request = new Message();
		final var multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("message", request, APPLICATION_JSON);
		multipartBodyBuilder.part("attachments", "file").filename("file.txt").contentType(TEXT_PLAIN);
		multipartBodyBuilder.part("attachments", "file").filename("file2.txt").contentType(TEXT_PLAIN);
		final var body = multipartBodyBuilder.build();

		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", "invalidConversationId"))
			.contentType(MULTIPART_FORM_DATA)
			.body(BodyInserters.fromMultipartData(body))
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void postMessageWithEmptyAttachmentFile() {

		final var request = new Message();
		final var multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("message", request, APPLICATION_JSON);
		multipartBodyBuilder.part("attachments", "").filename("file.pdf").contentType(APPLICATION_PDF);
		final var body = multipartBodyBuilder.build();

		final var response = webTestClient.post()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.contentType(MULTIPART_FORM_DATA)
			.body(BodyInserters.fromMultipartData(body))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("postMessage.attachments", "all files must contain data"));
	}

	@Test
	void postMessageConversationNotFound() {

		final var request = new Message();
		final var multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("message", request, APPLICATION_JSON);
		multipartBodyBuilder.part("attachments", "file").filename("file.txt").contentType(TEXT_PLAIN);
		multipartBodyBuilder.part("attachments", "file").filename("file2.txt").contentType(TEXT_PLAIN);
		final var body = multipartBodyBuilder.build();

		doThrow(Problem.valueOf(NOT_FOUND, "Conversation not found"))
			.when(messageServiceMock)
			.createMessage(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(CONVERSATION_ID), eq(new Message()), any());

		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.contentType(MULTIPART_FORM_DATA)
			.header(HEADER_NAME, "type=adAccount; joe01doe")
			.body(BodyInserters.fromMultipartData(body))
			.exchange()
			.expectStatus().isNotFound();
	}

	@Test
	void getMessageWithInvalidMunicipalityId() {
		webTestClient.get()
			.uri(PATH, Map.of("municipalityId", "invalidMunicipalityId", "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void getMessageWithInvalidNamespace() {
		webTestClient.get()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "#invalidNamespace", "id", CONVERSATION_ID))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void getMessageWithInvalidConversationId() {

		webTestClient.get()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", "invalidConversationId"))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void getMessageConversationNotFound() {

		doThrow(Problem.valueOf(NOT_FOUND, "Conversation not found"))
			.when(messageServiceMock)
			.getMessages(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(CONVERSATION_ID), any(), any());

		webTestClient.get()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.header(HEADER_NAME, "type=adAccount; joe01doe")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isNotFound();
	}

	@Test
	void deleteMessageWithInvalidMunicipalityId() {
		webTestClient.delete()
			.uri(PATH + "/{messageId}", Map.of("municipalityId", "invalidMunicipalityId", "namespace", NAMESPACE, "id", CONVERSATION_ID, "messageId", MESSAGE_ID))
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void deleteMessageWithInvalidNamespace() {
		webTestClient.delete()
			.uri(PATH + "/{messageId}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "#invalidNamespace", "id", CONVERSATION_ID, "messageId", MESSAGE_ID))
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void deleteMessageWithInvalidMessageId() {
		webTestClient.delete()
			.uri(PATH + "/{messageId}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID, "messageId", "invalidMessageId"))
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void deleteMessageWithInvalidConversationId() {
		webTestClient.delete()
			.uri(PATH + "/{messageId}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", "invalidConversationId", "messageId", MESSAGE_ID))
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void deleteMessageNotFound() {

		doThrow(Problem.valueOf(NOT_FOUND, "Message not found"))
			.when(messageServiceMock)
			.deleteMessage(MUNICIPALITY_ID, NAMESPACE, CONVERSATION_ID, MESSAGE_ID);

		webTestClient.delete()
			.uri(PATH + "/{messageId}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID, "messageId", MESSAGE_ID))
			.exchange()
			.expectStatus().isNotFound();
	}

	@Test
	void deleteMessageConversationNotFound() {

		doThrow(Problem.valueOf(NOT_FOUND, "Conversation not found"))
			.when(messageServiceMock)
			.deleteMessage(MUNICIPALITY_ID, NAMESPACE, CONVERSATION_ID, MESSAGE_ID);

		webTestClient.delete()
			.uri(PATH + "/{messageId}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID, "messageId", MESSAGE_ID))
			.exchange()
			.expectStatus().isNotFound();
	}

	@Test
	void readErrandAttachmentWithInvalidMunicipalityId() {
		webTestClient.get()
			.uri(PATH + "/{messageId}/attachments/{attachmentId}", Map.of("municipalityId", "invalidMunicipalityId", "namespace", NAMESPACE, "id", CONVERSATION_ID, "messageId", MESSAGE_ID, "attachmentId", randomUUID().toString()))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void readErrandAttachmentWithInvalidNamespace() {
		webTestClient.get()
			.uri(PATH + "/{messageId}/attachments/{attachmentId}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "#invalidNamespace", "id", CONVERSATION_ID, "messageId", MESSAGE_ID, "attachmentId", randomUUID().toString()))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void readErrandAttachmentWithInvalidMessageId() {
		webTestClient.get()
			.uri(PATH + "/{messageId}/attachments/{attachmentId}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID, "messageId", "invalidMessageId", "attachmentId", randomUUID().toString()))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void readErrandAttachmentWithInvalidConversationId() {
		webTestClient.get()
			.uri(PATH + "/{messageId}/attachments/{attachmentId}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", "invalidConversationId", "messageId", MESSAGE_ID, "attachmentId", randomUUID().toString()))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void readErrandAttachmentWithInvalidAttachmentId() {
		webTestClient.get()
			.uri(PATH + "/{messageId}/attachments/{attachmentId}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID, "messageId", MESSAGE_ID, "attachmentId", "invalidAttachmentId"))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void readErrandAttachmentNotFound() {

		doThrow(Problem.valueOf(NOT_FOUND, "Attachment not found"))
			.when(messageServiceMock)
			.readErrandAttachment(eq(NAMESPACE), eq(MUNICIPALITY_ID), eq(CONVERSATION_ID), eq(MESSAGE_ID), any(), any());

		webTestClient.get()
			.uri(PATH + "/{messageId}/attachments/{attachmentId}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID, "messageId", MESSAGE_ID, "attachmentId", randomUUID().toString()))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isNotFound();
	}
}
