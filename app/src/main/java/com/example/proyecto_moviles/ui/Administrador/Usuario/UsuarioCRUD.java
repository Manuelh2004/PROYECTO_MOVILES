package com.example.proyecto_moviles.ui.Administrador.Usuario;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.ServidorConfig;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_usuario, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AsyncHttpClient client = new AsyncHttpClient();

        String url = ServidorConfig.URL_SERVIDOR + "usuarioController/listar_usuario.php";
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    usuarios.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject usuarioObj = response.getJSONObject(i);
                        String nombre = usuarioObj.getString("nombre");
                        String correo = usuarioObj.getString("correo");
                        String estado = usuarioObj.getString("estado");

                        usuarios.add(new Usuario(nombre, correo, estado));
                    }
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
                showError("Error al obtener los usuarios: " + throwable.getMessage());
            }
        });
        return rootView;
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
