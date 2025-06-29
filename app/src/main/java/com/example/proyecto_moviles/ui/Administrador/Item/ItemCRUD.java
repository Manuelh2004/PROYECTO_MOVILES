package com.example.proyecto_moviles.ui.Administrador.Item;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
import java.util.List;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ItemCRUD extends Fragment implements View.OnClickListener{
    private AsyncHttpClient client;
    private ArrayList<String> categorias = new ArrayList<>();
    private CategoriasAdapter categoriasAdapter;
    String servidor = "http://10.0.2.2/proyecto_moviles/controladores/";

    Button btnAgregarCategoria, btnAgregarGenero, btnAgregarTipoDocumento, btnVisualizarCategorias, btnVisualizarGeneros, btnVisualizarTipodeDocumentos;
    EditText editTextCategoria, editTextGenero, editTextTipoDocumento;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout del fragmento
        View rootView = inflater.inflate(R.layout.fragment_items, container, false);

        client = new AsyncHttpClient();

        // Categorias
        editTextCategoria = rootView.findViewById(R.id.editTextCategoria);
        btnAgregarCategoria = rootView.findViewById(R.id.btnAgregarCategoria);

        // Genero
        editTextGenero = rootView.findViewById(R.id.editTextGenero);
        btnAgregarGenero = rootView.findViewById(R.id.btnAgregarGenero);

        // Tipo Documento
        editTextTipoDocumento = rootView.findViewById(R.id.editTextTipoDocumento);
        btnAgregarTipoDocumento = rootView.findViewById(R.id.btnAgregarTipoDocumento);

        btnVisualizarCategorias = rootView.findViewById(R.id.btnVisualizarCategorias);
        btnVisualizarGeneros = rootView.findViewById(R.id.visualizarGeneros);
        btnVisualizarTipodeDocumentos = rootView.findViewById(R.id.visualizarTipodeDocumentos);

        // RecyclerView recyclerViewCategorias = rootView.findViewById(R.id.recyclerViewCategorias);
        CardView cardView1 = rootView.findViewById(R.id.card_view_1);  // Cambiado a CardView

        // Configura el RecyclerView
        categoriasAdapter = new CategoriasAdapter(categorias);
        // recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(getContext()));
        // recyclerViewCategorias.setAdapter(categoriasAdapter);

        // Manejador del botón Agregar
        btnAgregarCategoria.setOnClickListener(this);
        btnAgregarGenero.setOnClickListener(this);
        btnAgregarTipoDocumento.setOnClickListener(this);
        btnVisualizarCategorias.setOnClickListener(this);
        btnVisualizarGeneros.setOnClickListener(this);
        btnVisualizarTipodeDocumentos.setOnClickListener(this);

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
                Toast.makeText(getContext(), "Ingresa una nueva categoría", Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btnVisualizarCategorias) {
            /*NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_administrador_to_nav_item);*/
            obtenerCategoriasDesdeAPI();
        }

        if (v == btnAgregarGenero) {
            String nuevoGenero = editTextGenero.getText().toString().trim();
            if (!nuevoGenero.isEmpty()) {
                agregarGenero();
            } else {
                Toast.makeText(getContext(), "Ingresa un nuevo genero", Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btnVisualizarGeneros) {
            obtenerCategoriasDesdeAPIGenero();
        }

        if (v == btnAgregarTipoDocumento) {
            String nuevoTipoDocumento = editTextTipoDocumento.getText().toString().trim();
            if (!nuevoTipoDocumento.isEmpty()) {
                agregarTipoDocumento();
            } else {
                Toast.makeText(getContext(), "Ingresa un nuevo tipo de documento", Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btnVisualizarTipodeDocumentos) {
            obtenerCategoriasDesdeAPIDocumento();
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
    private void agregarGenero() {
        String url = servidor + "itemsController/genero/agregar_genero.php";

        String nom_genero = editTextGenero.getText().toString();

        RequestParams params = new RequestParams();
        params.put("nom_genero", nom_genero);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.contains("success")) {
                    // Actualiza la interfaz en el hilo principal
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Género agregado correctamente", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Error al agregar el género", Toast.LENGTH_SHORT).show();
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

    private void agregarTipoDocumento() {
        String url = servidor + "itemsController/tipo_documento/agregar_tipo_documento.php";

        String nom_tipo_documento = editTextTipoDocumento.getText().toString();

        RequestParams params = new RequestParams();
        params.put("nom_tipo_documento", nom_tipo_documento);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.contains("success")) {
                    // Actualiza la interfaz en el hilo principal
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Tipo Documento agregado correctamente", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Error al agregar el tipo documento", Toast.LENGTH_SHORT).show();
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

    private void obtenerCategoriasDesdeAPI() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://10.0.2.2/proyecto_moviles/controladores/AdministradorController/visualizar_categorias.php"; // <-- Cambia esto por tu URL real

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<String> categorias = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        categorias.add(obj.getString("nom_categoria"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mostrarDialogoCategorias(categorias);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), "Error al obtener categorías", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoCategorias(List<String> listaCategorias) {
        if (listaCategorias.isEmpty()) {
            return;
        }

        String[] categoriasArray = listaCategorias.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Categorías disponibles")
                .setItems(categoriasArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String seleccion = categoriasArray[which];
                        Toast.makeText(getContext(), "Seleccionaste: " + seleccion, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cerrar", null)
                .show();
    }

    private void  obtenerCategoriasDesdeAPIGenero(){
        // Aquí puedes implementar la lógica para obtener las categorías desde la API
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://10.0.2.2/proyecto_moviles/controladores/AdministradorController/visualizar_genero.php";
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Aquí puedes manejar la respuesta del servidor
                List<String> generos = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        generos.add(obj.getString("nom_genero"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mostrarDialogoGeneros(generos);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), "Error al obtener genero", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoGeneros(List<String> listaGeneros) {
        if (listaGeneros.isEmpty()) {
            return;
        }

        String[] generosArray = listaGeneros.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Géneros disponibles")
                .setItems(generosArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String seleccion = generosArray[which];
                        Toast.makeText(getContext(), "Seleccionaste: " + seleccion, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cerrar", null)
                .show();
    }

    private void obtenerCategoriasDesdeAPIDocumento(){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://10.0.2.2/proyecto_moviles/controladores/AdministradorController/visualizar_tipo_documento.php";
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<String> documentos = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        documentos.add(obj.getString("nom_tipo_documento"));
                    } catch (Exception e) {
                        e.printStackTrace();
                }
            }
                mostrarDialogoDocumentos(documentos);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), "Error al obtener Documento", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoDocumentos(List<String> listaDocumentos) {
        if (listaDocumentos.isEmpty()) {
            return;
        }
        String[] documentosArray = listaDocumentos.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Documentos disponibles")
                .setItems(documentosArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String seleccion = documentosArray[which];
                        Toast.makeText(getContext(), "Seleccionaste: " + seleccion, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cerrar", null)
                .show();
    }
}