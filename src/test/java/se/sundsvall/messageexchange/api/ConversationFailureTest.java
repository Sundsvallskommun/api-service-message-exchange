package se.sundsvall.messageexchange.api;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import se.sundsvall.messageexchange.Application;
import se.sundsvall.messageexchange.api.model.Conversation;
import se.sundsvall.messageexchange.service.ConversationService;

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
			.bodyValue(new Conversation())
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(conversationServiceMock);
	}

	@Test
	void createConversationWithInvalidNamespace() {
		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "#invalidNamespace"))
			.contentType(APPLICATION_JSON)
			.bodyValue(new Conversation())
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(conversationServiceMock);

	}

	@Test
	void getConversationsWithInvalidMunicipalityId() {
		webTestClient.get()
			.uri(PATH, Map.of("municipalityId", "invalidMunicipalityId", "namespace", NAMESPACE))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(conversationServiceMock);
	}

	@Test
	void getConversationsWithInvalidNamespace() {
		webTestClient.get()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "#invalidNamespace"))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(conversationServiceMock);

	}

	@Test
	void getConversationsWithInvalidFilter() {
		// Call
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH).queryParam("filter", "topic:'My topic' and").build(Map.of("namespace", NAMESPACE, "municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Invalid Filter Content");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo("mismatched input '<EOF>' expecting {PREFIX_OPERATOR, TRUE, FALSE, '(', '[', '`', ID, NUMBER, STRING}");

		// Verification
		verifyNoInteractions(conversationServiceMock);
	}

	@Test
	void getConversationWithInvalidMunicipalityId() {
		webTestClient.get()
			.uri(PATH + "/{id}", Map.of("municipalityId", "invalidMunicipalityId", "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(conversationServiceMock);
	}

	@Test
	void getConversationWithInvalidNamespace() {
		webTestClient.get()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "#invalidNamespace", "id", CONVERSATION_ID))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(conversationServiceMock);

	}

	@Test
	void getConversationWithInvalidConversationId() {
		webTestClient.get()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", "invalidConversationId"))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(conversationServiceMock);

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

		verify(conversationServiceMock).readConversation(NAMESPACE, MUNICIPALITY_ID, CONVERSATION_ID);
		verifyNoMoreInteractions(conversationServiceMock);
	}

	@Test
	void updateConversationWithInvalidConversationId() {
		webTestClient.patch()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", "invalidConversationId"))
			.contentType(APPLICATION_JSON)
			.bodyValue(new Conversation())
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(conversationServiceMock);

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

		verify(conversationServiceMock).deleteConversation(NAMESPACE, MUNICIPALITY_ID, CONVERSATION_ID);
		verifyNoMoreInteractions(conversationServiceMock);

	}

	@Test
	void deleteConversationWithInvalidConversationId() {
		webTestClient.delete()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", "invalidConversationId"))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(conversationServiceMock);
	}

	@Test
	void deleteConversationWithInvalidMunicipalityId() {
		webTestClient.delete()
			.uri(PATH + "/{id}", Map.of("municipalityId", "invalidMunicipalityId", "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(conversationServiceMock);
	}

	@Test
	void deleteConversationWithInvalidNamespace() {
		webTestClient.delete()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "#invalidNamespace", "id", CONVERSATION_ID))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(conversationServiceMock);
	}
}
