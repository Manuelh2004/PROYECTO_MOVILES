package com.example.proyecto_moviles.ui.Presupuesto;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class VistaPresupuesto extends Fragment implements AdapterView.OnItemClickListener{

    private ListView lista;

    private List<Presupuesto> listaOriginal = new ArrayList<>();
    private Spinner categoria;
    final String servidor = "http://10.0.2.2/PHP_PROYECTO_MOVILES/controladores/presupuestoController/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_vista_presupuesto, container, false);
        // Inflate the layout for this fragment
        lista = (ListView) rootView.findViewById(R.id.lstPresupuestoMostrar);
        categoria = (Spinner) rootView.findViewById(R.id.spCategoriaM);

        List<Categoria> listaCategorias = new ArrayList<>();
        listaCategorias.add(new Categoria(0, "Todos las Categorias"));
        listaCategorias.add(new Categoria(1, "Alimentación"));
        listaCategorias.add(new Categoria(2, "Transporte"));
        listaCategorias.add(new Categoria(3, "Salud"));

        ArrayAdapter<Categoria> adapterCategoria = new ArrayAdapter<>( getContext(),android.R.layout.simple_spinner_item,listaCategorias);
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

        MostrarDatos();

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
}