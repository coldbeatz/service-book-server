package servicebook.services.jwt;

import jakarta.servlet.*;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import org.thymeleaf.util.StringUtils;

import servicebook.user.User;
import servicebook.user.UserService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter implements Filter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_NAME = "Authorization";

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, @Lazy UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public void doFilter(@NonNull ServletRequest request,
                         @NonNull ServletResponse response,
                         @NonNull FilterChain filterChain) throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String authorizationHeader = httpRequest.getHeader(HEADER_NAME);

        if (StringUtils.isEmpty(authorizationHeader) || !StringUtils.startsWith(authorizationHeader, BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        var jwt = authorizationHeader.substring(BEARER_PREFIX.length());
        var email = jwtService.extractUserEmail(jwt);

        if (!StringUtils.isEmpty(email) && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userService.findUserByEmail(email).orElse(null);

            // Якщо токен валідний, то аутентифікуємо користувача
            if (user != null && jwtService.isTokenValid(jwt, user)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, ((UserDetails) user).getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));

                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }

        filterChain.doFilter(request, response);
    }
}
