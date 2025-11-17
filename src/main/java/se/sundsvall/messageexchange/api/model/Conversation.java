package se.sundsvall.messageexchange.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "Represents a conversation containing identifiers, messages, and metadata.")
public class Conversation {

	@Schema(description = "The unique identifier of the conversation.", examples = "2af1002e-008f-4bdc-924b-daaae31f1118", accessMode = READ_ONLY)
	private String id;

	@ArraySchema(schema = @Schema(implementation = Identifier.class, description = "The list of participants in the conversation."))
	private List<Identifier> participants;

	@Schema(description = "The identifier of the municipality associated with the conversation.", examples = "2281", accessMode = READ_ONLY)
	private String municipalityId;

	@Schema(description = "The namespace of the conversation.", examples = "namespace-abc", accessMode = READ_ONLY)
	private String namespace;

	@ArraySchema(schema = @Schema(implementation = KeyValues.class, description = "The list of external references associated with the conversation."))
	private List<KeyValues> externalReferences;

	@ArraySchema(schema = @Schema(implementation = KeyValues.class, description = "A list of metadata associated with the conversation."))
	private List<KeyValues> metadata;

	@Schema(description = "The topic of the conversation.", examples = "Customer Support")
	private String topic;

	@Schema(description = "The latest sequence number of the conversation.", examples = "1", accessMode = READ_ONLY)
	private Long latestSequenceNumber;

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

	public List<KeyValues> getExternalReferences() {
		return externalReferences;
	}

	public void setExternalReferences(final List<KeyValues> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public Conversation withExternalReferences(final List<KeyValues> externalReferences) {
		this.externalReferences = externalReferences;
		return this;
	}

	public List<KeyValues> getMetadata() {
		return metadata;
	}

	public void setMetadata(final List<KeyValues> metadata) {
		this.metadata = metadata;
	}

	public Conversation withMetadata(final List<KeyValues> metadata) {
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

	public Long getLatestSequenceNumber() {
		return latestSequenceNumber;
	}

	public void setLatestSequenceNumber(final Long latestSequenceNumber) {
		this.latestSequenceNumber = latestSequenceNumber;
	}

	public Conversation withLatestSequenceNumber(final Long latestSequenceNumber) {
		this.latestSequenceNumber = latestSequenceNumber;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Conversation that = (Conversation) o;
		return Objects.equals(id, that.id) && Objects.equals(participants, that.participants) && Objects.equals(municipalityId, that.municipalityId) && Objects.equals(namespace, that.namespace)
			&& Objects.equals(externalReferences, that.externalReferences) && Objects.equals(metadata, that.metadata) && Objects.equals(topic, that.topic) && Objects.equals(latestSequenceNumber,
				that.latestSequenceNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, participants, municipalityId, namespace, externalReferences, metadata, topic, latestSequenceNumber);
	}

	@Override
	public String toString() {
		return "Conversation{" +
			"id='" + id + '\'' +
			", participants=" + participants +
			", municipalityId='" + municipalityId + '\'' +
			", namespace='" + namespace + '\'' +
			", externalReferences=" + externalReferences +
			", metadata=" + metadata +
			", topic='" + topic + '\'' +
			", latestSequenceNumber='" + latestSequenceNumber + '\'' +
			'}';
	}
}
