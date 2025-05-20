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
import se.sundsvall.chat.api.model.ConversationRequest;
import se.sundsvall.chat.service.ConversationService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class ConversationFailureTest {

	private static final String NAMESPACE = "namespace";
	private static final String MUNICIPALITY_ID = "2281";
	private static final String CONVERSATION_ID = randomUUID().toString();
	private static final String PATH = "/{municipalityId}/{namespace}/conversations";

	@MockitoBean
	private ConversationService conversationServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createConversationWithInvalidMunicipalityId() {
		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", "invalidMunicipalityId", "namespace", NAMESPACE))
			.contentType(APPLICATION_JSON)
			.bodyValue(new ConversationRequest())
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void createConversationWithInvalidNamespace() {
		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "#invalidNamespace"))
			.contentType(APPLICATION_JSON)
			.bodyValue(new ConversationRequest())
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void getConversationWithInvalidMunicipalityId() {
		webTestClient.get()
			.uri(PATH + "/{id}", Map.of("municipalityId", "invalidMunicipalityId", "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void getConversationWithInvalidNamespace() {
		webTestClient.get()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "#invalidNamespace", "id", CONVERSATION_ID))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void getConversationWithInvalidConversationId() {
		webTestClient.get()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", "invalidConversationId"))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void getConversationNotFound() {
		doThrow(Problem.valueOf(NOT_FOUND, "Conversation not found"))
			.when(conversationServiceMock)
			.readConversation(NAMESPACE, MUNICIPALITY_ID, CONVERSATION_ID);

		webTestClient.get()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isNotFound();
	}

	@Test
	void updateConversationWithInvalidConversationId() {
		webTestClient.patch()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", "invalidConversationId"))
			.contentType(APPLICATION_JSON)
			.bodyValue(new ConversationRequest())
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void deleteConversationNotFound() {
		doThrow(Problem.valueOf(NOT_FOUND, "Conversation not found"))
			.when(conversationServiceMock)
			.deleteConversation(NAMESPACE, MUNICIPALITY_ID, CONVERSATION_ID);

		webTestClient.delete()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.exchange()
			.expectStatus().isNotFound();
	}

	@Test
	void deleteConversationWithInvalidConversationId() {
		webTestClient.delete()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", "invalidConversationId"))
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void deleteConversationWithInvalidMunicipalityId() {
		webTestClient.delete()
			.uri(PATH + "/{id}", Map.of("municipalityId", "invalidMunicipalityId", "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void deleteConversationWithInvalidNamespace() {
		webTestClient.delete()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "#invalidNamespace", "id", CONVERSATION_ID))
			.exchange()
			.expectStatus().isBadRequest();
	}
}
