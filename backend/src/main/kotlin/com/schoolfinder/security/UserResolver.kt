package com.schoolfinder.security

import com.schoolfinder.domain.AppUser
import com.schoolfinder.domain.Role
import com.schoolfinder.repository.AppUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserResolver(private val users: AppUserRepository) {

    /** Find the user for this Firebase uid, creating a STUDENT record on first sight. */
    @Transactional
    fun resolve(firebaseUid: String, email: String?, displayName: String?): AppUser {
        val existing = users.findByFirebaseUid(firebaseUid)
        if (existing != null) {
            // Keep profile fields fresh from the identity provider.
            if (email != null && existing.email != email) existing.email = email
            if (displayName != null && existing.displayName != displayName) existing.displayName = displayName
            return existing
        }
        return users.save(
            AppUser(
                firebaseUid = firebaseUid,
                email = email,
                displayName = displayName,
                role = Role.STUDENT,
            )
        )
    }

    /** Dev-only: lets the X-Debug-Role header promote a user so admin routes can be tested. */
    @Transactional
    fun applyDevRole(user: AppUser, role: Role?) {
        if (role != null && user.role != role) {
            user.role = role
        }
    }
}
