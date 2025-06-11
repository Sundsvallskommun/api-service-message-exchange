package se.sundsvall.messageexchange.integration.db.model;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.List;
import java.util.Objects;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "conversation",
	indexes = {
		@Index(name = "idx_conversation_topic", columnList = "topic"),
		@Index(name = "idx_conversation_namespace_municipality_id_id", columnList = "namespace, municipality_id, id")
	})
public class ConversationEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@OneToMany(fetch = EAGER, cascade = ALL, orphanRemoval = true)
	@JoinTable(
		name = "conversation_participants",
		joinColumns = @JoinColumn(name = "conversation_id", foreignKey = @ForeignKey(name = "fk_conversation_participants_conversation_id")),
		inverseJoinColumns = @JoinColumn(name = "identifier_id", foreignKey = @ForeignKey(name = "fk_conversation_participants_identifier_id")),
		uniqueConstraints = @UniqueConstraint(name = "uq_conversation_participants_identifier_id", columnNames = {
			"identifier_id"
		}))
	private List<IdentifierEntity> participants;

	@Column(name = "municipality_id")
	private String municipalityId;

	@Column(name = "namespace")
	private String namespace;

	@OneToMany(fetch = EAGER, cascade = ALL, orphanRemoval = true)
	@JoinColumn(name = "conversation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_external_references_conversation_id"))
	private List<ExternalReferencesEntity> externalReferences;

	@OneToMany(fetch = EAGER, cascade = ALL, orphanRemoval = true)
	@JoinColumn(name = "conversation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_metadata_conversation_id"))
	private List<MetadataEntity> metadata;

	@Column(name = "topic")
	private String topic;

	@OneToMany(mappedBy = "conversation", fetch = LAZY, cascade = ALL, orphanRemoval = true)
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

	public List<ExternalReferencesEntity> getExternalReferences() {
		return externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferencesEntity> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public ConversationEntity withExternalReferences(final List<ExternalReferencesEntity> externalReferences) {
		this.externalReferences = externalReferences;
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
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ConversationEntity that = (ConversationEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(participants, that.participants) && Objects.equals(municipalityId, that.municipalityId) && Objects.equals(namespace, that.namespace)
			&& Objects.equals(externalReferences, that.externalReferences) && Objects.equals(metadata, that.metadata) && Objects.equals(topic, that.topic) && Objects.equals(messages, that.messages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, participants, municipalityId, namespace, externalReferences, metadata, topic, messages);
	}

	@Override
	public String toString() {
		return "ConversationEntity{" +
			"id='" + id + '\'' +
			", participants=" + participants +
			", municipalityId='" + municipalityId + '\'' +
			", namespace='" + namespace + '\'' +
			", externalReferences=" + externalReferences +
			", metadata=" + metadata +
			", topic='" + topic + '\'' +
			", messages=" + messages +
			'}';
	}
}
