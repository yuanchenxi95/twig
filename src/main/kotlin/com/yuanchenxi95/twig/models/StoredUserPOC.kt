package com.yuanchenxi95.twig.models

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class StoredUserPOC constructor (private var oauth2User: OAuth2User): OAuth2User  {
    override fun getName(): String {
        return oauth2User.getAttribute("name")!!
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return oauth2User.attributes
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return oauth2User.authorities;
    }

    fun getEmail(): String {
        return oauth2User.getAttribute("email")!!
    }
}
