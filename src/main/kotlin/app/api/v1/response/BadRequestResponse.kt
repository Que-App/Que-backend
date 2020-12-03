package app.api.v1.response

class BadRequestResponse(val cause: String, val action: String = DEFAULT_ACTION_MESSAGE) {

    companion object {
        private const val DEFAULT_ACTION_MESSAGE = "Please contact the administrator."
    }

    class Builder {
        private var _cause: String? = null

        private var _action: String = DEFAULT_ACTION_MESSAGE

        fun cause(cause: String): Builder  { _cause = cause; return this }

        fun action(action: String): Builder { _action = action; return this }

        fun build() = BadRequestResponse(_cause!!, _action)
    }
}