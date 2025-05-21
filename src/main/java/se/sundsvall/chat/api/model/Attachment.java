package se.sundsvall.chat.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Objects;

@Schema(description = "Represents an attachment in a message")
public class Attachment {

	@Schema(description = "Unique identifier for the attachment", example = "cb20c51f-fcf3-42c0-b613-de563634a8ec", accessMode = READ_ONLY)
	protected String id;

	@Schema(description = "Name of the file", example = "my-file.txt")
	protected String fileName;

	@Schema(description = "Mime type of the file", accessMode = Schema.AccessMode.READ_ONLY)
	private String mimeType;

	@Schema(description = "The attachment created date", example = "2023-01-01T00:00:00Z")
	private OffsetDateTime created;

	public static Attachment create() {
		return new Attachment();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Attachment withId(final String id) {
		this.id = id;
		return this;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public Attachment withFileName(final String fileName) {
		this.fileName = fileName;
		return this;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(final String mimeType) {
		this.mimeType = mimeType;
	}

	public Attachment withMimeType(final String mimeType) {
		this.mimeType = mimeType;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public Attachment withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Attachment that = (Attachment) o;
		return Objects.equals(id, that.id) && Objects.equals(fileName, that.fileName) && Objects.equals(mimeType, that.mimeType) && Objects.equals(created, that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, fileName, mimeType, created);
	}

	@Override
	public String toString() {
		return "Attachment{" +
			"id='" + id + '\'' +
			", fileName='" + fileName + '\'' +
			", mimeType='" + mimeType + '\'' +
			", created=" + created +
			'}';
	}
}
