package se.sundsvall.messageexchange.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "identifier")
public class IdentifierEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "type")
	private String type;

	@Column(name = "value")
	private String value;

	public static IdentifierEntity create() {
		return new IdentifierEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public IdentifierEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public IdentifierEntity withType(final String type) {
		this.type = type;
		return this;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public IdentifierEntity withValue(final String value) {
		this.value = value;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final IdentifierEntity that = (IdentifierEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(type, that.type) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, type, value);
	}

	@Override
	public String toString() {
		return "IdentifierEntity{" +
			"id='" + id + '\'' +
			", type='" + type + '\'' +
			", value='" + value + '\'' +
			'}';
	}
}
