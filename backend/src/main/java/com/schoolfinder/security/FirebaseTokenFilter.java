package com.schoolfinder.security;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.schoolfinder.config.AppProperties;
import com.schoolfinder.domain.AppUser;
import com.schoolfinder.domain.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authenticates each request.
 *
 * <ul>
 *   <li><b>Firebase mode</b> ({@code app.firebase.enabled=true}): verifies the
 *       {@code Authorization: Bearer <idToken>} header with the Firebase Admin SDK.</li>
 *   <li><b>Dev mode</b> (default): trusts an {@code X-Debug-Uid} header so the stack is usable
 *       without any Firebase project. {@code X-Debug-Email}, {@code X-Debug-Name} and
 *       {@code X-Debug-Role} are optional helpers.</li>
 * </ul>
 *
 * Requests with no credentials proceed unauthenticated (guest); endpoint rules decide access.
 */
@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(FirebaseTokenFilter.class);

    private final AppProperties props;
    private final UserResolver userResolver;
    private final FirebaseApp firebaseApp;

    public FirebaseTokenFilter(
        AppProperties props,
        UserResolver userResolver,
        @Nullable FirebaseApp firebaseApp
    ) {
        this.props = props;
        this.userResolver = userResolver;
        this.firebaseApp = firebaseApp;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (props.getFirebase().isEnabled()) {
                    authenticateFirebase(request);
                } else {
                    authenticateDev(request);
                }
            } catch (Exception ex) {
                log.debug("Authentication skipped: {}", ex.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticateFirebase(HttpServletRequest request) throws Exception {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return;
        String token = header.substring("Bearer ".length()).trim();
        if (firebaseApp == null) return;
        FirebaseToken decoded = FirebaseAuth.getInstance(firebaseApp).verifyIdToken(token);
        AppUser user = userResolver.resolve(decoded.getUid(), decoded.getEmail(), decoded.getName());
        authenticate(CurrentUser.of(user));
    }

    private void authenticateDev(HttpServletRequest request) {
        String uid = request.getHeader("X-Debug-Uid");
        if (uid == null) return;
        Role devRole = parseRole(request.getHeader("X-Debug-Role"));
        AppUser user = userResolver.resolve(
            uid,
            request.getHeader("X-Debug-Email"),
            request.getHeader("X-Debug-Name")
        );
        userResolver.applyDevRole(user, devRole);
        authenticate(CurrentUser.of(user));
    }

    @Nullable
    private Role parseRole(@Nullable String raw) {
        if (raw == null) return null;
        try {
            return Role.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private void authenticate(CurrentUser principal) {
        List<SimpleGrantedAuthority> authorities =
            List.of(new SimpleGrantedAuthority("ROLE_" + principal.role().name()));
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
