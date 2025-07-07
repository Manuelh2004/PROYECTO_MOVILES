package com.example.proyecto_moviles.ui.Administrador;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.ServidorConfig;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import cz.msebera.android.httpclient.Header;

public class Administrador extends Fragment implements View.OnClickListener {
    Button btnCofiguracionItems, btnVisualizacionComentarios, btnGestionUsuarios;
    private FrameLayout graphContainer;
    private BarChart barChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_administrador, container, false);

        btnCofiguracionItems = rootView.findViewById(R.id.btnCofiguracionItems);
        btnVisualizacionComentarios = rootView.findViewById(R.id.btnVisualizacionComentarios);
        btnGestionUsuarios = rootView.findViewById(R.id.btnGestionUsuarios);
        btnCofiguracionItems.setOnClickListener(this);
        btnVisualizacionComentarios.setOnClickListener(this);
        btnGestionUsuarios.setOnClickListener(this);

        graphContainer = rootView.findViewById(R.id.graphContainer);
        barChart = new BarChart(getContext());
        graphContainer.addView(barChart);

        cargarGraficoLogin();
        return rootView;
    }

    private void cargarGraficoLogin() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = ServidorConfig.URL_SERVIDOR + "AdministradorController/cantidad_logins.php";

        client.get(url, new RequestParams(), new AsyncHttpResponseHandler() {
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

                    BarDataSet dataSet = new BarDataSet(entries, "Inicios de sesión");
                    dataSet.setColor(getResources().getColor(R.color.teal_700));
                    BarData barData = new BarData(dataSet);
                    barChart.setData(barData);

                    // Configuración visual
                    barChart.getDescription().setEnabled(false);
                    barChart.getAxisRight().setEnabled(false);

                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setGranularity(1f);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            int index = (int) value;
                            return (index >= 0 && index < labels.size()) ? labels.get(index) : "";
                        }
                    });

                    barChart.invalidate(); // Redibuja

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar datos del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        if (v == btnCofiguracionItems) {
            navController.navigate(R.id.action_nav_administrador_to_nav_item);
        } else if (v == btnVisualizacionComentarios) {
            navController.navigate(R.id.action_nav_administrador_to_nav_comentario);
        } else if (v == btnGestionUsuarios) {
            navController.navigate(R.id.action_nav_administrador_to_nav_usuario);
        }
    }
}
