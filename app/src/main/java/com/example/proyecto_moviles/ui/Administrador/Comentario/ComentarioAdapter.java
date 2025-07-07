package com.example.proyecto_moviles.ui.Administrador.Comentario;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.Comentario;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

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

        holder.btnCambiarEstado.setOnClickListener(v -> {
            cambiarEstadoComentario(comentario, position, v.getContext());
        });
    }

    @Override
    public int getItemCount() {
        return comentarios.size();  // Devuelve el tamaño de la lista de comentarios
    }

    // ViewHolder que contiene las vistas de cada item
    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        TextView menComentario, freComentario, estComentario;
        Button btnCambiarEstado;
        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            menComentario = itemView.findViewById(R.id.men_comentario);
            freComentario = itemView.findViewById(R.id.fre_comentario);
            estComentario = itemView.findViewById(R.id.est_comentario);
            btnCambiarEstado = itemView.findViewById(R.id.btn_cambiar_estado);
        }
    }

    private void cambiarEstadoComentario(Comentario comentario, int position, Context context) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id_comentario", comentario.getId_comentario());

        String url = "http://10.0.2.2/proyecto_moviles/controladores/comentarioController/cambiar_estado_comentario.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONObject json = new JSONObject(response);

                    if (json.getBoolean("success")) {
                        int nuevoEstado = json.getInt("nuevo_estado");
                        String nuevoTexto = (nuevoEstado == 1) ? "No revisado" : "Revisado";
                        comentario.setEst_comentario(nuevoTexto);
                        notifyItemChanged(position);
                    } else {
                        Log.e("Estado", "Error en respuesta: " + json.optString("error"));
                    }
                } catch (Exception e) {
                    Log.e("JSON", "Error al parsear: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("HTTP", "Fallo conexión: " + error.getMessage());
            }
        });
    }
}
