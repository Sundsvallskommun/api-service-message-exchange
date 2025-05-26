package se.sundsvall.messageexchange.integration.db.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "message_sequence")
public class SequenceEntity {

	@Id
	@GeneratedValue(generator = "message_sequence_generator")
	@SequenceGenerator(name = "message_sequence_generator", sequenceName = "message_sequence_id_generator", allocationSize = 1)
	private Long id;

	public static SequenceEntity create() {
		return new SequenceEntity();
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public SequenceEntity withId(final Long id) {
		this.id = id;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final SequenceEntity that = (SequenceEntity) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "SequenceEntity{" +
			"id=" + id +
			'}';
	}
}
