package cs.sii.security;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 */
public class CsrfSecurityRequestMatcher implements RequestMatcher {
	private Pattern allowedMethods = Pattern.compile("^(GET)$");// (GET|HEAD|TRACE|OPTIONS)

	private static final String HTTP_REGEX = ".*(/cec/|/bot/).*";

	@Override
	public boolean matches(HttpServletRequest request) {
		// Skip allowed methods
		if (allowedMethods.matcher(request.getMethod()).matches()) {
			return false;
		}

		// If the request match one url the CSFR protection will be disabled
		if (request.getRequestURI().matches(HTTP_REGEX)) {
			return false;
		}

		return true;
	} // method matches
}
