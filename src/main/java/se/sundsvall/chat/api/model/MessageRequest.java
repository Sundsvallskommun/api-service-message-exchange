package se.sundsvall.chat.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "Represents a message within a conversation, including metadata and content.")
public class MessageRequest {

	@Schema(description = "The sequence number of the message in the conversation.", example = "1")
	private String sequenceNumber;

	@Schema(description = "The identifier of the message this message is replying to, if any.", example = "msg-12344")
	private String inReplyTo;

	@Schema(description = "The participant who created the message.")
	private Participant createdBy;

	@Schema(description = "The content of the message.", example = "Hello, how can I help you?")
	private String content;

	@Schema(description = "The list of participants who have read the message.")
	private List<Participant> readBy;

	public static MessageRequest create() {
		return new MessageRequest();
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(final String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public MessageRequest withSequenceNumber(final String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
		return this;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(final String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public MessageRequest withInReplyTo(final String inReplyTo) {
		this.inReplyTo = inReplyTo;
		return this;
	}

	public Participant getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final Participant createdBy) {
		this.createdBy = createdBy;
	}

	public MessageRequest withCreatedBy(final Participant createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public MessageRequest withContent(final String content) {
		this.content = content;
		return this;
	}

	public List<Participant> getReadBy() {
		return readBy;
	}

	public void setReadBy(final List<Participant> readBy) {
		this.readBy = readBy;
	}

	public MessageRequest withReadBy(final List<Participant> readBy) {
		this.readBy = readBy;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final MessageRequest that = (MessageRequest) o;
		return Objects.equals(sequenceNumber, that.sequenceNumber) && Objects.equals(inReplyTo, that.inReplyTo) && Objects.equals(createdBy, that.createdBy) && Objects.equals(content, that.content)
			&& Objects.equals(readBy, that.readBy);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sequenceNumber, inReplyTo, createdBy, content, readBy);
	}

	@Override
	public String toString() {
		return "MessageRequest{" +
			"sequenceNumber='" + sequenceNumber + '\'' +
			", inReplyTo='" + inReplyTo + '\'' +
			", createdBy=" + createdBy +
			", content='" + content + '\'' +
			", readBy=" + readBy +
			'}';
	}
}
