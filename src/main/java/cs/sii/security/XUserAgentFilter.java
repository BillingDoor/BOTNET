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
//        String message = 
//            "HTTP header '" + X_USER_AGENT + "' is required. Please set it to your application name so we know " +
//            "who to contact if there's an issue.";
//        this.errorJson = "{ " + wrap("message") + " : " + wrap(message) + " }";
    }

    private String wrap(String s) { return "\"" + s + "\""; }

    @Override
    public void init(FilterConfig config) throws ServletException { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        System.out.println("Header messaggio "+httpRequest.getHeaderNames());
//        if (httpRequest.getHeader(X_USER_AGENT) == null) {
//            httpResponse.setStatus(422);
//            httpResponse.getWriter().println(errorJson);
//        } else {}
            chain.doFilter(request, response);
        
    }

    @Override
    public void destroy() { }
}