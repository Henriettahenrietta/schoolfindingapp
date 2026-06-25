package com.schoolfinder.security

import com.schoolfinder.domain.AppUser
import org.springframework.security.core.context.SecurityContextHolder

/** Lightweight principal placed in the SecurityContext for each authenticated request. */
data class CurrentUser(
    val id: Long,
    val firebaseUid: String,
    val role: com.schoolfinder.domain.Role,
) {
    companion object {
        fun of(user: AppUser) = CurrentUser(
            id = user.id!!,
            firebaseUid = user.firebaseUid,
            role = user.role,
        )

        /** Returns the authenticated user, or null for guests. */
        fun current(): CurrentUser? =
            SecurityContextHolder.getContext().authentication?.principal as? CurrentUser

        /** Returns the authenticated user or throws 401. */
        fun require(): CurrentUser =
            current() ?: throw com.schoolfinder.api.UnauthorizedException()
    }
}
