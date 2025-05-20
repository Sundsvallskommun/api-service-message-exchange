package se.sundsvall.chat.api;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.chat.Application;
import se.sundsvall.chat.api.model.ConversationRequest;
import se.sundsvall.chat.service.ConversationService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class ConversationResourceTest {

	private static final String NAMESPACE = "namespace";

	private static final String MUNICIPALITY_ID = "2281";

	private static final String CONVERSATION_ID = randomUUID().toString();

	private static final String PATH = "/{municipalityId}/{namespace}/conversations";

	@MockitoBean
	private ConversationService conversationServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createConversation() {

		final var request = new ConversationRequest();

		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isCreated();

		verify(conversationServiceMock).createConversation(NAMESPACE, MUNICIPALITY_ID, request);
	}

	@Test
	void getConversation() {

		webTestClient.get()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk();

		verify(conversationServiceMock).readConversation(NAMESPACE, MUNICIPALITY_ID, CONVERSATION_ID);
	}

	@Test
	void updateConversation() {

		final var request = new ConversationRequest();

		webTestClient.patch()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isOk();

		verify(conversationServiceMock).updateConversation(NAMESPACE, MUNICIPALITY_ID, CONVERSATION_ID, request);
	}

	@Test
	void deleteConversation() {

		webTestClient.delete()
			.uri(PATH + "/{id}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.exchange()
			.expectStatus().isNoContent();

		verify(conversationServiceMock).deleteConversation(NAMESPACE, MUNICIPALITY_ID, CONVERSATION_ID);
	}
}
