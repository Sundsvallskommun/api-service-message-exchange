package se.sundsvall.messageexchange.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.messageexchange.api.validation.ValidMarkAsReadRequest;

@ValidMarkAsReadRequest
@Schema(description = "Request for marking a list of messages as read by an identifier and/or a part.")
public class MarkAsReadRequest {

	@ArraySchema(schema = @Schema(implementation = String.class, description = "The identifiers of the messages to mark as read.", examples = "7f77f9fd-d01d-4742-974a-714b911e3496"))
	@NotEmpty
	private List<@ValidUuid String> messageIds;

	@Valid
	@Schema(description = "The identifier of the person that read the messages. Either this and/or part must be provided.")
	private Identifier identifier;

	@Schema(description = "The part that read the messages. Either this and/or identifier must be provided.", examples = "errand-123")
	private String part;

	public static MarkAsReadRequest create() {
		return new MarkAsReadRequest();
	}

	public List<String> getMessageIds() {
		return messageIds;
	}

	public void setMessageIds(final List<String> messageIds) {
		this.messageIds = messageIds;
	}

	public MarkAsReadRequest withMessageIds(final List<String> messageIds) {
		this.messageIds = messageIds;
		return this;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final Identifier identifier) {
		this.identifier = identifier;
	}

	public MarkAsReadRequest withIdentifier(final Identifier identifier) {
		this.identifier = identifier;
		return this;
	}

	public String getPart() {
		return part;
	}

	public void setPart(final String part) {
		this.part = part;
	}

	public MarkAsReadRequest withPart(final String part) {
		this.part = part;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final MarkAsReadRequest that = (MarkAsReadRequest) o;
		return Objects.equals(messageIds, that.messageIds) && Objects.equals(identifier, that.identifier) && Objects.equals(part, that.part);
	}

	@Override
	public int hashCode() {
		return Objects.hash(messageIds, identifier, part);
	}

	@Override
	public String toString() {
		return "MarkAsReadRequest{" +
			"messageIds=" + messageIds +
			", identifier=" + identifier +
			", part='" + part + '\'' +
			'}';
	}
}
