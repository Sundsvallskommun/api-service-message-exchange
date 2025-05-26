package se.sundsvall.messageexchange.integration.db.model;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import org.hibernate.Length;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "message",
	indexes = {
		@Index(name = "idx_message_conversation_id", columnList = "conversation_id"),
		@Index(name = "idx_message_sequence_number", columnList = "sequence_number")
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_message_sequence_number", columnNames = {
			"sequence_number"
		}),
		@UniqueConstraint(name = "uq_message_created_by", columnNames = {
			"created_by"
		})
	})
public class MessageEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@OneToOne(fetch = EAGER, cascade = ALL, orphanRemoval = true)
	@JoinColumn(name = "sequence_number", nullable = false, foreignKey = @ForeignKey(name = "fk_message_sequence_number"))
	private SequenceEntity sequenceNumber;

	@Column(name = "in_reply_to_message_id")
	private String inReplyToMessageId;

	@Column(name = "created")
	private OffsetDateTime created;

	@OneToOne(fetch = EAGER, cascade = ALL, orphanRemoval = true)
	@JoinColumn(name = "created_by", nullable = true, foreignKey = @ForeignKey(name = "fk_message_created_by"))
	private IdentifierEntity createdBy;

	@Column(name = "content", length = Length.LONG32)
	private String content;

	@OneToMany(fetch = EAGER, cascade = ALL, orphanRemoval = true)
	@JoinColumn(name = "message_id", nullable = false, foreignKey = @ForeignKey(name = "fk_message_read_by"))
	private List<ReadByEntity> readBy;

	@ManyToOne(fetch = EAGER)
	@JoinColumn(name = "conversation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_message_conversation_id"))
	private ConversationEntity conversation;

	@OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "messageEntity")
	private List<AttachmentEntity> attachments;

	public static MessageEntity create() {
		return new MessageEntity();
	}

	@PrePersist
	void onCreate() {
		created = now(ZoneId.systemDefault()).truncatedTo(MILLIS);
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public MessageEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public SequenceEntity getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(final SequenceEntity sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public MessageEntity withSequenceNumber(final SequenceEntity sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
		return this;
	}

	public String getInReplyToMessageId() {
		return inReplyToMessageId;
	}

	public void setInReplyToMessageId(final String inReplyToMessageId) {
		this.inReplyToMessageId = inReplyToMessageId;
	}

	public MessageEntity withInReplyToMessageId(final String inReplyTo) {
		this.inReplyToMessageId = inReplyTo;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public MessageEntity withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public IdentifierEntity getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final IdentifierEntity createdBy) {
		this.createdBy = createdBy;
	}

	public MessageEntity withCreatedBy(final IdentifierEntity createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public MessageEntity withContent(final String content) {
		this.content = content;
		return this;
	}

	public List<ReadByEntity> getReadBy() {
		return readBy;
	}

	public void setReadBy(final List<ReadByEntity> readBy) {
		this.readBy = readBy;
	}

	public MessageEntity withReadBy(final List<ReadByEntity> readBy) {
		this.readBy = readBy;
		return this;
	}

	public ConversationEntity getConversation() {
		return conversation;
	}

	public void setConversation(final ConversationEntity conversation) {
		this.conversation = conversation;
	}

	public MessageEntity withConversation(final ConversationEntity conversation) {
		this.conversation = conversation;
		return this;
	}

	public List<AttachmentEntity> getAttachments() {
		return attachments;
	}

	public void setAttachments(final List<AttachmentEntity> attachments) {
		this.attachments = attachments;
	}

	public MessageEntity withAttachments(final List<AttachmentEntity> attachments) {
		this.attachments = attachments;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final MessageEntity that = (MessageEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(sequenceNumber, that.sequenceNumber) && Objects.equals(inReplyToMessageId, that.inReplyToMessageId) && Objects.equals(created, that.created) && Objects.equals(
			createdBy, that.createdBy) && Objects.equals(content, that.content) && Objects.equals(readBy, that.readBy) && Objects.equals(conversation, that.conversation) && Objects.equals(attachments, that.attachments);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, sequenceNumber, inReplyToMessageId, created, createdBy, content, readBy, conversation, attachments);
	}

	@Override
	public String toString() {
		return "MessageEntity{" +
			"id='" + id + '\'' +
			", sequenceNumber='" + sequenceNumber + '\'' +
			", inReplyToMessageId='" + inReplyToMessageId + '\'' +
			", created=" + created +
			", createdBy=" + createdBy +
			", content='" + content + '\'' +
			", readBy=" + readBy +
			", conversation=" + conversation +
			", attachments=" + attachments +
			'}';
	}
}
