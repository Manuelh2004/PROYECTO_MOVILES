package com.example.proyecto_moviles.ui.Inicio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.Item;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CrearCuenta extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    final String servidor = "http://10.0.2.2/proyecto_moviles/";
    private EditText etNombres, etApellidos, etTelefono, etFechaNa;
    private Button btnSiguiente, btnCancelar;
    private Spinner spPais;
    private Spinner spGenero;
    int idPais=-1, idGenero = -1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_crear_cuenta, container, false);

        etNombres = (EditText) rootView.findViewById(R.id.etNombres);
        etApellidos = (EditText) rootView.findViewById(R.id.etApellidos);
        etTelefono = (EditText) rootView.findViewById(R.id.etTelefono);
        etFechaNa = (EditText) rootView.findViewById(R.id.etFechaNa);

        spGenero = (Spinner) rootView.findViewById(R.id.spGenero);
        spGenero.setOnItemSelectedListener(this);
        spPais = (Spinner) rootView.findViewById(R.id.spPais);
        spPais.setOnItemSelectedListener(this);

        btnSiguiente = (Button) rootView.findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(this);
        btnCancelar = (Button) rootView.findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(this);


        obtenerPais();
        obtenerGenero();

        return  rootView;
    }

    private void obtenerGenero() {
        String url =  servidor+"obtener_genero.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                ArrayList<Item> lista = new ArrayList<>();

                lista.add(new Item(-1, "Seleccionar genero"));

                //Respuesta del servidor
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        lista.add(new Item(id, nombre));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //Llena el Spinner
                ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spGenero.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerPais() {
        String url =  servidor+"obtener_pais.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                ArrayList<Item> lista = new ArrayList<>();

                lista.add(new Item(-1, "Seleccionar pais"));

                //Respuesta del servidor
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        lista.add(new Item(id, nombre));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //Llena el Spinner
                ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spPais.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == btnSiguiente){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_crear_cuenta_to_resumenMovimientos); // action_crearCuenta_to_crearCuentaConfirmacion
        }
        if(v == btnCancelar){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_crear_cuenta_to_nav_login); // action_crearCuenta_to_nav_login
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent==spGenero)
        {
            Item selectedItem = (Item) parent.getItemAtPosition(position);

            // Verifica si es el item "Seleccionar categoría"
            if (selectedItem.id == -1) {
                // No hacer nada si es la opción "Seleccionar categoría"
                //Toast.makeText(getActivity(), "Por favor, seleccione una categoría", Toast.LENGTH_SHORT).show();
            } else {
                // Si no es el item ficticio, maneja la selección normalmente
                int selectedId = selectedItem.id;
                String selectedNombre = selectedItem.nombre;
                //Toast.makeText(getActivity(), "Seleccionado: " + selectedId + " - " + selectedNombre, Toast.LENGTH_SHORT).show();
                idGenero = selectedId;
            }
        }
        else if(parent==spPais)
        {
            Item selectedItem = (Item) parent.getItemAtPosition(position);

            // Verifica si es el item "Seleccionar marca"
            if (selectedItem.id == -1) {
                // No hacer nada si es la opción "Seleccionar marca"
                //Toast.makeText(getActivity(), "Por favor, seleccione una marca", Toast.LENGTH_SHORT).show();
            } else {
                // Si no es el item ficticio, maneja la selección normalmente
                int selectedId = selectedItem.id;
                String selectedNombre = selectedItem.nombre;
                //Toast.makeText(getActivity(), "Seleccionado: " + selectedId + " - " + selectedNombre, Toast.LENGTH_SHORT).show();
                idPais = selectedId;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}