package se.sundsvall.chat.api.model;

import java.util.Objects;

public class Participant {
	private String type;
	private String value;

	public static Participant create() {
		return new Participant();
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public Participant withType(final String type) {
		this.type = type;
		return this;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Participant withValue(final String value) {
		this.value = value;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Participant that = (Participant) o;
		return Objects.equals(type, that.type) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, value);
	}

	@Override
	public String toString() {
		return "Participant{" +
			"type='" + type + '\'' +
			", value='" + value + '\'' +
			'}';
	}
}
