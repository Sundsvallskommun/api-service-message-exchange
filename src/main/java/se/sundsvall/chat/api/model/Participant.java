package se.sundsvall.chat.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Represents a participant in a conversation, including their type and value.")
public class Participant {
	@Schema(description = "The type of the participant, e.g., 'user', 'bot', etc.", example = "user")
	private String type;
	@Schema(description = "The unique identifier or value of the participant.", example = "ad012ad")
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
