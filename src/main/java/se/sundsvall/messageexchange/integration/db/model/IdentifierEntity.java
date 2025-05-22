package se.sundsvall.messageexchange.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class IdentifierEntity {

	@Column(name = "type")
	private String type;

	@Column(name = "value")
	private String value;

	public static IdentifierEntity create() {
		return new IdentifierEntity();
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
		return Objects.equals(type, that.type) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, value);
	}

	@Override
	public String toString() {
		return "IdentifierEntity{" +
			"type='" + type + '\'' +
			", value='" + value + '\'' +
			'}';
	}
}
