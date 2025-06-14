package com.example.proyecto_moviles.ui.Administrador.Comentario;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.Comentario;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ComentarioCRUD extends Fragment {
    private RecyclerView recyclerViewComentarios;
    private ComentarioAdapter comentarioAdapter;
    private List<Comentario> comentarios = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comentario, container, false);

        // Obtener la referencia al RecyclerView
        recyclerViewComentarios = rootView.findViewById(R.id.recyclerViewComentarios);

        // Configurar el LayoutManager
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configurar el adaptador
        comentarioAdapter = new ComentarioAdapter(comentarios);
        recyclerViewComentarios.setAdapter(comentarioAdapter);

        // Obtener los comentarios desde el servidor
        obtenerComentarios();

        return rootView;
    }

    private void obtenerComentarios() {
        AsyncHttpClient client = new AsyncHttpClient();

        String url = "http://10.0.2.2/proyecto_moviles/controladores/comentarioController/listar_comentario.php";

        // Hacer la petición GET
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    // Convertir el cuerpo de la respuesta a String
                    String response = new String(responseBody, "UTF-8");

                    // Parsear la respuesta JSON
                    JSONArray jsonArray = new JSONArray(response);
                    comentarios.clear(); // Limpiar la lista antes de agregar los nuevos datos

                    // Recorrer el arreglo JSON y agregar los comentarios a la lista
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject comentarioJson = jsonArray.getJSONObject(i);
                        int id_comentario = comentarioJson.getInt("id_comentario");
                        String men_comentario = comentarioJson.getString("men_comentario");
                        String fre_comentario = comentarioJson.getString("fre_comentario");
                        String est_comentario = comentarioJson.getString("est_comentario");

                        // Crear el objeto Comentario y agregarlo a la lista
                        Comentario comentario = new Comentario(id_comentario, men_comentario, fre_comentario, est_comentario);
                        comentarios.add(comentario);
                    }

                    // Notificar al adaptador que los datos han cambiado
                    comentarioAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    // Manejo de excepciones
                    Log.e("Error", "Error al procesar la respuesta: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Manejo del error
                Log.e("Error", "Fallo la petición: " + error.getMessage());
                // Aquí puedes mostrar un mensaje de error en la UI
            }
        });
    }
}