package se.sundsvall.chat.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Schema(description = "Represents a message within a conversation, including metadata and content.")
public class Message {

	@Schema(description = "The unique identifier of the message.", example = "7f77f9fd-d01d-4742-974a-714b911e3496")
	private String id;

	@Schema(description = "The sequence number of the message in the conversation.", example = "1")
	private String sequenceNumber;

	@Schema(description = "The identifier of the message this message is replying to, if any.", example = "msg-12344")
	private String inReplyTo;

	@Schema(description = "The timestamp when the message was created.", example = "2023-01-01T12:00:00")
	private OffsetDateTime created;

	@Schema(description = "The participant who created the message.")
	private Participant createdBy;

	@Schema(description = "The content of the message.", example = "Hello, how can I help you?")
	private String content;

	@ArraySchema(schema = @Schema(implementation = Participant.class, description = "The list of participants who have read the message."))
	private List<Participant> readBy;

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

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(final String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public Message withSequenceNumber(final String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
		return this;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(final String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public Message withInReplyTo(final String inReplyTo) {
		this.inReplyTo = inReplyTo;
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

	public Participant getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final Participant createdBy) {
		this.createdBy = createdBy;
	}

	public Message withCreatedBy(final Participant createdBy) {
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

	public List<Participant> getReadBy() {
		return readBy;
	}

	public void setReadBy(final List<Participant> readBy) {
		this.readBy = readBy;
	}

	public Message withReadBy(final List<Participant> readBy) {
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
		return Objects.equals(id, message.id) && Objects.equals(sequenceNumber, message.sequenceNumber) && Objects.equals(inReplyTo, message.inReplyTo) && Objects.equals(created, message.created)
			&& Objects.equals(createdBy, message.createdBy) && Objects.equals(content, message.content) && Objects.equals(readBy, message.readBy) && Objects.equals(attachments, message.attachments);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, sequenceNumber, inReplyTo, created, createdBy, content, readBy, attachments);
	}

	@Override
	public String toString() {
		return "Message{" +
			"id='" + id + '\'' +
			", sequenceNumber='" + sequenceNumber + '\'' +
			", inReplyTo='" + inReplyTo + '\'' +
			", created=" + created +
			", createdBy=" + createdBy +
			", content='" + content + '\'' +
			", readBy=" + readBy +
			", attachments=" + attachments +
			'}';
	}
}
