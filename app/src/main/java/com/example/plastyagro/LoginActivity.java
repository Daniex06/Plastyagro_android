package com.example.plastyagro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private Button botonLogin;
    private EditText emailEditText;
    private EditText contrasenaEditText;
    private TextView registrarse;
    private DatabaseHelper databaseHelper; // Declaramos la base de datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializamos los elementos de la UI
        botonLogin = findViewById(R.id.button);
        emailEditText = findViewById(R.id.editTextText2);
        contrasenaEditText = findViewById(R.id.editTextText);
        registrarse = findViewById(R.id.textView);

        // Creamos la instancia de DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        botonLogin.setOnClickListener(v -> {

            String email = emailEditText.getText().toString().trim();
            String password = contrasenaEditText.getText().toString().trim();

            // Verificamos que los campos no estén vacíos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "No puedes dejar campos vacíos", Toast.LENGTH_SHORT).show();
            } else {
                // Comprobamos si el usuario existe y si la contraseña es correcta
                if (databaseHelper.verificarUsuario(email)) {
                    String storedPassword = databaseHelper.obtenerContrasena(email);
                    if (storedPassword.equals(password)) {
                        // Si las credenciales son correctas, obtenemos el perfil
                        String perfil = databaseHelper.obtenerPerfil(email);

                        // Redirigimos según el perfil
                        Intent intent;
                        // En LoginActivity, modifica el switch:
                        switch (perfil.toLowerCase()) { // Convertimos a minúsculas para evitar problemas de mayúsculas
                            case "trabajador":
                                intent = new Intent(this, TrabajadorActivity.class);
                                intent.putExtra("email", email);  // 🔹 Asegúrate de enviar el email
                                break;
                            case "gestor":
                                intent = new Intent(this, GestorActivity.class);
                                intent.putExtra("email", email);
                                break;
                            case "administrador":
                                intent = new Intent(this, AdministradorActivity.class);
                                intent.putExtra("email", email);
                                break;
                            default:
                                Toast.makeText(this, "Perfil desconocido: " + perfil, Toast.LENGTH_SHORT).show();
                                return;
                        }

                        startActivity(intent);
                        finish(); // Finalizamos la actividad de login para evitar que el usuario vuelva atrás
                    } else {
                        // Contraseña incorrecta
                        Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Usuario no encontrado
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registrarse.setOnClickListener(v -> {
            // Redirigimos a la pantalla de registro
            startActivity(new Intent(this, RegistrarseActivity.class));
        });

    }

}
