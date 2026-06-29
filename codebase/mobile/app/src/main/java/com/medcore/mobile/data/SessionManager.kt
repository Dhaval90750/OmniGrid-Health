package com.medcore.mobile.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    var token: String? = null
    var username: String? = null
    var permissions: Map<String, String> = emptyMap()
    
    fun clear() {
        token = null
        username = null
        permissions = emptyMap()
    }
}
