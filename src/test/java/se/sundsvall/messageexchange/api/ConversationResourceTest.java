package se.sundsvall.messageexchange.api;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.messageexchange.Application;
import se.sundsvall.messageexchange.api.model.Conversation;
import se.sundsvall.messageexchange.service.ConversationService;

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

		final var request = new Conversation();

		webTestClient.post()
			.uri(PATH, Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isCreated();

		verify(conversationServiceMock).createConversation(NAMESPACE, MUNICIPALITY_ID, request);
	}

	@Test
	void getConversationsWithoutFilter() {
		// Arrange
		final var pageable = PageRequest.of(0, 20);
		final var matches = new PageImpl<>(List.of(Conversation.create()), pageable, 1);

		when(conversationServiceMock.readConversations(eq(NAMESPACE), eq(MUNICIPALITY_ID), ArgumentMatchers.any(), eq(pageable))).thenReturn(matches);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH)
				.build(Map.of("namespace", NAMESPACE, "municipalityId", MUNICIPALITY_ID)))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(new ParameterizedTypeReference<Page<Conversation>>() {

			})
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getContent()).hasSize(1);
		verify(conversationServiceMock).readConversations(eq(NAMESPACE), eq(MUNICIPALITY_ID), ArgumentMatchers.any(), eq(pageable));
	}

	@Test
	void getConversationsWithFilter() {
		// Arrange
		final var page = 13;
		final var size = 37;
		final var pageable = PageRequest.of(page, size);
		final var matches = new PageImpl<>(List.of(Conversation.create()), pageable, 1);
		final var filter = "topic:'`My Topic`' and messages.createdBy.value:'joe01doe'";

		when(conversationServiceMock.readConversations(eq(NAMESPACE), eq(MUNICIPALITY_ID), ArgumentMatchers.any(), eq(pageable))).thenReturn(matches);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH)
				.queryParam("filter", filter)
				.queryParam("page", page)
				.queryParam("size", size)
				.build(Map.of("namespace", NAMESPACE, "municipalityId", MUNICIPALITY_ID)))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(new ParameterizedTypeReference<Page<Conversation>>() {

			})
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getContent()).hasSize(1);
		verify(conversationServiceMock).readConversations(eq(NAMESPACE), eq(MUNICIPALITY_ID), ArgumentMatchers.any(), eq(pageable));
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

		final var request = new Conversation();

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
