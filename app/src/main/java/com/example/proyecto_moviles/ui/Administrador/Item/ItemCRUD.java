package com.example.proyecto_moviles.ui.Administrador.Item;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Administrador.Item.Categoria.CategoriasAdapter;
import com.example.proyecto_moviles.ui.Clases.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import cz.msebera.android.httpclient.Header;

public class ItemCRUD extends Fragment implements View.OnClickListener{
    private AsyncHttpClient client;
    private ArrayList<String> categorias = new ArrayList<>();
    private CategoriasAdapter categoriasAdapter;
    private Button btnAgregarCategoria, btnAgregarGenero, btnAgregarTipoDocumento, btnVisualizarCategorias, btnVisualizarGeneros, btnVisualizarTipodeDocumentos;
    private EditText editTextCategoria, editTextGenero, editTextTipoDocumento;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_items, container, false);

        client = new AsyncHttpClient();
        editTextCategoria = rootView.findViewById(R.id.editTextCategoria);
        btnAgregarCategoria = rootView.findViewById(R.id.btnAgregarCategoria);
        editTextGenero = rootView.findViewById(R.id.editTextGenero);
        btnAgregarGenero = rootView.findViewById(R.id.btnAgregarGenero);
        editTextTipoDocumento = rootView.findViewById(R.id.editTextTipoDocumento);
        btnAgregarTipoDocumento = rootView.findViewById(R.id.btnAgregarTipoDocumento);

        btnVisualizarCategorias = rootView.findViewById(R.id.btnVisualizarCategorias);
        btnVisualizarGeneros = rootView.findViewById(R.id.visualizarGeneros);
        btnVisualizarTipodeDocumentos = rootView.findViewById(R.id.visualizarTipodeDocumentos);

        CardView cardView1 = rootView.findViewById(R.id.card_view_1);
        categoriasAdapter = new CategoriasAdapter(categorias);

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
        String url = ServidorConfig.URL_SERVIDOR + "presupuestoController/obtener_categorias.php";
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    categorias.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject categoriaObject = response.getJSONObject(i);
                        String categoriaNombre = categoriaObject.getString("nom_categoria");
                        categorias.add(categoriaNombre);
                    }
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
            String nuevaCategoria = editTextCategoria.getText().toString().trim();
            if (!nuevaCategoria.isEmpty()) {
                agregarCategoria();
            } else {
                Toast.makeText(getContext(), "Ingresa una nueva categoría", Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btnVisualizarCategorias) {
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
        String url = ServidorConfig.URL_SERVIDOR + "itemsController/categoria/agregar_categoria.php";
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
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Categoría agregada correctamente", Toast.LENGTH_SHORT).show();
                        editTextCategoria.setText("");
                        obtenerCategoriasDesdeServidor();
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
        String url = ServidorConfig.URL_SERVIDOR + "itemsController/genero/agregar_genero.php";
        String nom_genero = editTextGenero.getText().toString();

        RequestParams params = new RequestParams();
        params.put("nom_genero", nom_genero);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.contains("success")) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Género agregado correctamente", Toast.LENGTH_SHORT).show();
                        editTextGenero.setText("");
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
        String url = ServidorConfig.URL_SERVIDOR + "itemsController/tipo_documento/agregar_tipo_documento.php";
        String nom_tipo_documento = editTextTipoDocumento.getText().toString();

        RequestParams params = new RequestParams();
        params.put("nom_tipo_documento", nom_tipo_documento);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.contains("success")) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Tipo Documento agregado correctamente", Toast.LENGTH_SHORT).show();
                        editTextTipoDocumento.setText("");
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
        String url = ServidorConfig.URL_SERVIDOR + "AdministradorController/visualizar_categorias.php";
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
        String url = ServidorConfig.URL_SERVIDOR + "AdministradorController/visualizar_genero.php";
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
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
        String url = ServidorConfig.URL_SERVIDOR + "AdministradorController/visualizar_tipo_documento.php";
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