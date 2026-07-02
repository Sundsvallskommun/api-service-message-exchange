package se.sundsvall.messageexchange.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "Read statistics for a conversation, describing how many messages have been read per identifier and per part.")
public class ReadByStatistics {

	@Schema(description = "The total number of messages the statistics are based on.", examples = "13", accessMode = Schema.AccessMode.READ_ONLY)
	private Long messageCount;

	@ArraySchema(schema = @Schema(implementation = ReadByCount.class, description = "The number of messages read per identifier."))
	private List<ReadByCount> readByCount;

	@ArraySchema(schema = @Schema(implementation = ReadByPartCount.class, description = "The number of messages read per part."))
	private List<ReadByPartCount> readByPartCount;

	public static ReadByStatistics create() {
		return new ReadByStatistics();
	}

	public Long getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(final Long messageCount) {
		this.messageCount = messageCount;
	}

	public ReadByStatistics withMessageCount(final Long messageCount) {
		this.messageCount = messageCount;
		return this;
	}

	public List<ReadByCount> getReadByCount() {
		return readByCount;
	}

	public void setReadByCount(final List<ReadByCount> readByCount) {
		this.readByCount = readByCount;
	}

	public ReadByStatistics withReadByCount(final List<ReadByCount> readByCount) {
		this.readByCount = readByCount;
		return this;
	}

	public List<ReadByPartCount> getReadByPartCount() {
		return readByPartCount;
	}

	public void setReadByPartCount(final List<ReadByPartCount> readByPartCount) {
		this.readByPartCount = readByPartCount;
	}

	public ReadByStatistics withReadByPartCount(final List<ReadByPartCount> readByPartCount) {
		this.readByPartCount = readByPartCount;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ReadByStatistics that = (ReadByStatistics) o;
		return Objects.equals(messageCount, that.messageCount) && Objects.equals(readByCount, that.readByCount) && Objects.equals(readByPartCount, that.readByPartCount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(messageCount, readByCount, readByPartCount);
	}

	@Override
	public String toString() {
		return "ReadByStatistics{" +
			"messageCount=" + messageCount +
			", readByCount=" + readByCount +
			", readByPartCount=" + readByPartCount +
			'}';
	}
}
