package com.example.proyecto_moviles.ui.Presupuesto;

import android.content.SharedPreferences;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Categoria;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class PresupuestoFragment extends Fragment implements View.OnClickListener {

    final String servidor = "http://10.0.2.2/PHP_PROYECTO_MOVILES/controladores/presupuestoController/";
    private TextView f_inicio, f_fin;
    private EditText mon;
    private Button agre, mos;
    private Spinner cat;
    private String modoSeleccion = "";
    private String fechaSeleccionada = "";
    private CalendarView cal;
    private String fecha_inicio="", fecha_fin="";
    private int id_usuario = 0;
    private List<Categoria> listaCategorias;
    private ArrayAdapter<Categoria> adapterCategoria;

    SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    SimpleDateFormat formatoMySQL = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_presupuesto, container, false);

        f_inicio = (TextView) rootView.findViewById(R.id.tvFInicio);
        f_fin = (TextView) rootView.findViewById(R.id.tvFFin);
        mon = (EditText) rootView.findViewById(R.id.etMonto);
        agre = (Button) rootView.findViewById(R.id.btnAgregar);
        mos = (Button) rootView.findViewById(R.id.btnMostrar);
        cat = (Spinner) rootView.findViewById(R.id.spCategoria);
        cal = (CalendarView) rootView.findViewById(R.id.cvCalendario);

        // Establecer modo al tocar cada campo
        f_inicio.setOnClickListener(v -> {
            modoSeleccion = "inicio";
            Toast.makeText(getContext(),"Selecciona la nueva fecha de Incio",Toast.LENGTH_SHORT).show();
        });

        f_fin.setOnClickListener(v -> {
            modoSeleccion = "fin";
            Toast.makeText(getContext(), "Selecciona la nueva fecha de fin", Toast.LENGTH_SHORT).show();
        });

        cal.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            fechaSeleccionada = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);

            try{
                Date fecha = formatoEntrada.parse(fechaSeleccionada);

                if (modoSeleccion.equals("inicio")) {
                    f_inicio.setText("Inicio: " + fechaSeleccionada);
                    modoSeleccion = "";
                    fecha_inicio = formatoMySQL.format(fecha);// reiniciar

                } else if (modoSeleccion.equals("fin")) {
                    f_fin.setText("Fin: " + fechaSeleccionada);
                    modoSeleccion = ""; // reiniciar
                    fecha_fin = formatoMySQL.format(fecha);
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

        //String[] elementos = {"Categoria", "Academia"};

        listaCategorias = new ArrayList<>();
        cargarCategoriasDesdeServidor();

        mos.setOnClickListener(this);
        agre.setOnClickListener(this);

        return rootView;
    }

    private void cargarCategoriasDesdeServidor() {
        String url = servidor + "obtener_categorias.php";

        RequestParams params = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String

                try {
                    // Parsear el JSON recibido
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

            Categoria categoriaSeleccionada = (Categoria) cat.getSelectedItem();
            Float montoP = Float.parseFloat(mon.getText().toString());
            int categoriaP = categoriaSeleccionada.getId();
            String fechaInicioP = fecha_inicio;
            String fechaFinP = fecha_fin;

            //Toast.makeText(getContext(), "id categoria: " + categoriaP, Toast.LENGTH_SHORT).show();
            RegistrarPresupuesto(montoP, categoriaP, fechaInicioP, fechaFinP);
        }
    }

    private void RegistrarPresupuesto(Float montoP, int categoriaP, String fechaInicioP, String fechaFinP) {


        // Obtener id_usuario numérico guardado en SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        id_usuario = prefs.getInt("id_usuario", -1);
        if (id_usuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = servidor + "presupuesto_registrar.php";

        RequestParams params = new RequestParams();
        params.put("id_usuario", id_usuario);
        params.put("montoP", montoP);
        params.put("categoriaP", categoriaP);
        params.put("fechaInicioP", fechaInicioP);
        params.put("fechaFinP", fechaFinP);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String
                Toast.makeText(getActivity(), "Respuesta: " + response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String
                Toast.makeText(getActivity(), "Error: " + response, Toast.LENGTH_LONG).show();
            }
        });
    }
}