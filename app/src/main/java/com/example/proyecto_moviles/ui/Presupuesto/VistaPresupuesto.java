package com.example.proyecto_moviles.ui.Presupuesto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Categoria;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class VistaPresupuesto extends Fragment implements AdapterView.OnItemClickListener{

    private ListView lista;

    private List<Presupuesto> listaOriginal = new ArrayList<>();
    private Spinner categoria;
    ArrayAdapter<Categoria> adapterCategoria;
    List<Categoria> listaCategorias;
    private int id_usuario = 0;

    final String servidor = "http://10.0.2.2/PHP_PROYECTO_MOVILES/controladores/presupuestoController/";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_vista_presupuesto, container, false);
        // Inflate the layout for this fragment
        lista = (ListView) rootView.findViewById(R.id.lstPresupuestoMostrar);
        categoria = (Spinner) rootView.findViewById(R.id.spCategoriaM);

        listaCategorias = new ArrayList<>();
        listaCategorias.add(new Categoria(0, "Todos las Categorias"));
        cargarCategoriasDesdeServidor();

        adapterCategoria = new ArrayAdapter<>( getContext(),android.R.layout.simple_spinner_item,listaCategorias);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(adapterCategoria);

        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Categoria categoriaSeleccionada = (Categoria) parent.getItemAtPosition(position);
                filtrarPorCategoria(categoriaSeleccionada.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lista.setOnItemClickListener(this);

        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        id_usuario = prefs.getInt("id_usuario", -1);
        if (id_usuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
        }

        MostrarDatos();

        return rootView;
    }

    private void cargarCategoriasDesdeServidor() {
        String url = servidor + "obtener_categorias.php";

        RequestParams params = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String

                try {
                    // Parsear el JSON recibido
                    JSONArray jsonArray = new JSONArray(response);


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id_categoria = jsonObject.getInt("id_categoria");
                        String nombre_categoria = jsonObject.getString("nom_categoria");

                        Categoria categoria = new Categoria(id_categoria, nombre_categoria);
                        listaCategorias.add(categoria);
                    }

                    adapterCategoria = new ArrayAdapter<>( getContext(),android.R.layout.simple_spinner_item,listaCategorias);
                    adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categoria.setAdapter(adapterCategoria);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al parsear el JSON", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent==lista) //si estoy usando el listview lista
        {
            PopupMenu popupMenu = new PopupMenu(getActivity(),view);
            popupMenu.getMenuInflater().inflate(R.menu.opciones, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    // TextView id_contacto = view.findViewById(R.id.tvId);
                    TextView id_presupuesto = view.findViewById(R.id.tvIdPV);

                    // String idCont = id_paciente.getText().toString();
                    String idPresupuesto = id_presupuesto.getText().toString();

                    if (item.getItemId() == R.id.opc_editar)
                    {
                        EditarPaciente(idPresupuesto);
                    }
                    else if (item.getItemId() == R.id.opc_eliminar)
                    {
                        EliminarPaciente(idPresupuesto);
                    }
                    return false;
                }
            });
            popupMenu.show();
        }
    }

    private void actualizarLista(List<Presupuesto> listaFiltrada) {
        VistaPresupuesto.ContactAdapter adapter = new VistaPresupuesto.ContactAdapter(getActivity(), listaFiltrada);
        lista.setAdapter(adapter);
    }

    private void filtrarPorCategoria(int idCategoria) {
        if (idCategoria == 0) {
            actualizarLista(listaOriginal);
        } else {
            List<Presupuesto> filtrada = new ArrayList<>();
            for (Presupuesto p : listaOriginal) {
                if (p.getCategoria().getId() == idCategoria) {
                    filtrada.add(p);
                }
            }
            actualizarLista(filtrada);
        }
    }

    public class ContactAdapter extends BaseAdapter {

        private Context context;
        private List<Presupuesto> presupuestoList;

        public ContactAdapter(Context context, List<Presupuesto> contactList) {
            this.context = context;
            this.presupuestoList = contactList;
        }

        @Override
        public int getCount() {
            return presupuestoList.size();
        }

        @Override
        public Object getItem(int position) {
            return presupuestoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_presupuesto, null);
            }

            // Obtener los elementos de la vista
            TextView id = convertView.findViewById(R.id.tvIdPV);
            TextView monto = convertView.findViewById(R.id.tvMontoPV);
            TextView categoria = convertView.findViewById(R.id.tvCategoriaPV);

            // Obtener el contacto
            Presupuesto presupuesto = presupuestoList.get(position);

            // Asignar los valores
            id.setText(presupuesto.id);
            categoria.setText("Categoria: "+ presupuesto.categoria.getNombre());
            monto.setText("Monto: "+ presupuesto.monto);

            return convertView;
        }
    }

    private void MostrarDatos() {

        // Crear la URL para hacer la solicitud
        String url = servidor + "mostrar_presupuesto.php";

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();
        params.put("id_Usuario",id_usuario);

        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        // Hacer la solicitud GET
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String
                //Toast.makeText(getApplicationContext(), "Respuesta: " + response, Toast.LENGTH_LONG).show();

                try {
                    // Parsear el JSON recibido
                    JSONArray jsonArray = new JSONArray(response);
                    listaOriginal.clear(); // <- limpiar lista original

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String id = jsonObject.getString("id_presupuesto");
                        double monto = jsonObject.getDouble("pres_presupuesto");
                        String fechaInicio = jsonObject.getString("fini_presupuesto");
                        String fechaFin = jsonObject.getString("ffin_presupuesto");

                        int id_categoria = jsonObject.getInt("id_categoria");
                        String nom_categoria = jsonObject.getString("nom_categoria");

                        Categoria categoria = new Categoria(id_categoria, nom_categoria);
                        Presupuesto presupuesto = new Presupuesto(id, monto, fechaInicio, fechaFin ,categoria);

                        listaOriginal.add(presupuesto);
                    }

                    // Mostrar todos al inicio
                    actualizarLista(listaOriginal);

                    // Crear el adaptador y asignarlo al ListView
                    /*VistaPresupuesto.ContactAdapter adapter = new VistaPresupuesto.ContactAdapter(getActivity(), presupuestos);
                    // Asegúrate de que tu ListView tenga el ID correcto
                    lista.setAdapter(adapter);*/

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al parsear el JSON", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getActivity(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void EditarPaciente(String idPresupuesto) {

        Bundle bundle = new Bundle();
        bundle.putString("idPresupuesto", idPresupuesto);

        NavController navController = Navigation.findNavController(getView());
        navController.navigate(R.id.action_vistaPresupuesto_to_editarPresupuesto, bundle);
    }

    private void EliminarPaciente(String idPresupuesto) {
        // Crear la URL para hacer la solicitud
        String url = servidor + "eliminar_presupuesto.php";

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();
        params.put("idPresupuesto",idPresupuesto);

        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient presupuesto = new AsyncHttpClient();

        presupuesto.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String
                Toast.makeText(getActivity(), "Respuesta: " + response, Toast.LENGTH_LONG).show();
                MostrarDatos();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}