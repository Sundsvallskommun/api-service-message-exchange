package se.sundsvall.messageexchange.api.validation.impl;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.messageexchange.api.validation.ValidMultipartFiles;

public class ValidMultipartFilesValidator implements ConstraintValidator<ValidMultipartFiles, List<MultipartFile>> {

	@Override
	public boolean isValid(List<MultipartFile> multipartFiles, ConstraintValidatorContext context) {
		if (isNull(multipartFiles) || multipartFiles.isEmpty()) {
			return true;
		}

		return multipartFiles.stream()
			.allMatch(file -> nonNull(file) && !file.isEmpty());
	}
}
