package se.sundsvall.emailsender.api.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.emailsender.api.model.Header.IN_REPLY_TO;
import static se.sundsvall.emailsender.api.model.Header.MESSAGE_ID;
import static se.sundsvall.emailsender.api.model.Header.REFERENCES;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.emailsender.api.model.Header;
import se.sundsvall.emailsender.api.validation.impl.ValidMessageIdConstraintValidator;

@ExtendWith(MockitoExtension.class)
class ValidMessageIdConstraintValidatorTests {

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private ConstraintValidatorContext mockContext;

	@InjectMocks
	private ValidMessageIdConstraintValidator validator;

	private static Stream<Arguments> validArgumentsProvider() {
		return Stream.of(
			Arguments.of(Map.of(MESSAGE_ID, List.of("<valid@sundsvall.se>"))),
			Arguments.of(Map.of(IN_REPLY_TO, List.of("<valid@sundsvall.se>"))),
			Arguments.of(Map.of(REFERENCES, List.of("<valid@sundsvall.se>", "<alsoValid@sundsvall.se>"))));
	}

	private static Stream<Arguments> invalidArgumentsProvider() {
		return Stream.of(
			Arguments.of(Map.of(MESSAGE_ID, List.of("missing@brackets"))),
			Arguments.of(Map.of(IN_REPLY_TO, List.of("<missingthe-snabel-A>"))),
			Arguments.of(Map.of(REFERENCES, List.of("inv<@>alid", "invalid"))));
	}

	@ParameterizedTest
	@MethodSource("validArgumentsProvider")
	void validMessageIds(final Map<Header, List<String>> headers) {
		for (var header : headers.entrySet()) {
			for (var value : header.getValue()) {
				assertThat(validator.isValid(value, mockContext)).isTrue();
			}
		}
	}

	@ParameterizedTest
	@MethodSource("invalidArgumentsProvider")
	void invalidMessageIds(final Map<Header, List<String>> headers) {
		for (var header : headers.entrySet()) {
			for (var value : header.getValue()) {
				assertThat(validator.isValid(value, mockContext)).isFalse();
			}
		}
	}

	@Test
	void isValidTest() {
		String valid = "<Abc@abc.se>";
		String missingBrackets = "abc@abc.se";
		String blank = "";

		assertThat(validator.isValid(valid, mockContext)).isTrue();
		assertThat(validator.isValid(null, mockContext)).isFalse();
		assertThat(validator.isValid(missingBrackets, mockContext)).isFalse();
		assertThat(validator.isValid(blank, mockContext)).isFalse();
	}

}
