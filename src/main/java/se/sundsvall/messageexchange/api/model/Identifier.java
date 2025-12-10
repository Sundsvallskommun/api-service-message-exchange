package se.sundsvall.messageexchange.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Objects;

@Schema(description = "Represents a participant in a conversation, including their type and value.")
public class Identifier {

	@Schema(description = "The type of the participant, e.g., adAccount or partyId", examples = "user")
	@Pattern(regexp = "^(adAccount|partyId)$", message = "Type must be 'adAccount' or 'partyId'")
	private String type;

	@NotBlank
	@Schema(description = "The unique identifier or value of the participant.", examples = "ad012ad")
	private String value;

	public static Identifier create() {
		return new Identifier();
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public Identifier withType(final String type) {
		this.type = type;
		return this;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Identifier withValue(final String value) {
		this.value = value;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Identifier that = (Identifier) o;
		return Objects.equals(type, that.type) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, value);
	}

	@Override
	public String toString() {
		return "Identifier{" +
			"type='" + type + '\'' +
			", value='" + value + '\'' +
			'}';
	}
}
