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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListarMovimientos#newInstance} factory method to
 * create an instance of this fragment.
 */


public class ListarMovimientos extends Fragment implements View.OnClickListener{

    final String servidor = "http://10.0.2.2/proyecto_moviles/controladores/";
    ListView listaMovimientos;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Spinner spCategoriaFiltro;
    EditText etFechaFiltro;
    private String fechaFiltro = null, categoriaFiltro = null;
    public class MovimientoAdapter extends BaseAdapter {

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

            tvId.setText("ID: " + mov.id_movimiento);
            tvUsuario.setText("Usuario: " + mov.usuario);
            tvTipo.setText("Tipo: " + mov.tipo_movimiento);
            tvCategoria.setText("Categoría: " + mov.categoria);
            tvMonto.setText("Monto: " + mov.monto);
            tvFecha.setText("Fecha: " + mov.fecha);
            tvDescripcion.setText("Descripción: " + mov.descripcion);
            tvEstado.setText("Estado: " + (mov.estado.equals("1") ? "Activo" : "Inactivo"));
            editar.setOnClickListener(v -> EditarMovimiento(mov.id_movimiento));
            eliminar.setOnClickListener(v -> EliminarMovimiento(mov.id_movimiento));
            return convertView;
        }
    }

    private void showConfirmationDialog(String idMovimiento) {
        // Crear un cuadro de diálogo de confirmación
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este movimiento?")
                .setPositiveButton("Eliminar", (dialog, which) -> EliminarMovimiento(idMovimiento))
                .setNegativeButton("Cancelar", null)
                .show();
    }
    @SuppressLint("NotConstructor")
    private void ListarMovimientos(int idUsuario) {
        String url = servidor + "movimientoController/mostrar_movimiento.php"; // Asegúrate de que esta URL esté correcta
        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);

        // Filtros de fecha
        if (fechaFiltro != null) {
            params.put("fecha_inicio", fechaFiltro);
            params.put("fecha_fin", fechaFiltro);
        }

        // Filtro de categoría
        if (categoriaFiltro != null && !categoriaFiltro.isEmpty()) {
            params.put("id_categoria", categoriaFiltro);  // Asegúrate de que esto esté aquí
        }

        // Verificar que los parámetros estén bien construidos
        Log.d("Parametros GET", params.toString());  // Este log te ayudará a verificar los parámetros enviados

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
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

                    // Adapter para los movimientos
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


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListarPrespuestos.
     */
    // TODO: Rename and change types and number of parameters
    public static ListarMovimientos newInstance(String param1, String param2) {
        ListarMovimientos fragment = new ListarMovimientos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listar_movimientos, container, false);

        listaMovimientos = rootView.findViewById(R.id.lstMovimientos);
        spCategoriaFiltro = rootView.findViewById(R.id.spCategoriaFiltro);
        etFechaFiltro = rootView.findViewById(R.id.etFechaFiltro);

        etFechaFiltro.setOnClickListener(v -> openDatePicker());
        // Obtener el id_usuario desde SharedPreferences
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
                ListarMovimientos(idUsuario);  // Llama a la función para actualizar la lista con el filtro aplicado
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Opcional: manejar el caso de "ninguna categoría seleccionada"
            }
        });

        return rootView;
    }

    private void EliminarMovimiento(String idMovimiento) {
        // Asegurarse de obtener el contexto correcto (usando getContext() o getActivity())
        Context context = getActivity(); // Asegúrate de que esto no sea nulo

        if (context != null) {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar Movimiento")
                    .setMessage("¿Estás seguro de que deseas eliminar este movimiento?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        // Llamar al método para eliminar el movimiento
                        eliminarMovimiento(idMovimiento);
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        // Si el usuario cancela, no hacer nada
                        dialog.dismiss();
                    })
                    .show();
        } else {
            // Si el contexto es nulo, mostrar un error
            Toast.makeText(getActivity(), "Error: Contexto no disponible", Toast.LENGTH_SHORT).show();
        }
    }
    private void eliminarMovimiento(String idMovimiento) {
        String url = servidor + "movimientoController/eliminar_movimiento.php";
        RequestParams params = new RequestParams();
        params.put("id_movimiento", idMovimiento);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.contains("success")) {
                    Toast.makeText(getActivity(), "Movimiento eliminado correctamente", Toast.LENGTH_SHORT).show();
                    // Después de eliminar, puedes actualizar la lista de movimientos
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
        bundle.putString("id_movimiento", idMovimiento);  // Pasa el id_movimiento

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_nav_listar_movimientos_to_nav_editar_movimiento, bundle);
    }

    private void cargarCategoriasFiltro(int idUsuario) {

        // Verificar si el id_usuario es válido
        if (idUsuario == -1) {
            Log.e("Error", "id_usuario no válido");
            Toast.makeText(getActivity(), "Error: No se ha encontrado el ID de usuario", Toast.LENGTH_SHORT).show();
            return;  // No continuar con la solicitud si id_usuario no es válido
        }

        // Si id_usuario es válido, continuamos con la solicitud
        String url = servidor + "itemsController/obtener_categoria_presupuesto_mov.php";  // URL correcta
        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);  // El id del usuario se obtiene de SharedPreferences correctamente

        // Verificar que los parámetros estén bien construidos
        Log.d("Parametros de la solicitud", "id_usuario: " + idUsuario);  // Este log te ayudará a verificar el valor de id_usuario

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    // Convertir el cuerpo de la respuesta a String
                    String responseString = new String(responseBody);
                    Log.d("Respuesta API", responseString);  // Esto te ayudará a verificar qué estás recibiendo

                    // Procesar la respuesta JSON como un JSONArray, no JSONObject
                    JSONArray jsonArray = new JSONArray(responseString);  // Aquí cambiamos a JSONArray
                    List<String> categorias = new ArrayList<>();
                    List<Integer> categoriasIds = new ArrayList<>();

                    // Añadir la opción por defecto (Seleccionar categoría)
                    categorias.add("Seleccionar categoría");
                    categoriasIds.add(null);  // Usamos null para la opción por defecto

                    // Recoger las categorías y sus IDs
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        categorias.add(obj.getString("nombre"));
                        categoriasIds.add(obj.getInt("id_categoria"));
                    }

                    // Adaptador para el Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categorias);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCategoriaFiltro.setAdapter(adapter);

                    // Establecer el listener para que, al seleccionar una categoría, actualice el filtro
                    spCategoriaFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            categoriaFiltro = position > 0 ? String.valueOf(categoriasIds.get(position)) : null;
                            Log.d("Categoria Seleccionada", "ID Categoría: " + categoriaFiltro);
                            ListarMovimientos(idUsuario);  // Llamamos a la función para actualizar la lista con el filtro
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            categoriaFiltro = null;  // Si no se selecciona nada, dejamos el filtro vacío
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al cargar las categorías", Toast.LENGTH_SHORT).show();
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
                    String formattedDate = String.format("%02d/%02d/%02d", selectedDay, selectedMonth + 1, selectedYear % 100);
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