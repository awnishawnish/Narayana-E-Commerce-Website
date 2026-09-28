package NarayanGroup.example.E_Commerce.security;

import NarayanGroup.example.E_Commerce.service.Impl.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@AllArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final LoginService loginService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // ================= PUBLIC APIs =================

        if (path.equals("/api/product")
                || path.equals("/api/user")
                || pathMatcher.match("/api/product/**", path)
                || pathMatcher.match("/api/user/**", path) || pathMatcher.match("/login/**", path)
                || pathMatcher.match("/api/userId/cart/**", path)
                || pathMatcher.match("/api/address/**", path)
                || pathMatcher.match("/api/checkout/**", path)
                || pathMatcher.match("/api/inventory/**", path)
                || pathMatcher.match("/api/order/**", path)
                || pathMatcher.match("/api/payment/**", path)

        ) {


            filterChain.doFilter(request, response);
            return;
        }

        // ================= AUTHORIZATION HEADER =================

        String authHeader = request.getHeader("Authorization");

        // If token is missing, continue request
        // Spring Security will decide access permissions

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // ================= EXTRACT TOKEN =================

        String token = authHeader.substring(7);

        String username;

        try {

            username = jwtUtil.getUsernameFromToken(token);

        } catch (Exception e) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    ErrorConstants.TOKEN_INVALID
            );

            return;
        }

        // ================= VALIDATE TOKEN =================

        if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {

                UserDetails userDetails =
                        loginService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);

                } else {

                    response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            ErrorConstants.TOKEN_EXPIRED_OR_INVALID
                    );

                    return;
                }

            } catch (Exception e) {

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        ErrorConstants.USER_NOT_FOUND
                );

                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}