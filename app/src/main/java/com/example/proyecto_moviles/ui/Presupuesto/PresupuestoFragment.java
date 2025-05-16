package com.example.proyecto_moviles.ui.Presupuesto;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.proyecto_moviles.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.text.ParseException;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class PresupuestoFragment extends Fragment implements View.OnClickListener {

    final String servidor = "http://10.0.2.2/presupuesto/";
    private TextView f_inicio, f_fin;
    private EditText mon;
    private Button agre, mos;
    private Spinner cat;
    private String modoSeleccion = "";
    private String fechaSeleccionada = "";
    private CalendarView cal;
    private String fecha_inicio="", fecha_fin="";

    SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    SimpleDateFormat formatoMySQL = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_presupuesto, container, false);

        f_inicio = (TextView) rootView.findViewById(R.id.tvFInicio);
        f_fin = (TextView) rootView.findViewById(R.id.tvFFin);
        mon = (EditText) rootView.findViewById(R.id.etMonto);
        agre = (Button) rootView.findViewById(R.id.btnAñadir);
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

        String[] elementos = {"Categoria", "Academia"};

        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>( getContext(),android.R.layout.simple_spinner_item,elementos);

        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cat.setAdapter(adapterCategoria);

        mos.setOnClickListener(this);
        agre.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v == mos){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_presupuesto_to_vistaPresupuesto);
        }else if (v == agre){

            Float montoP = Float.parseFloat(mon.getText().toString());
            String categoriaP = cat.getSelectedItem().toString();
            String fechaInicioP = fecha_inicio;
            String fechaFinP = fecha_fin;

            RegistrarPresupuesto(montoP, categoriaP, fechaInicioP, fechaFinP);
        }
    }

    private void RegistrarPresupuesto(Float montoP, String categoriaP, String fechaInicioP, String fechaFinP) {

        String url = servidor + "presupuesto_registrar.php";

        RequestParams params = new RequestParams();
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