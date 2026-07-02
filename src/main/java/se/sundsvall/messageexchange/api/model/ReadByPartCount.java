package se.sundsvall.messageexchange.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "The number of messages read by a specific part within a conversation.")
public class ReadByPartCount {

	@Schema(description = "The part the count refers to.", examples = "errand-123")
	private String part;

	@Schema(description = "The number of messages read by the part.", examples = "6")
	private Long count;

	public static ReadByPartCount create() {
		return new ReadByPartCount();
	}

	public String getPart() {
		return part;
	}

	public void setPart(final String part) {
		this.part = part;
	}

	public ReadByPartCount withPart(final String part) {
		this.part = part;
		return this;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(final Long count) {
		this.count = count;
	}

	public ReadByPartCount withCount(final Long count) {
		this.count = count;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ReadByPartCount that = (ReadByPartCount) o;
		return Objects.equals(part, that.part) && Objects.equals(count, that.count);
	}

	@Override
	public int hashCode() {
		return Objects.hash(part, count);
	}

	@Override
	public String toString() {
		return "ReadByPartCount{" +
			"part='" + part + '\'' +
			", count=" + count +
			'}';
	}
}
