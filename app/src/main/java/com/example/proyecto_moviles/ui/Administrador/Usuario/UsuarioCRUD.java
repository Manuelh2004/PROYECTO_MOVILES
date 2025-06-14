package com.example.proyecto_moviles.ui.Administrador.Usuario;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.Usuario;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class UsuarioCRUD extends Fragment {

    private RecyclerView recyclerView;
    private UsuarioAdapter adapter;
    private List<Usuario> usuarios = new ArrayList<>();
    private final String servidor = "http://10.0.2.2/proyecto_moviles/controladores/usuarioController/listar_usuario.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_usuario, container, false);

        // Inicializar el RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerViewUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crear el cliente AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        // Realizar la solicitud GET
        client.get(servidor, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Aquí manejamos la respuesta exitosa
                try {
                    // Limpiar la lista de usuarios antes de llenarla de nuevo
                    usuarios.clear();

                    // Convertir el JSONArray en objetos Usuario
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject usuarioObj = response.getJSONObject(i);
                        String nombre = usuarioObj.getString("nombre");
                        String correo = usuarioObj.getString("correo");
                        // Asumimos que "estado" se pasa como un campo adicional desde el servidor
                        String estado = usuarioObj.getString("estado");

                        // Crear el objeto Usuario con el nuevo campo de estado
                        usuarios.add(new Usuario(nombre, correo, estado));
                    }

                    // Configurar el adapter si no está configurado
                    if (adapter == null) {
                        adapter = new UsuarioAdapter(usuarios);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Error al procesar los datos");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Manejar errores
                showError("Error al obtener los usuarios: " + throwable.getMessage());
            }
        });

        return rootView;
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
