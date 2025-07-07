package com.example.proyecto_moviles.ui.Administrador.Usuario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.Usuario;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>
{
    private List<Usuario> usuarios;
    public UsuarioAdapter(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);

        holder.txtNombreUsuario.setText(usuario.getNombre());
        holder.txtCorreoUsuario.setText(usuario.getCorreo());
        holder.txtEstado.setText(usuario.getEstado());
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreUsuario, txtCorreoUsuario, txtEstado;
        public UsuarioViewHolder(View itemView) {
            super(itemView);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
            txtCorreoUsuario = itemView.findViewById(R.id.txtCorreoUsuario);
            txtEstado = itemView.findViewById(R.id.txtEstado);
        }
    }
}