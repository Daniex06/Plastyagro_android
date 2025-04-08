package com.example.plastyagro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class TrabajadorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Trabajador> trabajadores;

    public TrabajadorAdapter(List<Trabajador> trabajadores) {
        this.trabajadores = trabajadores;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View headerView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_trabajador_header, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_trabajador, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            Trabajador trabajador = trabajadores.get(position - 1); // Restamos 1 por la cabecera
            ItemViewHolder itemHolder = (ItemViewHolder) holder;

            itemHolder.columnId.setText(String.valueOf(trabajador.getId()));
            itemHolder.columnNombre.setText(trabajador.getNombre());
            itemHolder.columnEmail.setText(trabajador.getEmail());
            itemHolder.columnPerfil.setText(trabajador.getPerfil());
        }
        // La cabecera no necesita binding ya que tiene textos fijos
    }

    @Override
    public int getItemCount() {
        return trabajadores.size() + 1; // +1 para la cabecera
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    // ViewHolder para la cabecera
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    // ViewHolder para los items
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView columnId, columnNombre, columnEmail, columnPerfil;

        public ItemViewHolder(View itemView) {
            super(itemView);
            columnId = itemView.findViewById(R.id.column_id);
            columnNombre = itemView.findViewById(R.id.column_nombre);
            columnEmail = itemView.findViewById(R.id.column_email);
            columnPerfil = itemView.findViewById(R.id.column_perfil);
        }
    }
}