package se.sundsvall.messageexchange.api;

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.http.MediaType.TEXT_PLAIN;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import se.sundsvall.messageexchange.Application;
import se.sundsvall.messageexchange.api.model.Message;
import se.sundsvall.messageexchange.service.MessageService;

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

		final var request = new Message();
		final var multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("message", request, APPLICATION_JSON);
		multipartBodyBuilder.part("attachments", "file").filename("file.txt").contentType(TEXT_PLAIN);
		multipartBodyBuilder.part("attachments", "file").filename("file2.txt").contentType(TEXT_PLAIN);
		final var body = multipartBodyBuilder.build();

		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.contentType(MULTIPART_FORM_DATA)
			.body(BodyInserters.fromMultipartData(body))
			.exchange()
			.expectStatus().isCreated();

		verify(messageServiceMock).createMessage(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(CONVERSATION_ID), eq(request), any());
	}

	@Test
	void getMessages() {

		webTestClient.get()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk();

		verify(messageServiceMock).getMessages(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(CONVERSATION_ID), any());

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

	@Test
	void readErrandAttachment() {

		webTestClient.get()
			.uri(PATH + "/{messageId}/attachments/{attachmentId}", Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "id", CONVERSATION_ID, "messageId", MESSAGE_ID, "attachmentId", randomUUID().toString()))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk();

		verify(messageServiceMock).readErrandAttachment(eq(NAMESPACE), eq(MUNICIPALITY_ID), eq(CONVERSATION_ID), eq(MESSAGE_ID), any(), any());
	}
}
