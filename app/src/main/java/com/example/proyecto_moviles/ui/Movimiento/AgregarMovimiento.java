package com.example.proyecto_moviles.ui.Movimiento;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.Item;
import com.example.proyecto_moviles.ui.Inicio.LoginDialogFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cz.msebera.android.httpclient.Header;

public class AgregarMovimiento extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    private EditText etFecha, etDescripcion, etMonto;
    private Spinner spCategoria, spTipoMovimiento;
    final String servidor = "http://10.0.2.2/PHP_PROYECTO_MOVILES/controladores/";
    private Button btnRegistrarMovimiento, btnMostrarMovimientos;
    int idCategoria=-1, idTipoMovimiento = -1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movimiento, container, false);
        etFecha = (EditText) rootView.findViewById(R.id.etFecha);
        etDescripcion = (EditText) rootView.findViewById(R.id.etDescripcion);
        etMonto = (EditText) rootView.findViewById(R.id.etMonto);

        spCategoria = (Spinner) rootView.findViewById(R.id.spCategoria);
        spCategoria.setOnItemSelectedListener(this);
        spTipoMovimiento = (Spinner) rootView.findViewById(R.id.spTipoMovimiento);
        spTipoMovimiento.setOnItemSelectedListener(this);

        btnRegistrarMovimiento = (Button) rootView.findViewById(R.id.btnRegistrarMovimiento);
        btnRegistrarMovimiento.setOnClickListener(this);
        btnMostrarMovimientos = (Button) rootView.findViewById(R.id.btnMostrarMovimientos);
        btnMostrarMovimientos.setOnClickListener(this);

        // Desactivamos edición directa y ponemos listener para abrir DatePicker
        etFecha.setFocusable(false);
        etFecha.setClickable(true);

        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String formattedDate = String.format("%02d/%02d/%02d", dayOfMonth, monthOfYear + 1, year % 100);
                                etFecha.setText(formattedDate);
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        obtenerCategoriaConPresupuesto();
        obtenerTipoMovimiento();

        return rootView;
    }

    private void obtenerTipoMovimiento() {
        String url =  servidor+"itemsController/obtener_tipo_movimiento.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                ArrayList<Item> lista = new ArrayList<>();

                lista.add(new Item(-1, "Seleccionar un tipo de movimiento"));

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
                spTipoMovimiento.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerCategoriaConPresupuesto() {
        String url =  servidor+"itemsController/obtener_categoria_presupuesto.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                ArrayList<Item> lista = new ArrayList<>();

                lista.add(new Item(-1, "Seleccionar una categoria"));

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
                spCategoria.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void LimpiarCampos()
    {
        etMonto.setText("");
        etDescripcion.setText("");
        etFecha.setText("");
        spCategoria.setSelection(0);
        spTipoMovimiento.setSelection(0);
        etMonto.requestFocus();
    }

    @Override
    public void onClick(View v) {
        if (v == btnRegistrarMovimiento) {
            // Solo validaciones básicas (puedes validar los EditText directamente sin guardar en variables)
            if (etMonto.getText().toString().isEmpty() ||
                    etDescripcion.getText().toString().isEmpty() ||
                    etFecha.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (idCategoria == -1) {
                Toast.makeText(getActivity(), "Por favor, seleccione una categoria", Toast.LENGTH_SHORT).show();
                return;
            }
            if (idTipoMovimiento == -1) {
                Toast.makeText(getActivity(), "Por favor, seleccione un tipo de movimiento", Toast.LENGTH_SHORT).show();
                return;
            }

            agregarMovimiento();
        }

        if(v == btnMostrarMovimientos){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_movimiento_to_listarMovimientos); // action_crearCuenta_to_nav_login
            LimpiarCampos();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent==spCategoria)
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
                idCategoria = selectedId;
            }
        }
        else if(parent==spTipoMovimiento)
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
                idTipoMovimiento = selectedId;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void agregarMovimiento() {
        String url = servidor + "movimientoController/registrar_movimiento.php";

        String fecha = etFecha.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String montoStr = etMonto.getText().toString();

        double monto;
        try {
            monto = Double.parseDouble(montoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener id_usuario numérico guardado en SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        if (idUsuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("id_tipo_movimiento", idTipoMovimiento);
        params.put("id_categoria", idCategoria);
        params.put("mon_movimiento", monto);
        params.put("fech_movimiento", fecha);
        params.put("des_movimiento", descripcion);
        params.put("id_usuario", idUsuario);  // Usamos ID numérico aquí
        params.put("est_movimiento", "1");

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if(response.contains("success")) {
                    Toast.makeText(getActivity(), "Movimiento agregado correctamente", Toast.LENGTH_SHORT).show();
                    LimpiarCampos();
                } else {
                    Toast.makeText(getActivity(), "Error al agregar movimiento", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }





}