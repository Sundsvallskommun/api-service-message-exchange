package se.sundsvall.messageexchange.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.UuidGenerator;

import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

@Entity
@Table(name = "message_read_by_part",
	indexes = {
		@Index(name = "idx_message_read_by_part_message_id_part", columnList = "message_id, part")
	})
public class ReadByPartEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "part", nullable = false)
	private String part;

	@Column(name = "read_at", nullable = false)
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime readAt;

	public static ReadByPartEntity create() {
		return new ReadByPartEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public ReadByPartEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public String getPart() {
		return part;
	}

	public void setPart(final String part) {
		this.part = part;
	}

	public ReadByPartEntity withPart(final String part) {
		this.part = part;
		return this;
	}

	public OffsetDateTime getReadAt() {
		return readAt;
	}

	public void setReadAt(final OffsetDateTime readAt) {
		this.readAt = readAt;
	}

	public ReadByPartEntity withReadAt(final OffsetDateTime readAt) {
		this.readAt = readAt;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ReadByPartEntity that = (ReadByPartEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(part, that.part) && Objects.equals(readAt, that.readAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, part, readAt);
	}

	@Override
	public String toString() {
		return "ReadByPartEntity{" +
			"id='" + id + '\'' +
			", part='" + part + '\'' +
			", readAt=" + readAt +
			'}';
	}
}
