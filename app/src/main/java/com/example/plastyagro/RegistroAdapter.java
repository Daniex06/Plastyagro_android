package com.example.plastyagro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> {

    private List<Registro> registros;

    public RegistroAdapter(List<Registro> registros) {
        this.registros = registros;
    }

    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro, parent, false);
        return new RegistroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroViewHolder holder, int position) {
        Registro registro = registros.get(position);
        holder.textViewFecha.setText("Fecha: " + registro.getFecha());
        holder.textViewHora.setText("Hora: " + registro.getHora());
        holder.textViewTipo.setText("Tipo: " + registro.getTipoRegistro());
        holder.textViewDentroHorario.setText(registro.isDentroHorario() ? "Dentro del horario" : "Fuera del horario");
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    static class RegistroViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFecha, textViewHora, textViewTipo, textViewDentroHorario;

        public RegistroViewHolder(@NonNull View itemView) {
            super(itemView);
            // Asegúrate de que los IDs coinciden con los del XML
            textViewFecha = itemView.findViewById(R.id.column_fecha);
            textViewHora = itemView.findViewById(R.id.column_hora);
            textViewTipo = itemView.findViewById(R.id.column_tipo_registro);
            textViewDentroHorario = itemView.findViewById(R.id.column_dentro_horario);
        }
    }
}
