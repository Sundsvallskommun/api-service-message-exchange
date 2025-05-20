package se.sundsvall.chat.api.model;

import java.util.List;
import java.util.Objects;

public class MetaData {
	private String key;
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
