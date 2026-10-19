package NarayanGroup.example.E_Commerce.security;

import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import NarayanGroup.example.E_Commerce.model.Enum.Role;
import NarayanGroup.example.E_Commerce.service.ISellerService;
import NarayanGroup.example.E_Commerce.service.ILoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@AllArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ILoginService loginService;
    private final ISellerService sellerService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.equals("/api/product")
                || path.equals("/api/user")
                || pathMatcher.match("/api/product/**", path)
                || pathMatcher.match("/api/user/**", path)
                || pathMatcher.match("/login/**", path)
                || pathMatcher.match("/api/address/**", path)
                || pathMatcher.match("/api/checkout/**", path)
                || pathMatcher.match("/api/inventory/**", path)
                || pathMatcher.match("/api/order/**", path)
                || pathMatcher.match("/api/payment/**", path)
                || (pathMatcher.match("/api/category/**", path)
                    && "GET".equalsIgnoreCase(request.getMethod()))
        ) {


            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username;

        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ErrorConstants.TOKEN_INVALID);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = loginService.loadUserByUsername(username);

                if (!jwtUtil.validateToken(token, userDetails)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ErrorConstants.TOKEN_EXPIRED_OR_INVALID);
                    return;
                }

                String activeRole = jwtUtil.getActiveRoleFromToken(token);
                if (activeRole == null || activeRole.isBlank()) {
                    activeRole = userDetails.getAuthorities().stream()
                            .findFirst()
                            .map(authority -> authority.getAuthority())
                            .orElse(Role.USER.name());
                }

                Role role;
                try {
                    role = Role.valueOf(activeRole);
                } catch (IllegalArgumentException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ErrorConstants.TOKEN_INVALID);
                    return;
                }

                if (role == Role.VENDOR) {
                    if (!sellerService.isActiveSeller(
                            getUserIdFromUserDetails(username)
                    )) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, ErrorConstants.VENDOR_ACCESS_NOT_ALLOWED);
                        return;
                    }
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority(role.name()))
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ErrorConstants.USER_NOT_FOUND);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Long getUserIdFromUserDetails(String email) {
        return loginService.getUserId(email);
    }
}
