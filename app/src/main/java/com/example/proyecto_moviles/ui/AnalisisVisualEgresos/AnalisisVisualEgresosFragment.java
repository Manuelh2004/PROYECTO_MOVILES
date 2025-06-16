package com.example.proyecto_moviles.ui.AnalisisVisualEgresos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.databinding.FragmentAnalisisVisualEgresosBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class AnalisisVisualEgresosFragment extends Fragment {

    private BarChart barChartEgresosMensuales;
    private PieChart pieChartEgresos;
    private FragmentAnalisisVisualEgresosBinding binding;
    private LinearLayout leyendaPersonalizada;

    private String servidor = "http://10.0.2.2/proyecto_moviles/controladores/EgresosController/";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnalisisVisualEgresosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        barChartEgresosMensuales = root.findViewById(R.id.barChartEgresosMensuales);
        pieChartEgresos = root.findViewById(R.id.pieChartEgresos);

        obtenerDatosEgresos();

        binding.btnActualizarGraficos.setOnClickListener(v -> obtenerDatosEgresos()); // Actualizar gráficos al presionar el botón
        leyendaPersonalizada = root.findViewById(R.id.leyendaPersonalizada);

        return root;
    }
    private void obtenerDatosEgresos() {
        // Leer ID del usuario desde SharedPreferences (misma forma que en EditarPresupuesto)
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        // Mostrar el ID en un Toast
        Toast.makeText(getActivity(), "ID Usuario actual: " + idUsuario, Toast.LENGTH_SHORT).show();

        // También puedes mostrarlo en Logcat para debugging
        android.util.Log.d("AnalisisVisualEgresos", "ID Usuario leído: " + idUsuario);

        if (idUsuario == -1) {
            Toast.makeText(getActivity(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = servidor + "obtener_egresos.php?id_usuario=" + idUsuario;
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    HashMap<String, Float> datosAgrupados = new HashMap<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String categoria = obj.getString("categoria");
                        float monto = (float) obj.getDouble("monto");

                        datosAgrupados.put(categoria, datosAgrupados.getOrDefault(categoria, 0f) + monto);
                    }

                    List<Map.Entry<String, Float>> listaOrdenada = new ArrayList<>(datosAgrupados.entrySet());
                    listaOrdenada.sort((e1, e2) -> Float.compare(e2.getValue(), e1.getValue()));

                    ArrayList<BarEntry> barEntries = new ArrayList<>();
                    ArrayList<String> barLabels = new ArrayList<>();
                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
                    ArrayList<Integer> colores = new ArrayList<>();
                    Random random = new Random();

                    for (int i = 0; i < listaOrdenada.size(); i++) {
                        String categoria = listaOrdenada.get(i).getKey();
                        float monto = listaOrdenada.get(i).getValue();

                        barEntries.add(new BarEntry(i, monto));
                        pieEntries.add(new PieEntry(monto, categoria));
                        barLabels.add(categoria);
                        colores.add(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                    }

                    // TÍTULOS
                    binding.tvTituloPie.setVisibility(View.VISIBLE);
                    binding.tvTituloBar.setVisibility(View.VISIBLE);

                    // Configurar PieChart
                    PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
                    pieDataSet.setColors(colores);
                    pieDataSet.setValueTextSize(16f);
                    pieDataSet.setValueTextColor(Color.BLACK);
                    pieDataSet.setSliceSpace(3f);

                    PieData pieData = new PieData(pieDataSet);
                    pieData.setValueFormatter(new com.github.mikephil.charting.formatter.PercentFormatter(pieChartEgresos));

                    pieChartEgresos.setUsePercentValues(true);
                    pieChartEgresos.setData(pieData);
                    pieChartEgresos.getDescription().setEnabled(false);
                    pieChartEgresos.getLegend().setEnabled(false); // Desactivar leyenda nativa
                    pieChartEgresos.setExtraBottomOffset(10f);
                    pieChartEgresos.animateY(1000);
                    pieChartEgresos.invalidate();

                    // Configurar BarChart
                    List<IBarDataSet> barDataSets = new ArrayList<>();
                    for (int i = 0; i < barEntries.size(); i++) {
                        BarEntry entry = barEntries.get(i);
                        String categoria = barLabels.get(i);

                        List<BarEntry> singleEntry = new ArrayList<>();
                        singleEntry.add(new BarEntry(entry.getX(), entry.getY()));

                        BarDataSet singleSet = new BarDataSet(singleEntry, categoria);
                        singleSet.setColor(colores.get(i));
                        singleSet.setValueTextColor(Color.BLACK);
                        singleSet.setValueTextSize(16f);

                        barDataSets.add(singleSet);
                    }

                    BarData barData = new BarData(barDataSets);
                    barData.setBarWidth(0.9f);

                    barChartEgresosMensuales.setData(barData);
                    barChartEgresosMensuales.setFitBars(true);
                    barChartEgresosMensuales.getDescription().setEnabled(false);
                    barChartEgresosMensuales.getLegend().setEnabled(false); // Desactivar leyenda nativa
                    barChartEgresosMensuales.getXAxis().setValueFormatter(new IndexAxisValueFormatter(barLabels));
                    barChartEgresosMensuales.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    barChartEgresosMensuales.getXAxis().setGranularity(1f);
                    barChartEgresosMensuales.getXAxis().setGranularityEnabled(true);
                    barChartEgresosMensuales.getAxisLeft().setAxisMinimum(0f);
                    barChartEgresosMensuales.getAxisRight().setEnabled(false);
                    barChartEgresosMensuales.animateY(1000);
                    barChartEgresosMensuales.invalidate();

                    barChartEgresosMensuales.getXAxis().setLabelRotationAngle(-35f); // etiquetas


                    // LEYENDA PERSONALIZADA
                    binding.leyendaPersonalizada.removeAllViews();
                    for (int i = 0; i < barLabels.size(); i++) {
                        LinearLayout itemLayout = new LinearLayout(getContext());
                        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                        itemLayout.setPadding(8, 8, 8, 8);

                        View colorBox = new View(getContext());
                        LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(40, 40);
                        colorParams.setMargins(0, 0, 16, 0);
                        colorBox.setLayoutParams(colorParams);
                        colorBox.setBackgroundColor(colores.get(i));

                        TextView label = new TextView(getContext());
                        label.setText(barLabels.get(i));
                        label.setTextSize(16f);
                        label.setTextColor(Color.BLACK);

                        itemLayout.addView(colorBox);
                        itemLayout.addView(label);
                        binding.leyendaPersonalizada.addView(itemLayout);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}