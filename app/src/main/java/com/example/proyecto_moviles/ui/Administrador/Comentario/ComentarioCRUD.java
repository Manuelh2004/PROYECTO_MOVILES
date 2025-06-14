package com.example.proyecto_moviles.ui.Administrador.Comentario;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

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
    private Spinner spinnerEstado;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comentario, container, false);

        recyclerViewComentarios = rootView.findViewById(R.id.recyclerViewComentarios);
        spinnerEstado = rootView.findViewById(R.id.spinner_estado);

        // Configurar el RecyclerView
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(getContext()));
        comentarioAdapter = new ComentarioAdapter(comentarios);
        recyclerViewComentarios.setAdapter(comentarioAdapter);

        // Cargar los comentarios al inicio
        obtenerComentarios("");

        // Agregar listener para el Spinner (estado)
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Obtener el valor seleccionado en el Spinner
                String estadoSeleccionado = parentView.getItemAtPosition(position).toString();
                // Llamar a la función para obtener los comentarios filtrados por estado
                obtenerComentarios(estadoSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Si no se selecciona nada, obtener todos los comentarios
                obtenerComentarios("");
            }
        });

        return rootView;
    }

    private void obtenerComentarios(String estado) {
        AsyncHttpClient client = new AsyncHttpClient();

        String url = "http://10.0.2.2/proyecto_moviles/controladores/comentarioController/listar_comentario.php";

        // Agregar el parámetro de estado a la URL
        if (!estado.isEmpty()) {
            url += "?estado=" + estado;
        }

        // Hacer la solicitud HTTP
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONArray jsonArray = new JSONArray(response);
                    comentarios.clear();

                    // Recorrer los resultados y agregarlos a la lista
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject comentarioJson = jsonArray.getJSONObject(i);
                        int id_comentario = comentarioJson.getInt("id_comentario");
                        String men_comentario = comentarioJson.getString("men_comentario");
                        String fre_comentario = comentarioJson.getString("fre_comentario");
                        String est_comentario = comentarioJson.getString("est_comentario");

                        // Reemplazar los valores 0 y 1 por "Revisado" y "No revisado"
                        if ("1".equals(est_comentario)) {
                            est_comentario = "No revisado";
                        } else if ("0".equals(est_comentario)) {
                            est_comentario = "Revisado";
                        }

                        Comentario comentario = new Comentario(id_comentario, men_comentario, fre_comentario, est_comentario);
                        comentarios.add(comentario);
                    }

                    comentarioAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Manejo de error
            }
        });
    }
}