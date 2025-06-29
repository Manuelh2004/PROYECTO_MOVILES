package com.example.proyecto_moviles.ui.Administrador;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Categoria;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Administrador extends Fragment implements View.OnClickListener{
    Button btnCofiguracionItems, btnVisualizacionComentarios, btnGestionUsuarios, btnConfiguracionNotificaciones;

    private FrameLayout graphContainer;

    final String servidor = "http://10.0.2.2/proyecto_moviles/controladores/AdministradorController/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_administrador, container, false);

        btnCofiguracionItems = rootView.findViewById(R.id.btnCofiguracionItems);
        btnCofiguracionItems.setOnClickListener(this);
        btnVisualizacionComentarios = rootView.findViewById(R.id.btnVisualizacionComentarios);
        btnVisualizacionComentarios.setOnClickListener(this);
        btnGestionUsuarios = rootView.findViewById(R.id.btnGestionUsuarios);
        btnGestionUsuarios.setOnClickListener(this);

        graphContainer = rootView.findViewById(R.id.graphContainer);

        BarChart barChart = new BarChart(getContext());

        // Simulación de datos desde la base de datos
        /*List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 4)); // Día 1: 4 logins
        entries.add(new BarEntry(1, 6)); // Día 2: 6 logins
        entries.add(new BarEntry(2, 2)); // Día 3: 2 logins

        BarDataSet dataSet = new BarDataSet(entries, "Inicios de sesión");
        BarData barData = new BarData(dataSet);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getAxisRight().setEnabled(false);
        barChart.invalidate(); // Refresca el gráfico*/

        // Agrega el gráfico al FrameLayout
        graphContainer.addView(barChart);

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();

        client.get(servidor + "cantidad_logins.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody, "UTF-8");
                    JSONArray response = new JSONArray(json);

                    List<BarEntry> entries = new ArrayList<>();
                    List<String> labels = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        String dia = obj.getString("dia");
                        int cantidad = obj.getInt("cantidad");

                        entries.add(new BarEntry(i, cantidad));
                        labels.add(dia);
                    }

                    // Crear el gráfico
                    BarChart barChart = new BarChart(getContext());

                    BarDataSet dataSet = new BarDataSet(entries, "Inicios de sesión");
                    dataSet.setColor(getResources().getColor(R.color.teal_700)); // Puedes personalizarlo

                    BarData barData = new BarData(dataSet);
                    barChart.setData(barData);

                    barChart.getDescription().setEnabled(false);
                    barChart.getAxisRight().setEnabled(false);

                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setGranularity(1f);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            int index = (int) value;
                            if (index >= 0 && index < labels.size()) {
                                return labels.get(index);
                            } else {
                                return "";
                            }
                        }
                    });

                    barChart.invalidate(); // Redibuja el gráfico

                    // Agrega el gráfico al contenedor
                    graphContainer.removeAllViews(); // Por si hay otro gráfico antes
                    graphContainer.addView(barChart);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar datos del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v == btnCofiguracionItems){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_administrador_to_nav_item);
        }
        if(v == btnVisualizacionComentarios){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_administrador_to_nav_comentario);
        }
        if(v == btnGestionUsuarios){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_administrador_to_nav_usuario);
        }

    }
}