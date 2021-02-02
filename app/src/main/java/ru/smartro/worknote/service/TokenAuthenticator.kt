package ru.smartro.worknote.service

import android.content.Context
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.smartro.worknote.util.MyUtil

class TokenAuthenticator(val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        MyUtil.logout(context)
        return null
    }
}