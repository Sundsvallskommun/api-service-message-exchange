package se.sundsvall.chat.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "Represents a conversation containing participants, messages, and metadata.")
public class ConversationRequest {

	@Schema(description = "The list of participants in the conversation.")
	private List<Participant> participants;

	@Schema(description = "A list of channel, reference, and context identifiers associated with the conversation.",
		example = "[\"channel1-ref1-context1\", \"channel2-ref2-context2\"]")
	private String channelId;

	@Schema(description = "A list of metadata associated with the conversation.")
	private List<MetaData> metaData;

	@Schema(description = "The topic of the conversation.", example = "Customer Support")
	private String topic;

	@Schema(description = "The list of messages in the conversation.")
	private List<Message> messages;

	public static ConversationRequest create() {
		return new ConversationRequest();
	}

	public List<Participant> getParticipants() {
		return participants;
	}

	public void setParticipants(final List<Participant> participants) {
		this.participants = participants;
	}

	public ConversationRequest withParticipants(final List<Participant> participants) {
		this.participants = participants;
		return this;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(final String channelId) {
		this.channelId = channelId;
	}

	public ConversationRequest withChannelId(final String channelId) {
		this.channelId = channelId;
		return this;
	}

	public List<MetaData> getMetaData() {
		return metaData;
	}

	public void setMetaData(final List<MetaData> metaData) {
		this.metaData = metaData;
	}

	public ConversationRequest withMetaData(final List<MetaData> metaData) {
		this.metaData = metaData;
		return this;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(final String topic) {
		this.topic = topic;
	}

	public ConversationRequest withTopic(final String topic) {
		this.topic = topic;
		return this;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(final List<Message> messages) {
		this.messages = messages;
	}

	public ConversationRequest withMessages(final List<Message> messages) {
		this.messages = messages;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final ConversationRequest that = (ConversationRequest) o;
		return Objects.equals(participants, that.participants) && Objects.equals(channelId, that.channelId) && Objects.equals(metaData, that.metaData) && Objects.equals(topic, that.topic) && Objects.equals(
			messages, that.messages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(participants, channelId, metaData, topic, messages);
	}

	@Override
	public String toString() {
		return "ConversationRequest{" +
			"participants=" + participants +
			", channelId='" + channelId + '\'' +
			", metaData=" + metaData +
			", topic='" + topic + '\'' +
			", messages=" + messages +
			'}';
	}
}
