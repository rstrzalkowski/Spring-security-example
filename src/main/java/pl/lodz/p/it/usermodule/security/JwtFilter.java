package pl.lodz.p.it.usermodule.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.lodz.p.it.usermodule.service.CustomUserDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;
    private final List<AntPathRequestMatcher> excludedMatchers;

    public JwtFilter(JwtProvider jwtProvider, CustomUserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.excludedMatchers = new ArrayList<>();

        excludedMatchers.add(new AntPathRequestMatcher("/register"));
        excludedMatchers.add(new AntPathRequestMatcher("/login"));
        excludedMatchers.add(new AntPathRequestMatcher("/guest"));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return excludedMatchers.stream()
                .anyMatch(antPathRequestMatcher -> antPathRequestMatcher.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = getToken(request);

        if (jwt != null && jwtProvider.validateToken(jwt)) {
            Claims claims = jwtProvider.parseJWT(jwt).getBody();
            UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

            if (!userDetails.isEnabled()) {
                throw new RuntimeException("Account is locked");
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    null,
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            throw new RuntimeException();
        }
    }

    private String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }
        return null;
    }
}
