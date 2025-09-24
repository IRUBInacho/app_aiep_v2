package com.tutorial.app_aiep_v2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var loginBtn: Button
    private lateinit var goToRegisterBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        emailEt = findViewById(R.id.emailEt)
        passwordEt = findViewById(R.id.passwordEt)
        loginBtn = findViewById(R.id.loginBtn)
        goToRegisterBtn = findViewById(R.id.goToRegisterBtn)

        loginBtn.setOnClickListener {
            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        goToRegisterBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this, "Login correcto", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("AUTH", "Error login", e)

                val errorMsg = when {
                    email.isEmpty() || password.isEmpty() ->
                        "Debes ingresar correo y contraseña"
                    e.message?.contains("badly formatted", ignoreCase = true) == true ->
                        "El correo ingresado no es válido"
                    e.message?.contains("no user record", ignoreCase = true) == true ->
                        "El usuario no existe. Regístrate primero"
                    e.message?.contains("password is invalid", ignoreCase = true) == true ->
                        "Contraseña incorrecta"
                    else ->
                        "Error de autenticación. Inténtalo nuevamente"
                }

                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
    }
}