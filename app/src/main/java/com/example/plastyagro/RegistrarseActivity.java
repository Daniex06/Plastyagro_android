package com.example.plastyagro;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;


public class RegistrarseActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextUsuario, editTextPassword, editTextRepetirPassword, editTextDNI;
    private Spinner spinnerRol;
    private Button buttonRegistrar;
    private String rolSeleccionado;
    FirebaseFirestore firestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        setContentView(R.layout.activity_registrarse);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRepetirPassword = findViewById(R.id.editTextRepetirPassword);
        editTextDNI = findViewById(R.id.editTextDNI);
        spinnerRol = findViewById(R.id.spinnerRol);
        buttonRegistrar = findViewById(R.id.buttonRegistrar);

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
            String nombre = editTextUsuario.getText().toString();
            String password = editTextPassword.getText().toString().trim();
            String repetirPassword = editTextRepetirPassword.getText().toString().trim();
            String dni = editTextDNI.getText().toString().trim();

            if (email.isEmpty() || nombre.isEmpty() || password.isEmpty() || repetirPassword.isEmpty() || dni.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(repetirPassword)) {
                Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            } else {
                registrarUsuarioJDBC(email, nombre, dni);
                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrarUsuarioJDBC(String email, String nombre, String dni) {


        firestore = FirebaseFirestore.getInstance();
       // DocumentReference nuevoUsuarioRef = firestore.collection("usuario").document();
        //String idGenerado = nuevoUsuarioRef.getId();
        Map<String, Object> mapa = new HashMap<>();
           mapa.put("id","1");
        mapa.put("nombre_usuario",nombre);
        mapa.put("email",email);
        mapa.put("dni",dni);
        mapa.put("perfil", "administrador");
        firestore.collection("usuario").document().set(mapa).
        addOnSuccessListener(aVoid -> Log.d("Firestore","Usuario agregado con éxito")).
         addOnFailureListener(e -> Log.w("Firestore", "Error al agregar usuario"));

    }

}

