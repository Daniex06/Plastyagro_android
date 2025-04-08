package com.example.plastyagro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TrabajadorActivity extends AppCompatActivity {

    private Button buttonAcceso, buttonSalida;
    private DatabaseHelper databaseHelper;
    private String emailUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabajador);

        try {
            Log.d("TrabajadorActivity", "Iniciando actividad...");

            // Vincular botones con el XML
            buttonAcceso = findViewById(R.id.button2);
            buttonSalida = findViewById(R.id.button3);

            if (buttonAcceso == null || buttonSalida == null) {
                Log.e("TrabajadorActivity", "Error: Botones no encontrados en el XML");
                Toast.makeText(this, "Error: Botones no encontrados", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Inicializar la base de datos
            databaseHelper = new DatabaseHelper(this);
            if (databaseHelper == null) {
                Log.e("TrabajadorActivity", "Error: DatabaseHelper es null");
                Toast.makeText(this, "Error al cargar la base de datos", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Obtener el email del usuario desde el Intent
            Intent intent = getIntent();
            emailUsuario = intent.getStringExtra("email");

            if (emailUsuario == null || emailUsuario.isEmpty()) {
                Log.e("TrabajadorActivity", "Error: emailUsuario es null o vacío");
                Toast.makeText(this, "Error: No se obtuvo el usuario", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Log.d("TrabajadorActivity", "Email recibido: " + emailUsuario);

            buttonAcceso.setOnClickListener(v -> registrarEvento("entrada"));
            buttonSalida.setOnClickListener(v -> registrarEvento("salida"));

        } catch (Exception e) {
            Log.e("TrabajadorActivity", "Error en onCreate: ", e);
            Toast.makeText(this, "Error crítico en la actividad", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void registrarEvento(String tipoEvento) {
        try {
            // Obtener el usuarioId a partir del email
            int usuarioId = databaseHelper.obtenerUsuarioId(emailUsuario);

            if (usuarioId == -1) {
                Log.e("TrabajadorActivity", "Usuario no encontrado en la base de datos");
                Toast.makeText(this, "Error: Usuario no encontrado", Toast.LENGTH_LONG).show();
                return;
            }

            Log.d("TrabajadorActivity", "Usuario ID obtenido: " + usuarioId);

            // Obtener fecha y hora actual
            SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String fecha = sdfFecha.format(new Date());
            String hora = sdfHora.format(new Date());

            // Determinar si está dentro del horario laboral
            boolean dentroHorario = estaDentroDeHorario(hora);

            // Registrar en la base de datos
            boolean exito = databaseHelper.registrarRegistro(usuarioId, fecha, hora, dentroHorario, tipoEvento);

            if (exito) {
                String mensaje = tipoEvento.equals("entrada")
                        ? "Entrada registrada: " + fecha + " " + hora
                        : "Salida registrada: " + fecha + " " + hora;
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error al registrar " + tipoEvento, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("TrabajadorActivity", "Error en registrarEvento: ", e);
            Toast.makeText(this, "Error en el registro del evento", Toast.LENGTH_LONG).show();
        }
    }

    private boolean estaDentroDeHorario(String hora) {
        try {
            // Definir el formato de hora
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            // Definir las horas de inicio y fin
            String horaInicio = "08:00:00";
            String horaFin = "18:00:00";

            // Convertir las cadenas de hora a objetos Date
            Date horaActual = sdfHora.parse(hora);
            Date horaInicioDate = sdfHora.parse(horaInicio);
            Date horaFinDate = sdfHora.parse(horaFin);

            // Comparar las horas
            return horaActual.after(horaInicioDate) && horaActual.before(horaFinDate);
        } catch (Exception e) {
            Log.e("TrabajadorActivity", "Error al comparar horas: ", e);
            return false;
        }
    }

}
