package com.example.proyecto_moviles.ui.Administrador.Item;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Administrador.Item.Categoria.CategoriasAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ItemCRUD extends Fragment implements View.OnClickListener{
    private AsyncHttpClient client;
    private ArrayList<String> categorias = new ArrayList<>();
    private CategoriasAdapter categoriasAdapter;
    String servidor = "http://10.0.2.2/proyecto_moviles/controladores/";

    Button btnAgregarCategoria;
    EditText editTextCategoria;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout del fragmento
        View rootView = inflater.inflate(R.layout.fragment_items, container, false);

        client = new AsyncHttpClient();

        // Encuentra las vistas
        editTextCategoria = rootView.findViewById(R.id.editTextCategoria);
        btnAgregarCategoria = rootView.findViewById(R.id.btnAgregarCategoria);

        // RecyclerView recyclerViewCategorias = rootView.findViewById(R.id.recyclerViewCategorias);
        CardView cardView1 = rootView.findViewById(R.id.card_view_1);  // Cambiado a CardView

        // Configura el RecyclerView
        categoriasAdapter = new CategoriasAdapter(categorias);
        // recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(getContext()));
        // recyclerViewCategorias.setAdapter(categoriasAdapter);

        // Manejador del botón Agregar
        btnAgregarCategoria.setOnClickListener(this);

        obtenerCategoriasDesdeServidor();

        return rootView;
    }
    private void obtenerCategoriasDesdeServidor() {
        String servidor = "http://10.0.2.2/proyecto_moviles/controladores/presupuestoController/obtener_categorias.php";

        // Hacer la solicitud GET
        client.get(servidor, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    // Limpiar la lista antes de agregar las nuevas categorías
                    categorias.clear();

                    // Recorremos el array de objetos JSON que contienen "id_categoria" y "nom_categoria"
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject categoriaObject = response.getJSONObject(i);

                        // Extraemos el nombre de la categoría
                        String categoriaNombre = categoriaObject.getString("nom_categoria");

                        // Agregar la categoría a la lista
                        categorias.add(categoriaNombre);
                    }

                    // Notificar al adaptador que los datos han cambiado
                    categoriasAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al obtener categorías", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnAgregarCategoria) {
            // Obtener el texto de la categoría desde el EditText
            String nuevaCategoria = editTextCategoria.getText().toString().trim();

            // Verificar que no esté vacío
            if (!nuevaCategoria.isEmpty()) {
                // Enviar la nueva categoría al servidor
                agregarCategoria();
            } else {
                Toast.makeText(getContext(), "Ingresa una categoría", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void agregarCategoria() {
        String url = servidor + "itemsController/categoria/agregar_categoria.php";

        String nom_categoria = editTextCategoria.getText().toString();
        Integer est_categoria = 1;

        RequestParams params = new RequestParams();
        params.put("nom_categoria", nom_categoria);
        params.put("est_categoria", est_categoria);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.contains("success")) {
                    // Actualiza la interfaz en el hilo principal
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Categoría agregada correctamente", Toast.LENGTH_SHORT).show();
                        // Limpiar el EditText y recargar las categorías
                        editTextCategoria.setText("");
                        obtenerCategoriasDesdeServidor();  // Vuelve a obtener las categorías del servidor
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Error al agregar la categoría", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Error en la conexión", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}