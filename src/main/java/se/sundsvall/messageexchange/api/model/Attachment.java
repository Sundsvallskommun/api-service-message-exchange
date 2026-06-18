package se.sundsvall.messageexchange.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Represents an attachment in a message")
public class Attachment {

	@Schema(description = "Unique identifier for the attachment", examples = "cb20c51f-fcf3-42c0-b613-de563634a8ec", accessMode = READ_ONLY)
	private String id;

	@Schema(description = "Name of the file", examples = "my-file.txt", accessMode = READ_ONLY)
	private String fileName;

	@Schema(description = "Size of the file in bytes", examples = "1024", accessMode = READ_ONLY)
	private int fileSize;

	@Schema(description = "Mime type of the file", accessMode = Schema.AccessMode.READ_ONLY)
	private String mimeType;

	@Schema(description = "SHA-256 content hash of the file (hex-encoded)", accessMode = READ_ONLY)
	private String hash;

	@Schema(description = "The attachment created date", examples = "2023-01-01T00:00:00Z", accessMode = READ_ONLY)
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

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(final int fileSize) {
		this.fileSize = fileSize;
	}

	public Attachment withFileSize(final int fileSize) {
		this.fileSize = fileSize;
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

	public String getHash() {
		return hash;
	}

	public void setHash(final String hash) {
		this.hash = hash;
	}

	public Attachment withHash(final String hash) {
		this.hash = hash;
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
		return Objects.equals(id, that.id) && Objects.equals(fileName, that.fileName) && Objects.equals(fileSize, that.fileSize) && Objects.equals(mimeType, that.mimeType) && Objects.equals(hash, that.hash) && Objects.equals(created,
			that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, fileName, fileSize, mimeType, hash, created);
	}

	@Override
	public String toString() {
		return "Attachment{" +
			"id='" + id + '\'' +
			", fileName='" + fileName + '\'' +
			", fileSize=" + fileSize +
			", mimeType='" + mimeType + '\'' +
			", hash='" + hash + '\'' +
			", created=" + created +
			'}';
	}
}
