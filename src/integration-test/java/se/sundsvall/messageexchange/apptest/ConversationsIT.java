package se.sundsvall.messageexchange.apptest;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.messageexchange.Application;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/ConversationsIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class ConversationsIT extends AbstractAppTest {

	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";
	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE_1 = "NAMESPACE-1";
	private static final String CONVERSATION_ID = "c1a1b2c3-d4e5-f6a7-b8c9-d0e1f2a3b4c5";
	private static final String PATH = "/" + MUNICIPALITY_ID + "/" + NAMESPACE_1 + "/conversations";

	@Test
	void test01_findConversation() {
		setupCall()
			.withServicePath(PATH + "/" + CONVERSATION_ID)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_createConversation() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(LOCATION, List.of(PATH + "/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_updateConversation() {
		setupCall()
			.withServicePath(PATH + "/" + CONVERSATION_ID)
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_deleteConversation() {
		setupCall()
			.withServicePath(PATH + "/" + CONVERSATION_ID)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.sendRequestAndVerifyResponse();
	}

}
