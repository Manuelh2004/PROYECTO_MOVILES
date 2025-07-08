package com.example.proyecto_moviles.ui.Movimiento;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.proyecto_moviles.ui.Clases.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class EditarMovimiento extends Fragment implements View.OnClickListener {
    private EditText etFecha, etDescripcion, etMonto;
    private Spinner spCategoria, spTipoMovimiento;
    private Button btnActualizarMovimiento;
    private String idMovimiento;
    private int idCategoria = -1, idTipoMovimiento = -1, idUsuario = -1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_editar_movimiento, container, false);
        idMovimiento = getArguments() != null ? getArguments().getString("id_movimiento") : null;

        etFecha = rootView.findViewById(R.id.etFecha);
        etDescripcion = rootView.findViewById(R.id.etDescripcion);
        etMonto = rootView.findViewById(R.id.etMonto);
        spCategoria = rootView.findViewById(R.id.spCategoria);
        spTipoMovimiento = rootView.findViewById(R.id.spTipoMovimiento);
        btnActualizarMovimiento = rootView.findViewById(R.id.btnActualizarMovimiento);
        btnActualizarMovimiento.setOnClickListener(this);

        cargarMovimiento();

        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);
        if (idUsuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
        }

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
        return rootView;
    }

    private void cargarMovimiento() {
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("ID Usuario", "idUsuario: " + idUsuario);
        String url = ServidorConfig.URL_SERVIDOR + "movimientoController/consultar_movimiento.php";

        RequestParams params = new RequestParams();
        params.put("idMovimiento", idMovimiento);
        params.put("idUsuario", idUsuario);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("Respuesta JSON", response);

                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.has("error")) {
                        Toast.makeText(getActivity(), responseJson.getString("error"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    etFecha.setText(responseJson.getString("fech_movimiento"));
                    etDescripcion.setText(responseJson.getString("des_movimiento"));
                    etMonto.setText(responseJson.getString("mon_movimiento"));

                    idCategoria = responseJson.getInt("id_categoria");
                    idTipoMovimiento = responseJson.getInt("id_tipo_movimiento");
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
                    cargarCategorias();
                    cargarTiposMovimiento();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al parsear los datos del movimiento: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMsg = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getActivity(), "Error al obtener movimiento: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Método para seleccionar un ítem en el Spinner
    private void setSelectedSpinnerItem(Spinner spinner, int selectedId) {
        if (spinner != null && spinner.getAdapter() != null) {
            for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
                Item item = (Item) spinner.getAdapter().getItem(i);
                if (item.id == selectedId) {
                    spinner.setSelection(i);
                    break;
                }
            }
        } else {
            Log.d("Spinner", "El Spinner o el Adapter son nulos");
        }
    }
    private void cargarTiposMovimiento() {
        String url = ServidorConfig.URL_SERVIDOR + "itemsController/obtener_tipo_movimiento.php";
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    ArrayList<Item> listaTipoMovimiento = new ArrayList<>();
                    listaTipoMovimiento.add(new Item(-1, "Seleccionar un tipo de movimiento"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        listaTipoMovimiento.add(new Item(id, nombre));
                    }
                    ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaTipoMovimiento);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spTipoMovimiento.setAdapter(adapter);
                    setSelectedSpinnerItem(spTipoMovimiento, idTipoMovimiento);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al cargar tipos de movimiento", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error al obtener tipos de movimiento", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarCategorias() {
        String url = ServidorConfig.URL_SERVIDOR + "itemsController/obtener_categoria_presupuesto.php";
        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    ArrayList<Item> listaCategoria = new ArrayList<>();
                    listaCategoria.add(new Item(-1, "Seleccionar una categoría"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        listaCategoria.add(new Item(id, nombre));
                    }
                    ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaCategoria);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCategoria.setAdapter(adapter);
                    setSelectedSpinnerItem(spCategoria, idCategoria);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al cargar categorías", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error al obtener categorías", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarMovimiento() {

        String fecha = etFecha.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String montoStr = etMonto.getText().toString();
        double montoNuevo;
        try {
            montoNuevo = Double.parseDouble(etMonto.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (descripcion.isEmpty() || fecha.isEmpty() || montoStr.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show();
            return;
        }
        double monto;

        try {
            monto = Double.parseDouble(montoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("PARAMS_DEBUG", "idMovimiento=" + idMovimiento +
                ", idTipoMovimiento=" + idTipoMovimiento +
                ", idCategoria=" + idCategoria +
                ", monto=" + montoNuevo +
                ", fecha=" + fecha +
                ", descripcion=" + descripcion +
                ", idUsuario=" + idUsuario);

        String url = ServidorConfig.URL_SERVIDOR + "movimientoController/actualizar_movimiento.php";
        RequestParams params = new RequestParams();
        params.put("id_movimiento", idMovimiento);
        params.put("id_tipo_movimiento", idTipoMovimiento);
        params.put("id_categoria", idCategoria);
        params.put("mon_movimiento", montoNuevo);
        params.put("fech_movimiento", fecha);
        params.put("des_movimiento", descripcion);
        params.put("id_usuario", idUsuario);
        params.put("est_movimiento",1);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("Respuesta de Actualización", response);

                if (response.contains("success")) {
                    Toast.makeText(getActivity(), "Movimiento actualizado correctamente", Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.action_nav_editar_movimiento_to_nav_listar_movimientos);
                } else {
                    Toast.makeText(getActivity(), "Error al actualizar el movimiento: " + response, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMsg = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getActivity(), "Error en la conexión: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnActualizarMovimiento) {
            actualizarMovimiento();
        }
    }
}