package se.sundsvall.chat.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "Represents metadata associated with a conversation, including key-value pairs.")
public class MetaData {
	@Schema(description = "The key of the metadata.", example = "key1")
	private String key;
	@ArraySchema(schema = @Schema(implementation = String.class))
	private List<String> values;

	public static MetaData create() {
		return new MetaData();
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public MetaData withKey(final String key) {
		this.key = key;
		return this;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(final List<String> values) {
		this.values = values;
	}

	public MetaData withValues(final List<String> values) {
		this.values = values;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final MetaData metaData = (MetaData) o;
		return Objects.equals(key, metaData.key) && Objects.equals(values, metaData.values);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, values);
	}

	@Override
	public String toString() {
		return "MetaData{" +
			"key='" + key + '\'' +
			", values=" + values +
			'}';
	}
}
