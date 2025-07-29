package com.example.proyecto_moviles.ui.Movimiento;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.Movimiento;
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
import androidx.activity.OnBackPressedCallback;
import cz.msebera.android.httpclient.Header;

public class ListarMovimientos extends Fragment implements View.OnClickListener{
    private ListView listaMovimientos;
    private Spinner spCategoriaFiltro;
    private EditText etFechaFiltro;
    private Button btnIrAgregarMovimiento;
    private String fechaFiltro = null, categoriaFiltro = null;
    public class MovimientoAdapter extends BaseAdapter{
        private Context context;
        private final List<Movimiento> movimientoList;
        public MovimientoAdapter(Context context, List<Movimiento> movimientoList) {
            this.context = context;
            this.movimientoList = movimientoList;
        }

        @Override
        public int getCount() {
            return movimientoList.size();
        }

        @Override
        public Object getItem(int position) {
            return movimientoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_movimiento, null);
            }

            TextView tvId = convertView.findViewById(R.id.tvIdPV);
            TextView tvUsuario = convertView.findViewById(R.id.tvUsuario);
            TextView tvTipo = convertView.findViewById(R.id.tvTipoMovimiento);
            TextView tvCategoria = convertView.findViewById(R.id.tvCategoria);
            TextView tvMonto = convertView.findViewById(R.id.tvMonto);
            TextView tvFecha = convertView.findViewById(R.id.tvFecha);
            TextView tvDescripcion = convertView.findViewById(R.id.tvDescripcion);
            TextView tvEstado = convertView.findViewById(R.id.tvEstado);
            Button editar = convertView.findViewById(R.id.btnEditar);
            Button eliminar = convertView.findViewById(R.id.btnEliminar);

            Movimiento mov = movimientoList.get(position);

            tvId.setText("ID: " + mov.getId_movimiento());
            tvUsuario.setText("Usuario: " + mov.getUsuario());
            tvTipo.setText("Tipo: " + mov.getTipo_movimiento());
            tvCategoria.setText("Categoría: " + mov.getCategoria());
            tvMonto.setText("Monto: " + mov.getMonto());
            tvFecha.setText("Fecha: " + mov.getFecha());
            tvDescripcion.setText("Descripción: " + mov.getDescripcion());
            tvEstado.setText("Estado: " + (mov.getEstado().equals("1") ? "Activo" : "Inactivo"));
            editar.setOnClickListener(v -> EditarMovimiento(mov.getId_movimiento()));
            eliminar.setOnClickListener(v -> EliminarMovimiento(mov.getId_movimiento()));
            return convertView;
        }
    }
    @SuppressLint("NotConstructor")
    private void ListarMovimientos(int idUsuario) {
        String url = ServidorConfig.URL_SERVIDOR + "movimientoController/mostrar_movimiento.php";
        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);

        // Filtros de fecha
        if (fechaFiltro != null) {
            params.put("fecha_inicio", fechaFiltro);
            Log.d("FECHA_ENVIADA", "Fecha enviada al backend: " + fechaFiltro);
        } else {
            Log.d("FECHA_ENVIADA", "No se seleccionó ninguna fecha.");
        }
        // Filtro de categoría
        if (categoriaFiltro != null && !categoriaFiltro.isEmpty()) {
            params.put("id_categoria", categoriaFiltro);
        }
        Log.d("Parametros GET", params.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("RESPUESTA_BACKEND", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    List<Movimiento> movimientos = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String id_movimiento = obj.getString("id_movimiento");
                        String usuario = obj.optString("usuario", "N/A");
                        String tipo_movimiento = obj.getString("nom_tipo_movimiento");
                        String categoria = obj.getString("nom_categoria");
                        String monto = obj.getString("mon_movimiento");
                        String fecha = obj.getString("fech_movimiento");
                        String descripcion = obj.getString("des_movimiento");
                        String estado = obj.getString("est_movimiento");

                        movimientos.add(new Movimiento(id_movimiento, usuario, tipo_movimiento, categoria,
                                monto, fecha, descripcion, estado));
                    }
                    MovimientoAdapter adapter = new MovimientoAdapter(getActivity(), movimientos);
                    listaMovimientos.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al procesar datos de movimientos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMsg = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getActivity(), "Error: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listar_movimientos, container, false);

        listaMovimientos = rootView.findViewById(R.id.lstMovimientos);
        spCategoriaFiltro = rootView.findViewById(R.id.spCategoriaFiltro);
        etFechaFiltro = rootView.findViewById(R.id.etFechaFiltro);

        etFechaFiltro.setOnClickListener(v -> openDatePicker());
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Log.e("Error", "No se encontró id_usuario en SharedPreferences.");
            Toast.makeText(getActivity(), "Error: No se encontró el ID de usuario", Toast.LENGTH_SHORT).show();
            return rootView;
        }

        cargarCategoriasFiltro(idUsuario);
        ListarMovimientos(idUsuario);

        spCategoriaFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                categoriaFiltro = position > 0 ? String.valueOf(position) : null;
                ListarMovimientos(idUsuario);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        btnIrAgregarMovimiento = rootView.findViewById(R.id.btnIrAgregarMovimiento);
        btnIrAgregarMovimiento.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_listar_movimientos_to_nav_movimiento);
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_listar_movimientos,
                        null,
                        new androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.nav_editar_movimiento, true) // Esto borra Editar del back stack
                                .build()
                );
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return rootView;
    }

    private void EliminarMovimiento(String idMovimiento) {
        Context context = getActivity();
        if (context == null) return;
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirmar_eliminacion, null);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);
        Button btnEliminar = dialogView.findViewById(R.id.btnEliminar);

        // Crear el diálogo
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnEliminar.setOnClickListener(v -> {
            dialog.dismiss();
            eliminarMovimiento(idMovimiento);
        });
        dialog.show();
    }

    private void eliminarMovimiento(String idMovimiento) {
        String url = ServidorConfig.URL_SERVIDOR + "movimientoController/eliminar_movimiento.php";
        RequestParams params = new RequestParams();
        params.put("id_movimiento", idMovimiento);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.contains("success")) {
                    Toast.makeText(getActivity(), "Movimiento eliminado correctamente", Toast.LENGTH_SHORT).show();
                    SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    int idUsuario = prefs.getInt("id_usuario", -1);
                    ListarMovimientos(idUsuario);
                } else {
                    Toast.makeText(getActivity(), "Error al eliminar el movimiento", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error de conexión: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void EditarMovimiento(String idMovimiento) {
        Bundle bundle = new Bundle();
        bundle.putString("id_movimiento", idMovimiento);

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.nav_editar_movimiento, bundle,
                new androidx.navigation.NavOptions.Builder()
                        .setPopUpTo(R.id.nav_listar_movimientos, true)
                        .build()
        );
    }

    private void cargarCategoriasFiltro(int idUsuario) {
        if (idUsuario == -1) {
            Log.e("Error", "id_usuario no válido");
            Toast.makeText(getActivity(), "Error: No se ha encontrado el ID de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ServidorConfig.URL_SERVIDOR + "itemsController/obtener_categoria_presupuesto_mov.php";
        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseString = new String(responseBody);
                    JSONArray jsonArray = new JSONArray(responseString);
                    List<String> categorias = new ArrayList<>();
                    List<Integer> categoriasIds = new ArrayList<>();

                    // Opción 0
                    categorias.add("Todas las categorías");
                    categoriasIds.add(0); // O usa null si tu backend lo permite

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        categorias.add(obj.getString("nombre"));
                        categoriasIds.add(obj.getInt("id_categoria"));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categorias);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCategoriaFiltro.setAdapter(adapter);

                    spCategoriaFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            if (position == 0) {
                                categoriaFiltro = null; // No enviar categoría
                            } else {
                                categoriaFiltro = String.valueOf(categoriasIds.get(position));
                            }
                            ListarMovimientos(idUsuario);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            categoriaFiltro = null;
                        }
                    });
                } catch (JSONException e) {
                    Log.e("JSONError", "Error al parsear categorías: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error al obtener categorías", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    etFechaFiltro.setText(formattedDate);
                    fechaFiltro = formattedDate;
                    SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    int idUsuario = prefs.getInt("id_usuario", -1);
                    ListarMovimientos(idUsuario);
                }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onClick(View v) {

    }
}