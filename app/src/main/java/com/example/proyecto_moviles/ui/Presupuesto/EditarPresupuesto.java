package com.example.proyecto_moviles.ui.Presupuesto;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.Categoria;
import com.example.proyecto_moviles.ui.Clases.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class EditarPresupuesto extends Fragment implements View.OnClickListener {
    private Button act;
    private EditText monEP;
    private EditText FiniEP, FfinEP;
    private Spinner catEP;
    private String idPresupuesto = "";
    private String modoSeleccion = "";
    private String fechaSeleccionada = "";
    private String fecha_inicio="", fecha_fin="";
    private int id_usuario = 0;
    private List<Categoria> listaCategorias;
    private ArrayAdapter<Categoria> adapterCategoria;
    SimpleDateFormat formatoMySQLEP = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat formatoDeseadoEP = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_editar_presupuesto, container, false);

        if (getArguments() != null) {
            idPresupuesto = getArguments().getString("idPresupuesto");
        }

        monEP = (EditText) rootView.findViewById(R.id.etMontoEP);
        act = (Button) rootView.findViewById(R.id.btnActualizarEP);
        FfinEP = (EditText) rootView.findViewById(R.id.etFechaFinEP);
        FiniEP = (EditText) rootView.findViewById(R.id.etFechaInicioEP);
        catEP = (Spinner) rootView.findViewById(R.id.spCategoriaEP);
        monEP.setEnabled(false);
        catEP.setEnabled(false);

        listaCategorias = new ArrayList<>();
        cargarCategoriasDesdeServidor();

        FiniEP.setOnClickListener(v -> {
            modoSeleccion = "inicio";
            Toast.makeText(getContext(),"Selecciona la nueva fecha de Incio",Toast.LENGTH_SHORT).show();
        });

        FfinEP.setOnClickListener(v -> {
            modoSeleccion = "fin";
            Toast.makeText(getContext(), "Selecciona la nueva fecha de fin", Toast.LENGTH_SHORT).show();
        });

        FiniEP.setFocusable(false);
        FiniEP.setClickable(true);

        FiniEP.setOnClickListener(new View.OnClickListener() {
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
                                FiniEP.setText(fecha_inicio);
                                selectedDate.set(year, monthOfYear, dayOfMonth);
                                fecha_inicio = formatoMySQLEP.format(selectedDate.getTime());
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        FfinEP.setFocusable(false);
        FfinEP.setClickable(true);

        FfinEP.setOnClickListener(new View.OnClickListener() {
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
                                FfinEP.setText(fecha_fin);
                                selectedDate.set(year, monthOfYear, dayOfMonth);
                                fecha_fin = formatoMySQLEP.format(selectedDate.getTime());
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });


        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        id_usuario = prefs.getInt("id_usuario", -1);
        if (id_usuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
        }

        act.setOnClickListener(this);
        return rootView;
    }

    private void cargarCategoriasDesdeServidor() {
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
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id_categoria = jsonObject.getInt("id_categoria");
                        String nombre_categoria = jsonObject.getString("nom_categoria");

                        Categoria categoria = new Categoria(id_categoria, nombre_categoria);
                        listaCategorias.add(categoria);
                    }

                    adapterCategoria = new ArrayAdapter<>( getContext(),android.R.layout.simple_spinner_item,listaCategorias);
                    adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    catEP.setAdapter(adapterCategoria);
                    ConsultarPresupuesto(idPresupuesto);

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

    private void ConsultarPresupuesto(String idPresupuesto) {
        String url = ServidorConfig.URL_SERVIDOR + "presupuestoController/consultar_presupuesto.php";
        RequestParams params = new RequestParams();
        params.put("idPresupuesto",idPresupuesto);
        AsyncHttpClient presupuesto = new AsyncHttpClient();

        presupuesto.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id_presupuesto = jsonObject.getString("id_presupuesto");
                        int id_categoria = jsonObject.getInt("id_categoria");
                        String pres_presupuesto = jsonObject.getString("pres_presupuesto");
                        String fini_presupuesto = jsonObject.getString("fini_presupuesto");
                        String ffin_presupuesto = jsonObject.getString("ffin_presupuesto");

                        Date fechaInicio = formatoMySQLEP.parse(fini_presupuesto);
                        Date fechaFin = formatoMySQLEP.parse(ffin_presupuesto);

                        monEP.setText(pres_presupuesto);
                        FfinEP.setText(formatoDeseadoEP.format(fechaFin));
                        FiniEP.setText(formatoDeseadoEP.format(fechaInicio));
                        catEP.setSelection(id_categoria-1);

                        fecha_inicio =  fini_presupuesto;
                        fecha_fin = ffin_presupuesto;
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al parsear el JSON", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == act){
            if (!validarCampos()) return;
            Categoria categoriaSeleccionada = (Categoria) catEP.getSelectedItem();
            Float montop = Float.parseFloat(monEP.getText().toString());
            Float montoActualp = Float.parseFloat(monEP.getText().toString());
            int categoriaP = categoriaSeleccionada.getId();
            String fechaInicioP = fecha_inicio;
            String fechaFinP = fecha_fin;

            ActualizarPresupuesto(idPresupuesto,id_usuario, categoriaP, montop, montoActualp, fechaInicioP, fechaFinP);
        }
    }

    private boolean validarCampos() {
        if (catEP.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return false;
        }

        String montoTexto = monEP.getText().toString().trim();
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

    private void ActualizarPresupuesto(String idPresupuesto, int idUsuario, int categoriaP, Float montoP, Float montoActualp, String fechaInicioP, String fechaFinP) {
        String url = ServidorConfig.URL_SERVIDOR + "presupuestoController/presupuesto_actualizar.php";
        RequestParams params = new RequestParams();
        params.put("idPresupuesto", idPresupuesto);
        params.put("idUsuario", idUsuario);
        params.put("categoriaP", categoriaP);
        params.put("montoP", montoP);
        params.put("montoActualp", montoActualp);
        params.put("fechaInicioP", fechaInicioP);
        params.put("fechaFinP", fechaFinP);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Toast.makeText(getActivity(), "Respuesta: " + response, Toast.LENGTH_LONG).show();

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_editarPresupuesto_to_vistaPresupuesto);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Toast.makeText(getActivity(), "Error: " + response, Toast.LENGTH_LONG).show();
            }
        });
    }
}