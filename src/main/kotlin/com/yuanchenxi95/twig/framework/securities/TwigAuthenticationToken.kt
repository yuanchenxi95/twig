package com.yuanchenxi95.twig.framework.securities

import com.yuanchenxi95.twig.models.StoredSession
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import java.time.Instant

class TwigAuthenticationToken(
    private val storedSession: StoredSession? = null,
    authorities: Collection<GrantedAuthority> = listOf()
) : AbstractAuthenticationToken(authorities) {

    init {
        super.setAuthenticated(storedSession != null)
    }

    override fun getCredentials(): Any {
        throw NotImplementedError("getCredentials() not implemented.")
    }

    override fun getPrincipal(): Any {
        throw NotImplementedError("getPrincipal() not implemented.")
    }

    fun getSessionId(): String {
        return this.storedSession!!.id
    }

    fun getUserId(): String {
        return this.storedSession!!.userId
    }

    fun getExpirationTime(): Instant {
        return this.storedSession!!.expirationTime
    }
}
