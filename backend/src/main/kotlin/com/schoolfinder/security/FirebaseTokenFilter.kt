package com.schoolfinder.security

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.schoolfinder.config.AppProperties
import com.schoolfinder.domain.Role
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Authenticates each request.
 *
 *  - **Firebase mode** (`app.firebase.enabled=true`): verifies the `Authorization: Bearer <idToken>`
 *    header with the Firebase Admin SDK.
 *  - **Dev mode** (default): trusts an `X-Debug-Uid` header so the stack is usable without any
 *    Firebase project. `X-Debug-Email`, `X-Debug-Name` and `X-Debug-Role` are optional helpers.
 *
 * Requests with no credentials proceed unauthenticated (guest); endpoint rules decide access.
 */
@Component
class FirebaseTokenFilter(
    private val props: AppProperties,
    private val userResolver: UserResolver,
    private val firebaseApp: FirebaseApp?,
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (SecurityContextHolder.getContext().authentication == null) {
            try {
                if (props.firebase.enabled) authenticateFirebase(request) else authenticateDev(request)
            } catch (ex: Exception) {
                log.debug("Authentication skipped: {}", ex.message)
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun authenticateFirebase(request: HttpServletRequest) {
        val header = request.getHeader("Authorization") ?: return
        if (!header.startsWith("Bearer ")) return
        val token = header.removePrefix("Bearer ").trim()
        val app = firebaseApp ?: return
        val decoded = FirebaseAuth.getInstance(app).verifyIdToken(token)
        val user = userResolver.resolve(decoded.uid, decoded.email, decoded.name)
        authenticate(CurrentUser.of(user))
    }

    private fun authenticateDev(request: HttpServletRequest) {
        val uid = request.getHeader("X-Debug-Uid") ?: return
        val devRole = request.getHeader("X-Debug-Role")?.let { runCatching { Role.valueOf(it.uppercase()) }.getOrNull() }
        val user = userResolver.resolve(
            firebaseUid = uid,
            email = request.getHeader("X-Debug-Email"),
            displayName = request.getHeader("X-Debug-Name"),
        )
        userResolver.applyDevRole(user, devRole)
        authenticate(CurrentUser.of(user))
    }

    private fun authenticate(principal: CurrentUser) {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_${principal.role.name}"))
        val auth = UsernamePasswordAuthenticationToken(principal, null, authorities)
        SecurityContextHolder.getContext().authentication = auth
    }
}
