package se.sundsvall.messageexchange.integration.db;

/**
 * Projection holding the number of messages read by a specific part.
 */
public interface ReadByPartCountProjection {

	String getPart();

	long getCount();
}
