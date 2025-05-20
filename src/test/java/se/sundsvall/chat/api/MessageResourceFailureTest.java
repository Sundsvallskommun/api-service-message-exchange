package se.sundsvall.chat.api;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.doThrow;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import se.sundsvall.chat.Application;
import se.sundsvall.chat.api.model.Message;
import se.sundsvall.chat.api.model.MessageRequest;
import se.sundsvall.chat.service.MessageService;

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
		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", "invalidMunicipalityId", "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.contentType(APPLICATION_JSON)
			.bodyValue(new Message())
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void postMessageWithInvalidNamespace() {
		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "#invalidNamespace", "id", CONVERSATION_ID))
			.contentType(APPLICATION_JSON)
			.bodyValue(new Message())
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void postMessageWithInvalidConversationId() {

		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", "invalidConversationId"))
			.contentType(APPLICATION_JSON)
			.bodyValue(new Message())
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void postMessageConversationNotFound() {

		doThrow(Problem.valueOf(NOT_FOUND, "Conversation not found"))
			.when(messageServiceMock)
			.createMessage(MUNICIPALITY_ID, NAMESPACE, CONVERSATION_ID, new MessageRequest());

		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.contentType(APPLICATION_JSON)
			.bodyValue(new Message())
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
			.getMessages(MUNICIPALITY_ID, NAMESPACE, CONVERSATION_ID, null, null, null);

		webTestClient.get()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
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
}
