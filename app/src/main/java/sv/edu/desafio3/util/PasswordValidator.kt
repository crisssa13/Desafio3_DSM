package sv.edu.desafio3.util

object PasswordValidator {
    private val special = "!@#\$%^&*"

    fun isValid(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isLowerCase() }) return false
        if (!password.any { it.isDigit() }) return false
        if (!password.any { special.contains(it) }) return false
        return true
    }
}
