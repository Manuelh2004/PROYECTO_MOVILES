package com.example.proyecto_moviles.ui.Administrador.Comentario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.Comentario;

import java.util.List;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder>{
    private List<Comentario> comentarios;

    // Constructor que recibe la lista de comentarios
    public ComentarioAdapter(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el diseño para cada item de la lista
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        // Obtén el comentario en la posición correspondiente
        Comentario comentario = comentarios.get(position);

        // Asocia los datos con las vistas del item
        holder.menComentario.setText(comentario.getMen_comentario());
        holder.freComentario.setText(comentario.getFre_comentario());
        holder.estComentario.setText(comentario.getEst_comentario());
    }

    @Override
    public int getItemCount() {
        return comentarios.size();  // Devuelve el tamaño de la lista de comentarios
    }

    // ViewHolder que contiene las vistas de cada item
    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        TextView menComentario, freComentario, estComentario;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            menComentario = itemView.findViewById(R.id.men_comentario);
            freComentario = itemView.findViewById(R.id.fre_comentario);
            estComentario = itemView.findViewById(R.id.est_comentario);
        }
    }
}
