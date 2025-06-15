package com.example.proyecto_moviles.ui.Administrador.Usuario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.Usuario;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>
{
    private List<Usuario> usuarios;

    // Constructor modificado
    public UsuarioAdapter(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ya no necesitamos el context porque no se pasa explícitamente
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);

        // Asignar los valores de los datos a las vistas
        holder.txtNombreUsuario.setText(usuario.getNombre());
        holder.txtCorreoUsuario.setText(usuario.getCorreo());
        holder.txtEstado.setText(usuario.getEstado());

        // Configuración del botón para cambiar el estado
        holder.btnCambiarEstado.setOnClickListener(v -> {
            // Cambiar el estado del usuario
            if (usuario.getEstado().equals("Activo")) {
                usuario.setEstado("Inactivo");
            } else {
                usuario.setEstado("Activo");
            }
            notifyItemChanged(position); // Actualizar el item en la vista
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreUsuario, txtCorreoUsuario, txtEstado;
        Button btnCambiarEstado;

        public UsuarioViewHolder(View itemView) {
            super(itemView);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
            txtCorreoUsuario = itemView.findViewById(R.id.txtCorreoUsuario);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            btnCambiarEstado = itemView.findViewById(R.id.btnCambiarEstado);
        }
    }
}