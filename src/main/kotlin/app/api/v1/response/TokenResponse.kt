package app.api.v1.response

class TokenResponse(val token: String) {
    val tokenType: String = "Bearer"
}