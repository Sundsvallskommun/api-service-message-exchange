package se.sundsvall.chat.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "Represents a conversation containing participants, messages, and metadata.")
public class Conversation {

	@Schema(description = "The unique identifier of the conversation.", example = "1234567890")
	private String id;

	@Schema(description = "The list of participants in the conversation.")
	private List<Participant> participants;

	@Schema(description = "The identifier of the municipality associated with the conversation.", example = "municipality-123")
	private String municipalityId;

	@Schema(description = "The namespace of the conversation.", example = "namespace-abc")
	private String namespace;

	@Schema(description = "A list of channel, reference, and context identifiers associated with the conversation.",
		example = "[\"channel1-ref1-context1\", \"channel2-ref2-context2\"]")
	private String channelId;

	@Schema(description = "A list of metadata associated with the conversation.")
	private List<MetaData> metaData;

	@Schema(description = "The topic of the conversation.", example = "Customer Support")
	private String topic;

	@Schema(description = "The list of messages in the conversation.")
	private List<Message> messages;

	public static Conversation create() {
		return new Conversation();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Conversation withId(final String id) {
		this.id = id;
		return this;
	}

	public List<Participant> getParticipants() {
		return participants;
	}

	public void setParticipants(final List<Participant> participants) {
		this.participants = participants;
	}

	public Conversation withParticipants(final List<Participant> participants) {
		this.participants = participants;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public Conversation withMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(final String namespace) {
		this.namespace = namespace;
	}

	public Conversation withNamespace(final String namespace) {
		this.namespace = namespace;
		return this;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(final String channelId) {
		this.channelId = channelId;
	}

	public Conversation withChannelId(final String channelId) {
		this.channelId = channelId;
		return this;
	}

	public List<MetaData> getMetaData() {
		return metaData;
	}

	public void setMetaData(final List<MetaData> metaData) {
		this.metaData = metaData;
	}

	public Conversation withMetaData(final List<MetaData> metaData) {
		this.metaData = metaData;
		return this;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(final String topic) {
		this.topic = topic;
	}

	public Conversation withTopic(final String topic) {
		this.topic = topic;
		return this;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(final List<Message> messages) {
		this.messages = messages;
	}

	public Conversation withMessages(final List<Message> messages) {
		this.messages = messages;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Conversation that = (Conversation) o;
		return Objects.equals(id, that.id) && Objects.equals(participants, that.participants) && Objects.equals(municipalityId, that.municipalityId) && Objects.equals(namespace, that.namespace)
			&& Objects.equals(channelId, that.channelId) && Objects.equals(metaData, that.metaData) && Objects.equals(topic, that.topic) && Objects.equals(messages, that.messages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, participants, municipalityId, namespace, channelId, metaData, topic, messages);
	}

	@Override
	public String toString() {
		return "Conversation{" +
			"id='" + id + '\'' +
			", participants=" + participants +
			", municipalityId='" + municipalityId + '\'' +
			", namespace='" + namespace + '\'' +
			", channelId='" + channelId + '\'' +
			", metaData=" + metaData +
			", topic='" + topic + '\'' +
			", messages=" + messages +
			'}';
	}
}
