package se.sundsvall.messageexchange.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@Schema(description = "Represents a message within a conversation, including metadata and content.")
public class Message {

	@Schema(description = "The unique identifier of the message.", example = "7f77f9fd-d01d-4742-974a-714b911e3496", accessMode = Schema.AccessMode.READ_ONLY)
	private String id;

	@Schema(description = "The sequence number of the message in the conversation.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Long sequenceNumber;

	@Schema(description = "The identifier of the message this message is replying to, if any.", example = "2af1002e-008f-4bdc-924b-daaae31f1118")
	@ValidUuid(nullable = true)
	private String inReplyToMessageId;

	@Schema(description = "The timestamp when the message was created.", example = "2023-01-01T12:00:00", accessMode = Schema.AccessMode.READ_ONLY)
	private OffsetDateTime created;

	@Schema(description = "The participant who created the message.", accessMode = Schema.AccessMode.READ_ONLY)
	private Identifier createdBy;

	@Schema(description = "The content of the message.", example = "Hello, how can I help you?")
	private String content;

	@ArraySchema(schema = @Schema(implementation = Identifier.class, description = "The list of people who have read the message."))
	private List<ReadBy> readBy;

	@ArraySchema(schema = @Schema(implementation = Attachment.class, description = "The list of attachments associated with the message."))
	private List<Attachment> attachments;

	public static Message create() {
		return new Message();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Message withId(final String id) {
		this.id = id;
		return this;
	}

	public Long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(final Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public Message withSequenceNumber(final Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
		return this;
	}

	public String getInReplyToMessageId() {
		return inReplyToMessageId;
	}

	public void setInReplyToMessageId(final String inReplyToMessageId) {
		this.inReplyToMessageId = inReplyToMessageId;
	}

	public Message withInReplyToMessageId(final String inReplyTo) {
		this.inReplyToMessageId = inReplyTo;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public Message withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public Identifier getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final Identifier createdBy) {
		this.createdBy = createdBy;
	}

	public Message withCreatedBy(final Identifier createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public Message withContent(final String content) {
		this.content = content;
		return this;
	}

	public List<ReadBy> getReadBy() {
		return readBy;
	}

	public void setReadBy(final List<ReadBy> readBy) {
		this.readBy = readBy;
	}

	public Message withReadBy(final List<ReadBy> readBy) {
		this.readBy = readBy;
		return this;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(final List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public Message withAttachments(final List<Attachment> attachments) {
		this.attachments = attachments;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Message message = (Message) o;
		return Objects.equals(id, message.id) && Objects.equals(sequenceNumber, message.sequenceNumber) && Objects.equals(inReplyToMessageId, message.inReplyToMessageId) && Objects.equals(created, message.created)
			&& Objects.equals(createdBy, message.createdBy) && Objects.equals(content, message.content) && Objects.equals(readBy, message.readBy) && Objects.equals(attachments, message.attachments);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, sequenceNumber, inReplyToMessageId, created, createdBy, content, readBy, attachments);
	}

	@Override
	public String toString() {
		return "Message{" +
			"id='" + id + '\'' +
			", sequenceNumber='" + sequenceNumber + '\'' +
			", inReplyToMessageId='" + inReplyToMessageId + '\'' +
			", created=" + created +
			", createdBy=" + createdBy +
			", content='" + content + '\'' +
			", readBy=" + readBy +
			", attachments=" + attachments +
			'}';
	}
}
