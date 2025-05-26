package se.sundsvall.messageexchange.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Objects;

@Schema(description = "Represents a person who has read a message, including their identifier and the timestamp of when they read it.")
public class ReadBy {

	@Schema(description = "The identifier of the person who read the message.", example = "ad012ad")
	private Identifier identifier;
	@Schema(description = "The timestamp when the message was read.", example = "2023-01-01T12:00:00")
	private OffsetDateTime readAt;

	public static ReadBy create() {
		return new ReadBy();
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final Identifier identifier) {
		this.identifier = identifier;
	}

	public ReadBy withIdentifier(final Identifier identifier) {
		this.identifier = identifier;
		return this;
	}

	public OffsetDateTime getReadAt() {
		return readAt;
	}

	public void setReadAt(final OffsetDateTime readAt) {
		this.readAt = readAt;
	}

	public ReadBy withReadAt(final OffsetDateTime readAt) {
		this.readAt = readAt;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final ReadBy readBy = (ReadBy) o;
		return Objects.equals(identifier, readBy.identifier) && Objects.equals(readAt, readBy.readAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier, readAt);
	}

	@Override
	public String toString() {
		return "ReadBy{" +
			"identifier=" + identifier +
			", readAt=" + readAt +
			'}';
	}
}
