package se.sundsvall.chat.apptest;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.chat.Application;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/MessagesIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
public class MessagesIT extends AbstractAppTest {

	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";
	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE_1 = "NAMESPACE-1";
	private static final String CONVERSATION_ID = "c1a1b2c3-d4e5-f6a7-b8c9-d0e1f2a3b4c5";
	private static final String MESSAGE_ID = "d82bd8ac-1507-4d9a-958d-369261eecc15";
	private static final String PATH = "/" + MUNICIPALITY_ID + "/" + NAMESPACE_1 + "/conversations/" + CONVERSATION_ID + "/messages";

	@Test
	void test01_findMessages() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_createMessage() throws FileNotFoundException {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withContentType(MULTIPART_FORM_DATA)
			.withRequestFile("message", REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(LOCATION, List.of(PATH + "/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_deleteMessage() {
		setupCall()
			.withServicePath(PATH + "/" + MESSAGE_ID)
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_readAttachment() throws IOException {
		setupCall()
			.withServicePath(PATH + "/" + MESSAGE_ID + "/attachments/a1a1b2c3-d4e5-f6a7-b8c9-d0e1f2a3b4c5")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(IMAGE_JPEG_VALUE))
			.withExpectedBinaryResponse("Test_image.jpg")
			.sendRequestAndVerifyResponse();
	}
}
