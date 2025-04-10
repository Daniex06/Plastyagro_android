package com.example.plastyagro;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AdministradorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Trabajador> listaTrabajadores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        recyclerView = findViewById(R.id.recyclerViewTrabajadores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaTrabajadores = new ArrayList<>();


       // cargarTrabajadores();

      /*  trabajadorAdapter = new TrabajadorAdapter(listaTrabajadores);
        recyclerView.setAdapter(trabajadorAdapter);
*/

        RecyclerView recyclerViewRegistros = findViewById(R.id.recyclerViewRegistros);
        recyclerViewRegistros.setLayoutManager(new LinearLayoutManager(this));
     //   List<Registro> listaRegistros = databaseHelper.obtenerRegistros();
     //   RegistroAdapter registroAdapter = new RegistroAdapter(listaRegistros);
     //   recyclerViewRegistros.setAdapter(registroAdapter);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_trabajadores) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewRegistros.setVisibility(View.GONE);
                return true;
            } else if (id == R.id.nav_config) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewRegistros.setVisibility(View.VISIBLE);
                return true;
            }

            return false;
        });



    }

    /*private void cargarTrabajadores() {
        try {
            List<Trabajador> trabajadores = databaseHelper.obtenerTrabajadores();
            if (trabajadores != null && !trabajadores.isEmpty()) {
                listaTrabajadores.clear();
                listaTrabajadores.addAll(trabajadores);
                trabajadorAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "No hay trabajadores registrados", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("AdministradorActivity", "Error al cargar los trabajadores: ", e);
            Toast.makeText(this, "Error al cargar los trabajadores", Toast.LENGTH_LONG).show();
        }
    }*/
}

