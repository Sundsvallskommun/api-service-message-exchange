package se.sundsvall.chat.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.util.UUID;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbBlob;

class AttachmentDataEntityTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(AttachmentDataEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {

		// Arrange
		final var id = UUID.randomUUID().toString();
		final var blob = new MariaDbBlob();

		// Act
		final var attachmentData = AttachmentDataEntity.create()
			.withId(id)
			.withFile(blob);

		// Assert
		assertThat(attachmentData).hasNoNullFieldsOrProperties();
		assertThat(attachmentData.getFile()).isSameAs(blob);
		assertThat(attachmentData.getId()).isEqualTo(id);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(AttachmentDataEntity.create()).hasAllNullFieldsOrPropertiesExcept();
		assertThat(new AttachmentDataEntity()).hasAllNullFieldsOrProperties();
	}

}
