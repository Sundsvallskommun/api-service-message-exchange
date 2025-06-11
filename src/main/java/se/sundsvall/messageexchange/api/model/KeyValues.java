package se.sundsvall.messageexchange.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "Represents a key-value pair of metadata.")
public class KeyValues {

	@Schema(description = "The key of the data", example = "key1")
	private String key;

	@ArraySchema(schema = @Schema(implementation = String.class, description = "The values associated with the key"))
	private List<String> values;

	public static KeyValues create() {
		return new KeyValues();
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public KeyValues withKey(final String key) {
		this.key = key;
		return this;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(final List<String> values) {
		this.values = values;
	}

	public KeyValues withValues(final List<String> values) {
		this.values = values;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final KeyValues metaData = (KeyValues) o;
		return Objects.equals(key, metaData.key) && Objects.equals(values, metaData.values);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, values);
	}

	@Override
	public String toString() {
		return "KeyValues{" +
			"key='" + key + '\'' +
			", values=" + values +
			'}';
	}
}
