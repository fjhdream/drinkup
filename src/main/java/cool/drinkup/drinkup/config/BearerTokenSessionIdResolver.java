package cool.drinkup.drinkup.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

public class BearerTokenSessionIdResolver implements HttpSessionIdResolver {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final CookieHttpSessionIdResolver cookieResolver;

    public BearerTokenSessionIdResolver(CookieHttpSessionIdResolver cookieResolver) {
        this.cookieResolver = cookieResolver;
    }

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            String token = authorization.substring(BEARER_PREFIX.length()).trim();
            if (!token.isEmpty()) {
                try {
                    String decoded = new String(Base64.getDecoder().decode(token));
                    if (decoded.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
                        return Collections.singletonList(decoded);
                    }
                } catch (Exception e) {
                    // Not Base64, use as-is
                }
                return Collections.singletonList(token);
            }
        }
        return cookieResolver.resolveSessionIds(request);
    }

    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        cookieResolver.setSessionId(request, response, sessionId);
    }

    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        cookieResolver.expireSession(request, response);
    }
}
