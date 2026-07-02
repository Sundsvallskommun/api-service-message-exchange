package se.sundsvall.messageexchange.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Objects;

@Schema(description = "Represents a part that has read a message, including the part identifier and the timestamp of when it was read.")
public class ReadByPart {

	@Schema(description = "The part that read the message.", examples = "errand-123")
	private String part;

	@Schema(description = "The timestamp when the message was read.", examples = "2023-01-01T12:00:00")
	private OffsetDateTime readAt;

	public static ReadByPart create() {
		return new ReadByPart();
	}

	public String getPart() {
		return part;
	}

	public void setPart(final String part) {
		this.part = part;
	}

	public ReadByPart withPart(final String part) {
		this.part = part;
		return this;
	}

	public OffsetDateTime getReadAt() {
		return readAt;
	}

	public void setReadAt(final OffsetDateTime readAt) {
		this.readAt = readAt;
	}

	public ReadByPart withReadAt(final OffsetDateTime readAt) {
		this.readAt = readAt;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ReadByPart that = (ReadByPart) o;
		return Objects.equals(part, that.part) && Objects.equals(readAt, that.readAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(part, readAt);
	}

	@Override
	public String toString() {
		return "ReadByPart{" +
			"part='" + part + '\'' +
			", readAt=" + readAt +
			'}';
	}
}
