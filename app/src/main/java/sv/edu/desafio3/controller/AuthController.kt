package sv.edu.desafio3.controller

import android.content.Context
import android.widget.Toast
import sv.edu.desafio3.util.PasswordValidator

class AuthController(private val context: Context) {

    // Registro de usuario
    fun register(email: String, password: String, confirmPassword: String): Boolean {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            showToast("Complete todos los campos")
            return false
        }

        if (password != confirmPassword) {
            showToast("Las contraseñas no coinciden")
            return false
        }

        if (!PasswordValidator.isValid(password)) {
            showToast("La contraseña debe tener:\n- 8 caracteres\n- Mayúscula\n- Minúscula\n- Número\n- Símbolo especial (!@#\$%^&*)")
            return false
        }

        showToast("Registro exitoso (demo)")
        return true
    }

    // Inicio de sesión
    fun login(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            showToast("Complete los campos")
            return false
        }

        if (!PasswordValidator.isValid(password)) {
            showToast("Contraseña no válida")
            return false
        }

        showToast("Inicio de sesión exitoso (demo)")
        return true
    }

    // Cerrar sesión
    fun logout() {
        showToast("Sesión cerrada correctamente")
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

