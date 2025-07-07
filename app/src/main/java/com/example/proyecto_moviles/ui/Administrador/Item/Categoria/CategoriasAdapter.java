package com.example.proyecto_moviles.ui.Administrador.Item.Categoria;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_moviles.R;
import java.util.ArrayList;

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasAdapter.CategoriaViewHolder> {
    private ArrayList<String> categorias;
    public CategoriasAdapter(ArrayList<String> categorias) {
        this.categorias = categorias;
    }

    @Override
    public CategoriaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoriaViewHolder holder, int position) {
        String categoria = categorias.get(position);
        holder.bind(categoria);
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public class CategoriaViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCategoria;
        private ImageButton btnEditar, btnCambiarEstado;

        public CategoriaViewHolder(View itemView) {
            super(itemView);
            textViewCategoria = itemView.findViewById(R.id.textViewCategoria);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnCambiarEstado = itemView.findViewById(R.id.btnCambiarEstado);
        }

        public void bind(String categoria) {
            textViewCategoria.setText(categoria);

            btnEditar.setOnClickListener(v -> {
            });

            btnCambiarEstado.setOnClickListener(v -> {
            });
        }
    }
}
