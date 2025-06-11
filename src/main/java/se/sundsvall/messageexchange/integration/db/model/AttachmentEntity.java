package se.sundsvall.messageexchange.integration.db.model;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "attachment")
public class AttachmentEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "mime_type")
	private String mimeType;

	@Column(name = "file_size")
	private int fileSize;

	@ManyToOne(fetch = LAZY, cascade = ALL)
	@JoinColumn(name = "attachment_data_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attachment_data_attachment"))
	private AttachmentDataEntity attachmentData;

	@Column(name = "created")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "message_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attachment_message"))
	private MessageEntity messageEntity;

	public static AttachmentEntity create() {
		return new AttachmentEntity();
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

	public AttachmentEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public AttachmentEntity withFileName(final String fileName) {
		this.fileName = fileName;
		return this;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(final String mimeType) {
		this.mimeType = mimeType;
	}

	public AttachmentEntity withMimeType(final String mimeType) {
		this.mimeType = mimeType;
		return this;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(final int fileSize) {
		this.fileSize = fileSize;
	}

	public AttachmentEntity withFileSize(final int fileSize) {
		this.fileSize = fileSize;
		return this;
	}

	public AttachmentDataEntity getAttachmentData() {
		return attachmentData;
	}

	public void setAttachmentData(final AttachmentDataEntity attachmentData) {
		this.attachmentData = attachmentData;
	}

	public AttachmentEntity withAttachmentData(final AttachmentDataEntity attachmentData) {
		this.attachmentData = attachmentData;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public AttachmentEntity withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public MessageEntity getMessageEntity() {
		return messageEntity;
	}

	public void setMessageEntity(final MessageEntity messageEntity) {
		this.messageEntity = messageEntity;
	}

	public AttachmentEntity withMessageEntity(final MessageEntity messageEntity) {
		this.messageEntity = messageEntity;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final AttachmentEntity that = (AttachmentEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(fileName, that.fileName) && Objects.equals(mimeType, that.mimeType) && Objects.equals(fileSize, that.fileSize) && Objects.equals(attachmentData,
			that.attachmentData) && Objects.equals(created, that.created) && Objects.equals(messageEntity, that.messageEntity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, fileName, mimeType, fileSize, attachmentData, created, messageEntity);
	}

	@Override
	public String toString() {
		return "AttachmentEntity{" +
			"id='" + id + '\'' +
			", fileName='" + fileName + '\'' +
			", mimeType='" + mimeType + '\'' +
			", fileSize=" + fileSize +
			", attachmentData=" + attachmentData +
			", created=" + created +
			", messageEntity=" + messageEntity +
			'}';
	}
}
