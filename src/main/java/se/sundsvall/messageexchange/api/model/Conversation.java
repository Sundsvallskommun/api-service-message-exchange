package se.sundsvall.messageexchange.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "Represents a conversation containing identifiers, messages, and metadata.")
public class Conversation {

	@Schema(description = "The unique identifier of the conversation.", example = "1234567890", accessMode = READ_ONLY)
	private String id;

	@ArraySchema(schema = @Schema(implementation = Identifier.class, description = "The list of participants in the conversation."))
	private List<Identifier> participants;

	@Schema(description = "The identifier of the municipality associated with the conversation.", example = "municipality-123", accessMode = READ_ONLY)
	private String municipalityId;

	@Schema(description = "The namespace of the conversation.", example = "namespace-abc", accessMode = READ_ONLY)
	private String namespace;

	@Schema(description = "The ID of the channel used for the conversation.", example = "12345")
	private String channelId;

	@ArraySchema(schema = @Schema(implementation = Metadata.class, description = "A list of metadata associated with the conversation."))
	private List<Metadata> metadata;

	@Schema(description = "The topic of the conversation.", example = "Customer Support")
	private String topic;

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

	public List<Identifier> getParticipants() {
		return participants;
	}

	public void setParticipants(final List<Identifier> participants) {
		this.participants = participants;
	}

	public Conversation withParticipants(final List<Identifier> participants) {
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

	public List<Metadata> getMetadata() {
		return metadata;
	}

	public void setMetadata(final List<Metadata> metadata) {
		this.metadata = metadata;
	}

	public Conversation withMetadata(final List<Metadata> metadata) {
		this.metadata = metadata;
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

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Conversation that = (Conversation) o;
		return Objects.equals(id, that.id) && Objects.equals(participants, that.participants) && Objects.equals(municipalityId, that.municipalityId) && Objects.equals(namespace, that.namespace)
			&& Objects.equals(channelId, that.channelId) && Objects.equals(metadata, that.metadata) && Objects.equals(topic, that.topic);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, participants, municipalityId, namespace, channelId, metadata, topic);
	}

	@Override
	public String toString() {
		return "Conversation{" +
			"id='" + id + '\'' +
			", participants=" + participants +
			", municipalityId='" + municipalityId + '\'' +
			", namespace='" + namespace + '\'' +
			", channelId='" + channelId + '\'' +
			", metadata=" + metadata +
			", topic='" + topic + '\'' +
			'}';
	}
}
