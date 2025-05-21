package se.sundsvall.chat.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.CoreMatchers.allOf;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.OffsetDateTime;
import java.util.Random;
import java.util.UUID;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbBlob;

class AttachmentEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(AttachmentEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void hasValidBuilderMethods() {

		// Arrange
		final var id = UUID.randomUUID().toString();
		final var fileName = "fileName";
		final var file = new AttachmentDataEntity().withFile(new MariaDbBlob("file".getBytes()));
		final var mimeType = "mimeType";
		final var fileSize = 100;
		final var messageEntity = MessageEntity.create();

		// Act
		final var attachmentEntity = AttachmentEntity.create()
			.withId(id)
			.withFileName(fileName)
			.withAttachmentData(file)
			.withMimeType(mimeType)
			.withCreated(now().truncatedTo(SECONDS))
			.withFileSize(fileSize)
			.withMessageEntity(messageEntity);

		// Assert
		assertThat(attachmentEntity).hasNoNullFieldsOrProperties();
		assertThat(attachmentEntity.getId()).isEqualTo(id);
		assertThat(attachmentEntity.getFileName()).isEqualTo(fileName);
		assertThat(attachmentEntity.getAttachmentData()).isEqualTo(file);
		assertThat(attachmentEntity.getMimeType()).isEqualTo(mimeType);
		assertThat(attachmentEntity.getFileSize()).isEqualTo(fileSize);
	}

	@Test
	void testOnCreate() {
		final var entity = new AttachmentEntity();
		entity.onCreate();

		assertThat(entity.getCreated()).isCloseTo(now(), within(1, SECONDS));
		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("created");
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(AttachmentEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new AttachmentEntity()).hasAllNullFieldsOrProperties();
	}

}
