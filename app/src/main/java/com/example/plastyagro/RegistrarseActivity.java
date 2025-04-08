package com.example.plastyagro;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RegistrarseActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextUsuario, editTextPassword, editTextRepetirPassword, editTextDNI;
    private Spinner spinnerRol;
    private Button buttonRegistrar;
    private String rolSeleccionado;

    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static final String DB_URL = "jdbc:mysql://192.168.0.17:3307/plastyagro";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin";

   private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRepetirPassword = findViewById(R.id.editTextRepetirPassword);
        editTextDNI = findViewById(R.id.editTextDNI);
        spinnerRol = findViewById(R.id.spinnerRol);
        buttonRegistrar = findViewById(R.id.buttonRegistrar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapter);

        spinnerRol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rolSeleccionado = parent.getItemAtPosition(position).toString().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                rolSeleccionado = "trabajador";
            }
        });

        buttonRegistrar.setOnClickListener(view -> {


            String email = editTextEmail.getText().toString().trim();
            String nombre = editTextUsuario.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String repetirPassword = editTextRepetirPassword.getText().toString().trim();
            String dni = editTextDNI.getText().toString().trim();

            registrarUsuarioJDBC(email, nombre, password, dni);
        });

    }


    private void registrarUsuarioJDBC(String email, String nombre, String password, String dni) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            String message = "";


            try {
                // 1. Cargar el driver
                Class.forName("com.mysql.jdbc.Driver");

                // 2. Establecer conexión
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

                // 3. Crear sentencia SQL
                String sql = "INSERT INTO usuario (correo, nombre_usuario, contrasena, perfil, dni) VALUES (?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, nombre);
                preparedStatement.setString(3, password);
                preparedStatement.setString(4, rolSeleccionado);
                preparedStatement.setString(5, dni);

                // 4. Ejecutar inserción
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    success = true;
                    message = "Usuario registrado con éxito";
                } else {
                    message = "No se pudo registrar el usuario";
                }
            } catch (ClassNotFoundException e) {
                message = "Error: Driver no encontrado";
                Log.e("JDBC", message, e);
            } catch (SQLException e) {
                message = "Error de SQL: " + e.getMessage();
                Log.e("JDBC", message, e);
            } catch (Exception e) {
                message = "Error general: " + e.getMessage();
                Log.e("JDBC", message, e);
            } finally {
                // 5. Cerrar recursos
                try {
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    Log.e("JDBC", "Error al cerrar conexión", e);
                }

                // 6. Notificar resultado en UI Thread
                final String finalMessage = message;
                handler.post(() -> {
                    Toast.makeText(RegistrarseActivity.this, finalMessage, Toast.LENGTH_LONG).show();
                    if (success) {
                        finish(); // Cerrar actividad si fue exitoso
                    }
                });
            }
        });
    }

}
