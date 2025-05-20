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
import se.sundsvall.chat.api.model.MessageRequest;
import se.sundsvall.chat.service.MessageService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class MessageResourceTest {

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
	void postMessage() {

		final var request = new MessageRequest();

		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isCreated();

		verify(messageServiceMock).createMessage(MUNICIPALITY_ID, NAMESPACE, CONVERSATION_ID, request);
	}

	@Test
	void getMessages() {

		webTestClient.get()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk();

		verify(messageServiceMock).getMessages(MUNICIPALITY_ID, NAMESPACE, CONVERSATION_ID, null, null, null);

	}

	@Test
	void deleteMessage() {

		webTestClient.delete()
			.uri(PATH + "/{messageId}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID, "messageId", MESSAGE_ID))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isNoContent();

		verify(messageServiceMock).deleteMessage(MUNICIPALITY_ID, NAMESPACE, CONVERSATION_ID, MESSAGE_ID);
	}
}
