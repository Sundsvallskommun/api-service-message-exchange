package se.sundsvall.messageexchange.integration.db.model;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "message_read_by",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_message_read_by_identifier_id", columnNames = {
			"identifier_id"
		})
	})
public class ReadByEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@OneToOne(fetch = EAGER, cascade = ALL, orphanRemoval = true)
	@JoinColumn(name = "identifier_id", nullable = false, foreignKey = @ForeignKey(name = "fk_read_by_identifier_id"))
	private IdentifierEntity identifier;

	@Column(name = "read_at", nullable = false)
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime readAt;

	public static ReadByEntity create() {
		return new ReadByEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public ReadByEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public IdentifierEntity getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final IdentifierEntity identifier) {
		this.identifier = identifier;
	}

	public ReadByEntity withIdentifier(final IdentifierEntity identifier) {
		this.identifier = identifier;
		return this;
	}

	public OffsetDateTime getReadAt() {
		return readAt;
	}

	public void setReadAt(final OffsetDateTime readAt) {
		this.readAt = readAt;
	}

	public ReadByEntity withReadAt(final OffsetDateTime readAt) {
		this.readAt = readAt;
		return this;
	}

	@Override
	public boolean equals(final Object o) {

		if (o == null || getClass() != o.getClass())
			return false;
		final ReadByEntity that = (ReadByEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(identifier, that.identifier) && Objects.equals(readAt, that.readAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, identifier, readAt);
	}

	@Override
	public String toString() {
		return "ReadByEntity{" +
			"id='" + id + '\'' +
			", identifier=" + identifier +
			", readAt=" + readAt +
			'}';
	}
}
