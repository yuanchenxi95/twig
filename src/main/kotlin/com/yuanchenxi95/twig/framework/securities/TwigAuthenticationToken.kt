package com.yuanchenxi95.twig.framework.securities

import com.yuanchenxi95.twig.models.StoredSession
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class TwigAuthenticationToken(
    val storedSession: StoredSession? = null,
    authorities: Collection<out GrantedAuthority> = listOf()
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
}
