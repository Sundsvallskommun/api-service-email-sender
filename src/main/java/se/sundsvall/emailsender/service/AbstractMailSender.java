package se.sundsvall.emailsender.service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import se.sundsvall.dept44.common.validators.annotation.impl.ValidBase64ConstraintValidator;

import static java.nio.charset.StandardCharsets.UTF_8;

abstract class AbstractMailSender implements MailSender {

	protected static final ValidBase64ConstraintValidator BASE64_VALIDATOR = new ValidBase64ConstraintValidator();

	private String municipalityId;

	@Override
	public String getMunicipalityId() {
		return municipalityId;
	}

	@Override
	public void setMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
	}

	Optional<String> decodeBase64(final String s) {
		try {
			return Optional.of(new String(Base64.getDecoder().decode(s), UTF_8));
		} catch (final IllegalArgumentException e) {
			return Optional.empty();
		}
	}

	String formatHeader(final List<String> values) {
		return values.stream()
			.reduce((a, b) -> a + " " + b)
			.orElse("");
	}
}
