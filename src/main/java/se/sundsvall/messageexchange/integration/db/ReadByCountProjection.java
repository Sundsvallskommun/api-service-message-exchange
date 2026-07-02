package se.sundsvall.messageexchange.integration.db;

/**
 * Projection holding the number of messages read by a specific identifier (type + value).
 */
public interface ReadByCountProjection {

	String getType();

	String getValue();

	long getCount();
}
