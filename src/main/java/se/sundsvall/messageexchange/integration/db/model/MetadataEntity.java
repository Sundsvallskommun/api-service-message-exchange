package se.sundsvall.messageexchange.integration.db.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "conversation_metadata",
	indexes = {
		@Index(name = "idx_conversation_metadata_id", columnList = "conversation_id, `key`")
	})
public class MetadataEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "\"key\"")
	private String key;

	@ElementCollection
	@CollectionTable(
		name = "conversation_metadata_values",
		joinColumns = @JoinColumn(name = "conversation_metadata_id"),
		foreignKey = @ForeignKey(name = "fk_metadata_values"))
	@Column(name = "\"values\"")
	private List<String> values;

	public static MetadataEntity create() {
		return new MetadataEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public MetadataEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public MetadataEntity withKey(final String key) {
		this.key = key;
		return this;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(final List<String> values) {
		this.values = values;
	}

	public MetadataEntity withValues(final List<String> values) {
		this.values = values;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final MetadataEntity that = (MetadataEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(key, that.key) && Objects.equals(values, that.values);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, key, values);
	}

	@Override
	public String toString() {
		return "MetadataEntity{" +
			"id='" + id + '\'' +
			", key='" + key + '\'' +
			", values=" + values +
			'}';
	}
}
