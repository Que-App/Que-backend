package app.api.response

class JwtResponse(val token: String) {
    val tokenType: String = "Bearer"
}