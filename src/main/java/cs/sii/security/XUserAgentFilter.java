package cs.sii.security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;

public class XUserAgentFilter implements Filter {
	private static final String X_USER_AGENT = "X-User-Agent";

	private String errorJson;

	public XUserAgentFilter() {
	}

	private String wrap(String s) {
		return "\"" + s + "\"";
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		chain.doFilter(request, response);

	}

	@Override
	public void destroy() {
	}
}