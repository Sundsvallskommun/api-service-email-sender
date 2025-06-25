package se.sundsvall.emailsender.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

class ProblemUtilsTest {

	@Test
	void getProblemCause() {
		var cause = Problem.builder()
			.withDetail("Cause")
			.build();

		var exception = new Exception("Exception", cause);

		var result = ProblemUtils.getProblemCause(exception);

		assertThat(result).isNotNull();
		assertThat(result.getMessage()).isEqualTo("Cause");
	}

	@Test
	void getProblemDetails_shouldReturnProblem_whenCauseCannotBeExtracted() {
		var result = ProblemUtils.getProblemCause(null);
		assertThat(result).isNotNull();
		assertThat(result.getMessage()).isEqualTo("Cause Extraction Failed: Couldn't extract cause from exception");
	}
}
