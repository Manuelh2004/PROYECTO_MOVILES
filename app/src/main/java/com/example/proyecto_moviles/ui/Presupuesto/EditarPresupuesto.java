package com.example.proyecto_moviles.ui.Presupuesto;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Categoria;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class EditarPresupuesto extends Fragment implements View.OnClickListener {

    private Button act;
    private EditText monEP;
    private TextView FiniEP, FfinEP;
    private CalendarView calenderEP;
    private Spinner catEP;
    private String idPresupuesto = "";
    final String servidor = "http://10.0.2.2/PHP_PROYECTO_MOVILES/controladores/presupuestoController/";
    private String modoSeleccion = "";
    private String fechaSeleccionada = "";
    private String fecha_inicio="", fecha_fin="";
    private int id_usuario = 1;
    SimpleDateFormat formatoMySQLEP = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat formatoDeseadoEP = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_editar_presupuesto, container, false);

        if (getArguments() != null) {
            idPresupuesto = getArguments().getString("idPresupuesto");
            Toast.makeText(getContext(), "ID recibido: " + idPresupuesto, Toast.LENGTH_SHORT).show();
        }

        monEP = (EditText) rootView.findViewById(R.id.etMontoEP);
        act = (Button) rootView.findViewById(R.id.btnActualizarEP);
        FfinEP = (TextView) rootView.findViewById(R.id.tvFfinEP);
        FiniEP = (TextView) rootView.findViewById(R.id.tvFIniEP);
        calenderEP = (CalendarView) rootView.findViewById(R.id.cvCalendarioEP);
        catEP = (Spinner) rootView.findViewById(R.id.spCategoriaEP);

        List<Categoria> listaCategorias = new ArrayList<>();
        listaCategorias.add(new Categoria(0, "Todos las Categorias"));
        listaCategorias.add(new Categoria(1, "Alimentacion"));
        listaCategorias.add(new Categoria(2, "Transporte"));
        listaCategorias.add(new Categoria(3, "Salud"));

        ArrayAdapter<Categoria> adapterCategoria = new ArrayAdapter<>( getContext(),android.R.layout.simple_spinner_item,listaCategorias);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catEP.setAdapter(adapterCategoria);

        // Establecer modo al tocar cada campo
        FiniEP.setOnClickListener(v -> {
            modoSeleccion = "inicio";
            Toast.makeText(getContext(),"Selecciona la nueva fecha de Incio",Toast.LENGTH_SHORT).show();
        });

        FfinEP.setOnClickListener(v -> {
            modoSeleccion = "fin";
            Toast.makeText(getContext(), "Selecciona la nueva fecha de fin", Toast.LENGTH_SHORT).show();
        });

        calenderEP.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            fechaSeleccionada = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);

            try{
                Date fecha = formatoDeseadoEP.parse(fechaSeleccionada);

                if (modoSeleccion.equals("inicio")) {
                    FiniEP.setText("Inicio: " + fechaSeleccionada);
                    modoSeleccion = "";
                    fecha_inicio = formatoMySQLEP.format(fecha);// reiniciar

                } else if (modoSeleccion.equals("fin")) {
                    FfinEP.setText("Fin: " + fechaSeleccionada);
                    modoSeleccion = ""; // reiniciar
                    fecha_fin = formatoMySQLEP.format(fecha);
                } else {
                    Toast.makeText(getContext(), "Toca una fecha a editar primero", Toast.LENGTH_SHORT).show();

                    //prueba de valores de fecha inicio y fin
                    //Toast.makeText(getContext(), "Inicio: "+fecha_inicio +" // " +fecha_fin, Toast.LENGTH_SHORT).show();
                }
            }catch (ParseException e){
                e.printStackTrace();
                Toast.makeText(getContext(), "Error al convertir la fecha", Toast.LENGTH_SHORT).show();
            }

        });

        ConsultarPresupuesto(idPresupuesto);

        act.setOnClickListener(this);
        return rootView;
    }

    private void ConsultarPresupuesto(String idPresupuesto) {
        // Crear la URL para hacer la solicitud
        String url = servidor + "consultar_presupuesto.php";

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();
        params.put("idPresupuesto",idPresupuesto);

        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient presupuesto = new AsyncHttpClient();

        presupuesto.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String

                try {
                    // Parsear el JSON recibido
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

                        //Colocar datos en los EditText
                        monEP.setText(pres_presupuesto);
                        FfinEP.setText("Fin: " + formatoDeseadoEP.format(fechaFin));
                        FiniEP.setText("Inicio: " + formatoDeseadoEP.format(fechaInicio));
                        catEP.setSelection(id_categoria);

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
            Categoria categoriaSeleccionada = (Categoria) catEP.getSelectedItem();
            Float montop = Float.parseFloat(monEP.getText().toString());
            int categoriaP = categoriaSeleccionada.getId();
            String fechaInicioP = fecha_inicio;
            String fechaFinP = fecha_fin;

            ActualizarPresupuesto(idPresupuesto,id_usuario, categoriaP, montop, fechaInicioP, fechaFinP);
        }
    }

    private void ActualizarPresupuesto(String idPresupuesto, int idUsuario, int categoriaP, Float montoP, String fechaInicioP, String fechaFinP) {
        String url = servidor + "presupuesto_actualizar.php";

        RequestParams params = new RequestParams();
        params.put("idPresupuesto", idPresupuesto);
        params.put("idUsuario", idUsuario);
        params.put("categoriaP", categoriaP);
        params.put("montoP", montoP);
        params.put("fechaInicioP", fechaInicioP);
        params.put("fechaFinP", fechaFinP);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String
                Toast.makeText(getActivity(), "Respuesta: " + response, Toast.LENGTH_LONG).show();

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_editarPresupuesto_to_vistaPresupuesto);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String
                Toast.makeText(getActivity(), "Error: " + response, Toast.LENGTH_LONG).show();
            }
        });
    }
}