package se.sundsvall.messageexchange.util;

import static org.springframework.util.MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;

import java.io.InputStream;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUtil.class);
	private static final String MIME_ERROR_MSG = "Exception when detecting mime type of file with filename '%s'";
	private static final Tika DETECTOR = new Tika();

	private ServiceUtil() {}

	private static String sanitizeFileName(String fileName) {
		// Allow only alphanumeric characters, underscores, hyphens, and dots
		return fileName == null ? "unknown" : fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
	}

	public static String detectMimeTypeFromStream(final String fileName, final InputStream stream) {
		try {
			return DETECTOR.detect(stream, fileName);
		} catch (final Exception e) {
			LOGGER.warn(String.format(MIME_ERROR_MSG, sanitizeFileName(fileName)), e);
			return APPLICATION_OCTET_STREAM_VALUE; // Return mime type for arbitrary binary files
		}
	}

}
