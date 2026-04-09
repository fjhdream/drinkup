package cool.drinkup.drinkup.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

/**
 * 组合 Session ID 解析器：优先从 Authorization: Bearer header 读取，
 * 如果没有则 fallback 到 Cookie。
 * 解决移动端 iOS cookie 丢失导致隔天登录失效的问题。
 */
public class BearerTokenSessionIdResolver implements HttpSessionIdResolver {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final CookieHttpSessionIdResolver cookieResolver;

    public BearerTokenSessionIdResolver(CookieHttpSessionIdResolver cookieResolver) {
        this.cookieResolver = cookieResolver;
    }

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        // 优先从 Authorization: Bearer header 读取
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            String sessionId = authorization.substring(BEARER_PREFIX.length()).trim();
            if (!sessionId.isEmpty()) {
                return Collections.singletonList(sessionId);
            }
        }
        // fallback 到 Cookie
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
