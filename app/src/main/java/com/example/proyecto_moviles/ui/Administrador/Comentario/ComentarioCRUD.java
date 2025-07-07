package com.example.proyecto_moviles.ui.Administrador.Comentario;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.Comentario;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ComentarioCRUD extends Fragment {
    private RecyclerView recyclerViewComentarios;
    private ComentarioAdapter comentarioAdapter;
    private List<Comentario> comentarios = new ArrayList<>();
    private Spinner spinnerEstado;
    private EditText etFechaInicio, etFechaFin;
    private String fechaInicio = "", fechaFin = "";
    final String servidor = "http://10.0.2.2/proyecto_moviles/controladores/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comentario, container, false);

        recyclerViewComentarios = rootView.findViewById(R.id.recyclerViewComentarios);
        spinnerEstado = rootView.findViewById(R.id.spinner_estado);

        // Configurar el RecyclerView
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(getContext()));
        comentarioAdapter = new ComentarioAdapter(comentarios);
        recyclerViewComentarios.setAdapter(comentarioAdapter);
        etFechaInicio = rootView.findViewById(R.id.et_fecha_inicio);
        etFechaFin = rootView.findViewById(R.id.et_fecha_fin);

        // Al hacer clic, mostrar DatePicker
        etFechaInicio.setOnClickListener(v -> mostrarDatePicker(true));
        etFechaFin.setOnClickListener(v -> mostrarDatePicker(false));

        // Cargar los comentarios al inicio
        obtenerComentarios("", "", "");

        // Agregar listener para el Spinner (estado)
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Obtener el valor seleccionado en el Spinner
                String estadoSeleccionado = parentView.getItemAtPosition(position).toString();
                if (estadoSeleccionado.equals("Todos")) {
                    estadoSeleccionado = "";
                }
                // Llamar a la función para obtener los comentarios filtrados por estado
                obtenerComentarios(estadoSeleccionado, fechaInicio, fechaFin);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Si no se selecciona nada, obtener todos los comentarios
                obtenerComentarios("", fechaInicio, fechaFin);
            }
        });

        return rootView;
    }

    private void obtenerComentarios(String estado, String fechaInicio, String fechaFin) {
        AsyncHttpClient client = new AsyncHttpClient();

        String url = servidor + "comentarioController/listar_comentario.php";

        List<String> parametros = new ArrayList<>();
        if (!estado.isEmpty()) {
            parametros.add("estado=" + estado);
        }
        if (!fechaInicio.isEmpty()) {
            parametros.add("fecha_inicio=" + fechaInicio);
        }
        if (!fechaFin.isEmpty()) {
            parametros.add("fecha_fin=" + fechaFin);
        }

        if (!parametros.isEmpty()) {
            url += "?" + String.join("&", parametros);
        }

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONArray jsonArray = new JSONArray(response);
                    comentarios.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject comentarioJson = jsonArray.getJSONObject(i);
                        int id_comentario = comentarioJson.getInt("id_comentario");
                        String men_comentario = comentarioJson.getString("men_comentario");
                        String fre_comentario = comentarioJson.getString("fre_comentario");
                        String est_comentario = comentarioJson.getString("est_comentario");

                        if ("1".equals(est_comentario)) {
                            est_comentario = "No revisado";
                        } else if ("0".equals(est_comentario)) {
                            est_comentario = "Revisado";
                        }

                        Comentario comentario = new Comentario(id_comentario, men_comentario, fre_comentario, est_comentario);
                        comentarios.add(comentario);
                    }

                    comentarioAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("JSON Error", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("HTTP Error", error.getMessage());
            }
        });
    }

    private void mostrarDatePicker(boolean esInicio) {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String fecha = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);

            if (esInicio) {
                fechaInicio = fecha;
                etFechaInicio.setText(fecha);
            } else {
                fechaFin = fecha;
                etFechaFin.setText(fecha);
            }

            // Refrescar comentarios cada vez que cambia una fecha
            String estadoSeleccionado = spinnerEstado.getSelectedItem().toString();
            obtenerComentarios(estadoSeleccionado, fechaInicio, fechaFin);

        }, anio, mes, dia);

        datePicker.show();
    }
}

