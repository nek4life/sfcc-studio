package com.binarysushi.studio.webdav

import okhttp3.*
import java.io.IOException

class StudioServerAuthenticator(private val username: String, private val password: String) : Authenticator {
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization") != null) {
            return null // Give up, we've already attempted to authenticate.
        }
        val credential = Credentials.basic(username, password)
        return response.request.newBuilder().header("Authorization", credential).build()
    }
}
