package app.api.v1.request

class ChangePasswordRequest {
    lateinit var oldPassword: String

    lateinit var newPassword: String
}