package se.sundsvall.messageexchange.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "message_sequence",
	indexes = {
		@Index(name = "idx_message_sequence_namespace_municipality_id", columnList = "namespace, municipality_id"),
	})
public class MessageSequenceEntity {

	@Id
	@Column(name = "namespace")
	private String namespace;

	@Column(name = "municipality_id")
	private String municipalityId;

	@Column(name = "last_sequence_number")
	private long lastSequenceNumber;

	public static MessageSequenceEntity create() {
		return new MessageSequenceEntity();
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(final String namespace) {
		this.namespace = namespace;
	}

	public MessageSequenceEntity withNamespace(final String namespace) {
		this.namespace = namespace;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public MessageSequenceEntity withMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public long getLastSequenceNumber() {
		return lastSequenceNumber;
	}

	public void setLastSequenceNumber(final long sequence) {
		this.lastSequenceNumber = sequence;
	}

	public MessageSequenceEntity withLastSequenceNumber(final long lastSequenceNumber) {
		this.lastSequenceNumber = lastSequenceNumber;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final MessageSequenceEntity entity = (MessageSequenceEntity) o;
		return lastSequenceNumber == entity.lastSequenceNumber
			&& Objects.equals(namespace, entity.namespace)
			&& Objects.equals(municipalityId, entity.municipalityId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(namespace, municipalityId, lastSequenceNumber);
	}

	@Override
	public String toString() {
		return "MessageSequenceEntity{" +
			"namespace=" + namespace +
			", municipalityId=" + municipalityId +
			", lastSequenceNumber=" + lastSequenceNumber +
			'}';
	}
}
