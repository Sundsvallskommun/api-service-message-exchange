package se.sundsvall.messageexchange.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.sql.Blob;
import java.util.Objects;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "attachment_data")
public class AttachmentDataEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "file", columnDefinition = "longblob")
	@Lob
	private Blob file;

	public static AttachmentDataEntity create() {
		return new AttachmentDataEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public AttachmentDataEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public Blob getFile() {
		return file;
	}

	public void setFile(final Blob file) {
		this.file = file;
	}

	public AttachmentDataEntity withFile(final Blob file) {
		this.file = file;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final AttachmentDataEntity that = (AttachmentDataEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(file, that.file);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, file);
	}

	@Override
	public String toString() {
		return "AttachmentDataEntity{" + "id='" + id + '\''
			+ ", file" + file
			+ '}';
	}
}
