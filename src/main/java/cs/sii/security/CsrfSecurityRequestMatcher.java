package cs.sii.security;

import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 */
public class CsrfSecurityRequestMatcher implements RequestMatcher {
    private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

    private static final String HTTP_REGEX =  "^(/bot/**)";
    
    
    ///"^(/bot/**|/hmac)"

    private RegexRequestMatcher matchUnprotected = new RegexRequestMatcher(HTTP_REGEX, null);
    @Override
    public boolean matches(HttpServletRequest request) {
        if(allowedMethods.matcher(request.getMethod()).matches()){
            return false;
        }


        return (!matchUnprotected.matches(request));
    }
}
