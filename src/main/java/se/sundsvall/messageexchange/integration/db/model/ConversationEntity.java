package se.sundsvall.messageexchange.integration.db.model;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "conversation")
public class ConversationEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "conversation_participants", joinColumns = @JoinColumn(name = "conversation_id", foreignKey = @ForeignKey(name = "fk_participants_conversation_id")))
	private List<IdentifierEntity> participants;

	@Column(name = "municipality_id")
	private String municipalityId;

	@Column(name = "namespace")
	private String namespace;

	@Column(name = "channel_id")
	private String channelId;

	@OneToMany(fetch = EAGER, cascade = ALL, orphanRemoval = true)
	@JoinColumn(name = "conversation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_meta_data_conversation_id"))
	private List<MetadataEntity> metadata;

	@Column(name = "topic")
	private String topic;

	@OneToMany(mappedBy = "conversation", fetch = LAZY)
	private List<MessageEntity> messages;

	public static ConversationEntity create() {
		return new ConversationEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public ConversationEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public List<IdentifierEntity> getParticipants() {
		return participants;
	}

	public void setParticipants(final List<IdentifierEntity> participants) {
		this.participants = participants;
	}

	public ConversationEntity withParticipants(final List<IdentifierEntity> participants) {
		this.participants = participants;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public ConversationEntity withMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(final String namespace) {
		this.namespace = namespace;
	}

	public ConversationEntity withNamespace(final String namespace) {
		this.namespace = namespace;
		return this;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(final String channelId) {
		this.channelId = channelId;
	}

	public ConversationEntity withChannelId(final String channelId) {
		this.channelId = channelId;
		return this;
	}

	public List<MetadataEntity> getMetadata() {
		return metadata;
	}

	public void setMetadata(final List<MetadataEntity> metadata) {
		this.metadata = metadata;
	}

	public ConversationEntity withMetadata(final List<MetadataEntity> metadata) {
		this.metadata = metadata;
		return this;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(final String topic) {
		this.topic = topic;
	}

	public ConversationEntity withTopic(final String topic) {
		this.topic = topic;
		return this;
	}

	public List<MessageEntity> getMessages() {
		return messages;
	}

	public void setMessages(final List<MessageEntity> messages) {
		this.messages = messages;
	}

	public ConversationEntity withMessages(final List<MessageEntity> messages) {
		this.messages = messages;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final ConversationEntity that = (ConversationEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(participants, that.participants) && Objects.equals(municipalityId, that.municipalityId) && Objects.equals(namespace, that.namespace)
			&& Objects.equals(channelId, that.channelId) && Objects.equals(metadata, that.metadata) && Objects.equals(topic, that.topic) && Objects.equals(messages, that.messages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, participants, municipalityId, namespace, channelId, metadata, topic, messages);
	}

	@Override
	public String toString() {
		return "ConversationEntity{" +
			"id='" + id + '\'' +
			", participants=" + participants +
			", municipalityId='" + municipalityId + '\'' +
			", namespace='" + namespace + '\'' +
			", channelId='" + channelId + '\'' +
			", metadata=" + metadata +
			", topic='" + topic + '\'' +
			", messages=" + messages +
			'}';
	}
}
