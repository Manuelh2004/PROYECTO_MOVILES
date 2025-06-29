package com.example.proyecto_moviles.ui.Movimiento;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.Item;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class AgregarMovimiento extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText etDescripcion, etMonto;
    private Spinner spCategoria, spTipoMovimiento;
    final String servidor = "http://10.0.2.2/proyecto_moviles/controladores/";
    private Button btnRegistrarMovimiento, btnMostrarMovimientos;
    int idCategoria = -1, idTipoMovimiento = -1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movimiento, container, false);
        etDescripcion = rootView.findViewById(R.id.etDescripcion);
        etMonto = rootView.findViewById(R.id.etMonto);

        spCategoria = rootView.findViewById(R.id.spCategoria);
        spCategoria.setOnItemSelectedListener(this);
        spTipoMovimiento = rootView.findViewById(R.id.spTipoMovimiento);
        spTipoMovimiento.setOnItemSelectedListener(this);

        btnRegistrarMovimiento = rootView.findViewById(R.id.btnActualizarMovimiento);
        btnRegistrarMovimiento.setOnClickListener(this);
        btnMostrarMovimientos = rootView.findViewById(R.id.btnMostrarMovimientos);
        btnMostrarMovimientos.setOnClickListener(this);

        obtenerCategoriaConPresupuesto();
        obtenerTipoMovimiento();

        return rootView;
    }

    private void obtenerTipoMovimiento() {
        String url = servidor + "itemsController/obtener_tipo_movimiento.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Item> lista = new ArrayList<>();
                lista.add(new Item(-1, "Seleccionar un tipo de movimiento"));

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
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = servidor + "itemsController/obtener_categoria_presupuesto.php";
        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Item> lista = new ArrayList<>();
                lista.add(new Item(-1, "Seleccionar una categoria"));

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

                ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategoria.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar categorías", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LimpiarCampos() {
        etMonto.setText("");
        etDescripcion.setText("");
        spCategoria.setSelection(0);
        spTipoMovimiento.setSelection(0);
        etMonto.requestFocus();
    }

    @Override
    public void onClick(View v) {
        if (v == btnRegistrarMovimiento) {
            if (etMonto.getText().toString().isEmpty() || etDescripcion.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (idCategoria == -1) {
                Toast.makeText(getActivity(), "Por favor, seleccione una categoría", Toast.LENGTH_SHORT).show();
                return;
            }
            if (idTipoMovimiento == -1) {
                Toast.makeText(getActivity(), "Por favor, seleccione un tipo de movimiento", Toast.LENGTH_SHORT).show();
                return;
            }
            agregarMovimiento();
        }

        if (v == btnMostrarMovimientos) {
            try {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_movimiento_to_nav_listar_movimientos);
                LimpiarCampos();
            } catch (Exception e) {
                Log.e("MiApp", "Error en la navegación", e);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Item selectedItem = (Item) parent.getItemAtPosition(position);
        if (parent == spCategoria) {
            idCategoria = (selectedItem.id != -1) ? selectedItem.id : -1;
        } else if (parent == spTipoMovimiento) {
            idTipoMovimiento = (selectedItem.id != -1) ? selectedItem.id : -1;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void agregarMovimiento() {
        String url = servidor + "movimientoController/registrar_movimiento.php";

        String descripcion = etDescripcion.getText().toString();
        String montoStr = etMonto.getText().toString();

        double monto;
        try {
            monto = Double.parseDouble(montoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

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
        params.put("des_movimiento", descripcion);
        params.put("id_usuario", idUsuario);
        params.put("est_movimiento", "1");

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.contains("success")) {
                    Toast.makeText(getActivity(), "Movimiento agregado correctamente", Toast.LENGTH_SHORT).show();
                    LimpiarCampos();
                    actualizarPresupuesto(monto);
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

    private void actualizarPresupuesto(double monto) {
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        if (idUsuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = servidor + "presupuestoController/actualizar_presupuesto_movimiento.php";
        double operacionPresupuesto = (idTipoMovimiento == 1) ? monto : -monto;

        RequestParams params = new RequestParams();
        params.put("id_categoria", idCategoria);
        params.put("id_usuario", idUsuario);
        params.put("monto_actualizado", operacionPresupuesto);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (!response.contains("success")) {
                    Toast.makeText(getActivity(), "Error al actualizar el presupuesto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error en la conexión al actualizar el presupuesto", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
