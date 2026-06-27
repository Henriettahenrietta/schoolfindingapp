package com.schoolfinder.security;

import com.schoolfinder.api.UnauthorizedException;
import com.schoolfinder.domain.AppUser;
import com.schoolfinder.domain.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Lightweight principal placed in the SecurityContext for each authenticated request. */
public record CurrentUser(Long id, String firebaseUid, Role role) {

    public static CurrentUser of(AppUser user) {
        return new CurrentUser(user.getId(), user.getFirebaseUid(), user.getRole());
    }

    /** Returns the authenticated user, or null for guests. */
    public static CurrentUser current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CurrentUser cu) {
            return cu;
        }
        return null;
    }

    /** Returns the authenticated user or throws 401. */
    public static CurrentUser require() {
        CurrentUser cu = current();
        if (cu == null) throw new UnauthorizedException();
        return cu;
    }
}
