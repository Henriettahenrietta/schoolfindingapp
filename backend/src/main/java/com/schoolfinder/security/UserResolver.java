package com.schoolfinder.security;

import com.schoolfinder.domain.AppUser;
import com.schoolfinder.domain.Role;
import com.schoolfinder.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Maps a verified Firebase token to a persisted {@link AppUser}, creating one on first sign-in. */
@Service
public class UserResolver {

    private final AppUserRepository users;

    public UserResolver(AppUserRepository users) {
        this.users = users;
    }

    @Transactional
    public AppUser resolve(String firebaseUid, String email, String displayName) {
        AppUser existing = users.findByFirebaseUid(firebaseUid);
        if (existing != null) {
            boolean changed = false;
            if (email != null && !email.equals(existing.getEmail())) {
                existing.setEmail(email);
                changed = true;
            }
            if (displayName != null && !displayName.equals(existing.getDisplayName())) {
                existing.setDisplayName(displayName);
                changed = true;
            }
            return changed ? users.save(existing) : existing;
        }
        AppUser created = new AppUser(firebaseUid, email, displayName, Role.STUDENT);
        return users.save(created);
    }

    /** Dev-only: lets the X-Debug-Role header promote a user so admin routes can be tested. */
    @Transactional
    public void applyDevRole(AppUser user, Role role) {
        if (role != null && user.getRole() != role) {
            user.setRole(role);
        }
    }
}
