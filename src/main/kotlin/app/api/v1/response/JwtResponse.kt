package app.api.v1.response

class JwtResponse(val token: String) {
    val tokenType: String = "Bearer"
}