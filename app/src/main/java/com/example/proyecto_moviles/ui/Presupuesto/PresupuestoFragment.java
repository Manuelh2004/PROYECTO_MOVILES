package com.example.proyecto_moviles.ui.Presupuesto;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.proyecto_moviles.ui.Clases.Categoria;
import com.example.proyecto_moviles.ui.Clases.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;

import cz.msebera.android.httpclient.Header;

public class PresupuestoFragment extends Fragment implements View.OnClickListener {
    private EditText f_inicio, f_fin;
    private EditText mon;
    private Button agre, mos;
    private Spinner cat;
    private String modoSeleccion = "";
    private String fechaSeleccionada = "";
    private String fecha_inicio="", fecha_fin="";
    private int id_usuario = 0;
    private List<Categoria> listaCategorias;
    private ArrayAdapter<Categoria> adapterCategoria;
    SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    SimpleDateFormat formatoMySQL = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_presupuesto, container, false);

        f_inicio = (EditText) rootView.findViewById(R.id.etFechaInicio);
        f_fin = (EditText) rootView.findViewById(R.id.etFechaFin);
        mon = (EditText) rootView.findViewById(R.id.etMonto);
        agre = (Button) rootView.findViewById(R.id.btnAgregar);
        mos = (Button) rootView.findViewById(R.id.btnMostrar);
        cat = (Spinner) rootView.findViewById(R.id.spCategoria);

        // Establecer modo al tocar cada campo
        f_inicio.setOnClickListener(v -> {
            modoSeleccion = "inicio";
            Toast.makeText(getContext(),"Selecciona la nueva fecha de Incio",Toast.LENGTH_SHORT).show();
        });

        f_fin.setOnClickListener(v -> {
            modoSeleccion = "fin";
            Toast.makeText(getContext(), "Selecciona la nueva fecha de fin", Toast.LENGTH_SHORT).show();
        });

        f_inicio.setFocusable(false);
        f_inicio.setClickable(true);

        f_inicio.setOnClickListener(new View.OnClickListener() {
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
                                Calendar selectedDate = Calendar.getInstance();
                                fecha_inicio = String.format("%02d/%02d/%02d", dayOfMonth, monthOfYear + 1, year % 100);
                                f_inicio.setText(fecha_inicio);
                                selectedDate.set(year, monthOfYear, dayOfMonth);
                                fecha_inicio = formatoMySQL.format(selectedDate.getTime());
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        f_fin.setFocusable(false);
        f_fin.setClickable(true);
        f_fin.setOnClickListener(new View.OnClickListener() {
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
                                Calendar selectedDate = Calendar.getInstance();
                                fecha_fin = String.format("%02d/%02d/%02d", dayOfMonth, monthOfYear + 1, year % 100);
                                f_fin.setText(fecha_fin);
                                selectedDate.set(year, monthOfYear, dayOfMonth);
                                fecha_fin = formatoMySQL.format(selectedDate.getTime());
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
        listaCategorias = new ArrayList<>();
        cargarCategoriasDesdeServidor();

        mos.setOnClickListener(this);
        agre.setOnClickListener(this);

        return rootView;
    }

    private void cargarCategoriasDesdeServidor() {
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        id_usuario = prefs.getInt("id_usuario", -1);
        if (id_usuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = ServidorConfig.URL_SERVIDOR + "presupuestoController/obtener_categorias.php";
        RequestParams params = new RequestParams();
        params.put("id_usuario",id_usuario);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    listaCategorias.clear();
                    listaCategorias.add(new Categoria(0, "Seleccione una categoría"));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id_categoria = jsonObject.getInt("id_categoria");
                        String nombre_categoria = jsonObject.getString("nom_categoria");

                        Categoria categoria = new Categoria(id_categoria, nombre_categoria);
                        listaCategorias.add(categoria);
                    }

                    adapterCategoria = new ArrayAdapter<>( getContext(),android.R.layout.simple_spinner_item,listaCategorias);
                    adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cat.setAdapter(adapterCategoria);

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
    public void onClick(View v) {
        if(v == mos){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_presupuesto_to_vistaPresupuesto);
        }else if (v == agre){
            if (!validarCampos()) return;

            Categoria categoriaSeleccionada = (Categoria) cat.getSelectedItem();
            Float montoP = Float.parseFloat(mon.getText().toString());
            int categoriaP = categoriaSeleccionada.getId();
            String fechaInicioP = fecha_inicio;
            String fechaFinP = fecha_fin;

            RegistrarPresupuesto(montoP, categoriaP, fechaInicioP, fechaFinP);
            limpiarCampos();
        }
    }

    private boolean validarCampos() {
        if (cat.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return false;
        }
        String montoTexto = mon.getText().toString().trim();
        if (montoTexto.isEmpty()) {
            Toast.makeText(getContext(), "Ingrese un monto válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            float monto = Float.parseFloat(montoTexto);
            if (monto <= 0) {
                Toast.makeText(getContext(), "El monto debe ser mayor a cero", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Monto no válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fecha_inicio == null || fecha_inicio.isEmpty() || fecha_fin == null || fecha_fin.isEmpty()) {
            Toast.makeText(getContext(), "Seleccione las fechas de inicio y fin", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fecha_inicio.compareTo(fecha_fin) > 0) {
            Toast.makeText(getContext(), "La fecha de inicio no puede ser mayor que la fecha de fin", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void limpiarCampos() {
        mon.setText("");
        cat.setSelection(0);
        fecha_inicio = "";
        fecha_fin = "";
        f_inicio.setText("");
        f_fin.setText("");
    }

    private void RegistrarPresupuesto(Float montoP, int categoriaP, String fechaInicioP, String fechaFinP) {
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        id_usuario = prefs.getInt("id_usuario", -1);
        if (id_usuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = ServidorConfig.URL_SERVIDOR + "presupuestoController/presupuesto_registrar.php";

        RequestParams params = new RequestParams();
        params.put("id_usuario", id_usuario);
        params.put("montoP", montoP);
        params.put("categoriaP", categoriaP);
        params.put("fechaInicioP", fechaInicioP);
        params.put("fechaFinP", fechaFinP);

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Toast.makeText(getActivity(), "Respuesta: " + response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Toast.makeText(getActivity(), "Error: " + response, Toast.LENGTH_LONG).show();
            }
        });
    }
}