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
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CrearCuenta extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, LoginDialogFragment.LoginDialogListener{
    private FirebaseAuth mAuth;
    final String servidor = "http://10.0.2.2/proyecto_moviles/controladores/";
    private EditText etNombres, etApellidos, etTelefono, etFechaNa, etDocumento;
    private Button btnCrearUsuario, btnCancelar;
    private Spinner spPais;
    private Spinner spGenero;
    private Spinner spTipoDoc;
    int idPais=-1, idGenero = -1, idTipoDoc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_crear_cuenta, container, false);
        mAuth = FirebaseAuth.getInstance();

        etNombres = (EditText) rootView.findViewById(R.id.etNombres);
        etApellidos = (EditText) rootView.findViewById(R.id.etApellidos);
        etTelefono = (EditText) rootView.findViewById(R.id.etTelefono);
        etFechaNa = (EditText) rootView.findViewById(R.id.etFechaNa);
        etDocumento = (EditText) rootView.findViewById(R.id.etDocumento);

        spGenero = (Spinner) rootView.findViewById(R.id.spGenero);
        spGenero.setOnItemSelectedListener(this);
        spPais = (Spinner) rootView.findViewById(R.id.spPais);
        spPais.setOnItemSelectedListener(this);
        spTipoDoc = (Spinner) rootView.findViewById(R.id.spTipoDoc);
        spTipoDoc.setOnItemSelectedListener(this);

        btnCrearUsuario = (Button) rootView.findViewById(R.id.btnCrearUsuario);
        btnCrearUsuario.setOnClickListener(this);
        btnCancelar = (Button) rootView.findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(this);

        obtenerPais();
        obtenerGenero();
        obtenerTipoDoc();

        return  rootView;
    }

    private void obtenerTipoDoc() {
        String url =  servidor+"itemsController/obtener_tipo_doc.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                ArrayList<Item> lista = new ArrayList<>();

                lista.add(new Item(-1, "Seleccionar Tipo de Documento"));

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
                spTipoDoc.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerGenero() {
        String url =  servidor+"itemsController/obtener_genero.php";

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
    private void LimpiarCampos()
    {
        etNombres.setText("");
        etApellidos.setText("");
        etTelefono.setText("");
        etFechaNa.setText("");
        etDocumento.setText("");
        spGenero.setSelection(0);
        spPais.setSelection(0);
        spTipoDoc.setSelection(0);
        etNombres.requestFocus();
    }

    private void obtenerPais() {
        String url =  servidor+"itemsController/obtener_pais.php";

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
        if (v == btnCrearUsuario) {
            // Solo validaciones básicas (puedes validar los EditText directamente sin guardar en variables)
            if (etNombres.getText().toString().isEmpty() ||
                    etApellidos.getText().toString().isEmpty() ||
                    etTelefono.getText().toString().isEmpty() ||
                    etDocumento.getText().toString().isEmpty() ||
                    etFechaNa.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (idPais == -1) {
                Toast.makeText(getActivity(), "Por favor, seleccione un pais", Toast.LENGTH_SHORT).show();
                return;
            }
            if (idGenero == -1) {
                Toast.makeText(getActivity(), "Por favor, seleccione un genero", Toast.LENGTH_SHORT).show();
                return;
            }
            if (idTipoDoc == -1) {
                Toast.makeText(getActivity(), "Por favor, seleccione un tipo de documento", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si todo bien, muestra el diálogo para email y password
            LoginDialogFragment dialog = new LoginDialogFragment();
            dialog.setLoginDialogListener(this);
            dialog.show(getParentFragmentManager(), "LoginDialog");
        }

        if(v == btnCancelar){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_crear_cuenta_to_nav_login); // action_crearCuenta_to_nav_login
        }
    }

    private void RegistrarUsuario(String nombres, String apellidos, String telefono, String documento, String fechaNa, int idPais, int idGenero, int idTipoDoc, String email, String password) {
        String url = servidor + "usuarioController/crear_usuario.php";

        RequestParams params = new RequestParams();
        params.put("nombres", nombres);
        params.put("apellidos", apellidos);
        params.put("telefono", telefono);
        params.put("documento", documento);
        params.put("fechaNa", fechaNa);
        params.put("idPais", idPais);
        params.put("idGenero", idGenero);
        params.put("idTipoDoc", idTipoDoc);
        params.put("email", email);
        params.put("password", password);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    boolean exito = response.getBoolean("exito");
                    String mensaje = response.getString("mensaje");
                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
                    if (exito) {
                        // Limpiar campos y navegar, por ejemplo:
                        LimpiarCampos();
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.action_nav_crear_cuenta_to_nav_presupuesto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });

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
        else if(parent==spTipoDoc)
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
                idTipoDoc = selectedId;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onLoginDataEntered(String email, String password) {
        String nombres  = etNombres.getText().toString();
        String apellidos = etApellidos.getText().toString();
        String telefono = etTelefono.getText().toString();
        String documento = etDocumento.getText().toString();
        String fechaNa = etFechaNa.getText().toString();

        RegistrarUsuario(nombres, apellidos, telefono, documento, fechaNa, idPais, idGenero, idTipoDoc, email, password);
        // Aquí puedes continuar con registro o navegación
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_nav_crear_cuenta_to_nav_presupuesto);
    }

}