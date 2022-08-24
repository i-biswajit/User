package com.willowtreeapps.signinwithapplebutton

data class SignInWithAppleConfiguration(
    val clientId: String,
    val redirectUri: String,
    val scope: String


) {

    class Builder {
        private lateinit var clientId: String
        private lateinit var redirectUri: String
        private lateinit var scope: String
        private lateinit var text: String

        constructor()

        fun clientId(clientId: String) = apply {
            this.clientId = clientId
        }

        fun redirectUri(redirectUri: String) = apply {
            this.redirectUri = redirectUri
        }

        fun scope(scope: String) = apply {
            this.scope = scope
        }



        fun build() = SignInWithAppleConfiguration(clientId, redirectUri, scope)

    }
}
