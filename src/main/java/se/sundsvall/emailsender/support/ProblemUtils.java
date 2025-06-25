package se.sundsvall.emailsender.support;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import java.util.Optional;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

public final class ProblemUtils {

	private ProblemUtils() {}

	/**
	 * Extracts a ThrowableProblem from an exception.
	 *
	 * @param  exception the exception to extract the cause from
	 * @return           the ThrowableProblem cause, or a fallback problem if not found.
	 */
	public static ThrowableProblem getProblemCause(final Exception exception) {
		if (exception == null) {
			return createFallbackProblem("Couldn't extract cause from exception");
		}

		return findThrowableProblem(exception)
			.orElseGet(() -> createFallbackProblem("No ThrowableProblem found in cause chain"));

	}

	private static Optional<ThrowableProblem> findThrowableProblem(final Exception exception) {
		Throwable cause = exception.getCause();
		if (cause instanceof ThrowableProblem throwableProblem) {
			return Optional.of(throwableProblem);
		}

		return Optional.empty();
	}

	private static ThrowableProblem createFallbackProblem(final String reason) {
		return Problem.builder()
			.withTitle("Cause Extraction Failed")
			.withDetail(reason)
			.withStatus(INTERNAL_SERVER_ERROR)
			.build();
	}
}
