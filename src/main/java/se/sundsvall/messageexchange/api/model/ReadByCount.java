package se.sundsvall.messageexchange.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "The number of messages read by a specific identifier within a conversation.")
public class ReadByCount {

	@Schema(description = "The identifier of the person the count refers to.")
	private Identifier identifier;

	@Schema(description = "The number of messages read by the identifier.", examples = "4")
	private Long count;

	public static ReadByCount create() {
		return new ReadByCount();
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final Identifier identifier) {
		this.identifier = identifier;
	}

	public ReadByCount withIdentifier(final Identifier identifier) {
		this.identifier = identifier;
		return this;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(final Long count) {
		this.count = count;
	}

	public ReadByCount withCount(final Long count) {
		this.count = count;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ReadByCount that = (ReadByCount) o;
		return Objects.equals(identifier, that.identifier) && Objects.equals(count, that.count);
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier, count);
	}

	@Override
	public String toString() {
		return "ReadByCount{" +
			"identifier=" + identifier +
			", count=" + count +
			'}';
	}
}
