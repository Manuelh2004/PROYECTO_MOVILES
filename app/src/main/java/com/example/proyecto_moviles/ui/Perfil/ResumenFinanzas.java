package com.example.proyecto_moviles.ui.Perfil;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.proyecto_moviles.MonedaViewModel;
import com.example.proyecto_moviles.ui.Movimiento.AgregarMovimiento;
import com.example.proyecto_moviles.ui.Presupuesto.PresupuestoFragment;
import com.example.proyecto_moviles.MainActivity;
import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.databinding.FragmentResumenFinanzasBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import androidx.navigation.NavOptions;

import cz.msebera.android.httpclient.Header;

public class ResumenFinanzas extends Fragment {

    private FragmentResumenFinanzasBinding binding;
    private MonedaViewModel monedaViewModel;
    private final String URL = "http://10.0.2.2/proyecto_moviles/controladores/ResumenController/funcion_resumen.php";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentResumenFinanzasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        obtenerResumenFinanzas();
        mostrarGraficoPresupuestos();

        // Ir a MovimientoFragment manualmente
        binding.btnIrMovimiento.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.nav_resumen_finanzas, true)
                    .build();
            navController.navigate(R.id.nav_movimiento, null, navOptions);
        });

        // Ir a PresupuestoFragment manualmente
        binding.btnIrPresupuesto.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.nav_resumen_finanzas, true)
                    .build();
            navController.navigate(R.id.nav_presupuesto, null, navOptions);
        });

        return root;
    }

    private void obtenerResumenFinanzas() {
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getActivity(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = URL + "?id_usuario=" + idUsuario;
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    if (jsonArray.length() > 0) {
                        JSONObject json = jsonArray.getJSONObject(0);

                        double ingresos = json.getDouble("total_ingresos");
                        double egresos = json.getDouble("total_egresos");
                        double saldo = json.getDouble("saldo_disponible");

                        MonedaViewModel monedaViewModel = new ViewModelProvider(requireActivity()).get(MonedaViewModel.class);
                        monedaViewModel.getMostrarEnDolares().observe(getViewLifecycleOwner(), mostrarEnDolares -> {
                            String simbolo = mostrarEnDolares ? "$ " : "S/ ";

                            binding.tvUltimoIngreso.setText("Total Ingresos: " + simbolo + ingresos);
                            binding.tvUltimoEgreso.setText("Total Egresos: " + simbolo + egresos);
                            binding.tvTotal.setText("Saldo Disponible: " + simbolo + saldo);
                        });
                    } else {
                        Toast.makeText(getActivity(), "No hay datos disponibles", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarGraficoPresupuestos() {
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) return;

        String url = "http://10.0.2.2/proyecto_moviles/controladores/ResumenController/obtener_presupuestos_grafica.php?id_usuario=" + idUsuario;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));

                    ArrayList<PieEntry> entries = new ArrayList<>();
                    ArrayList<Integer> colores = new ArrayList<>();
                    Random random = new Random();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String categoria = obj.getString("categoria");
                        float monto = (float) obj.getDouble("monto");

                        entries.add(new PieEntry(monto, categoria));
                        colores.add(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                    }

                    PieDataSet dataSet = new PieDataSet(entries, "");
                    dataSet.setColors(colores);
                    dataSet.setValueTextSize(14f);
                    dataSet.setValueTextColor(Color.BLACK);
                    dataSet.setSliceSpace(3f);

                    PieData pieData = new PieData(dataSet);
                    pieData.setValueFormatter(new PercentFormatter(binding.pieChartPresupuestos));

                    binding.pieChartPresupuestos.setUsePercentValues(true);
                    binding.pieChartPresupuestos.setData(pieData);
                    binding.pieChartPresupuestos.getDescription().setEnabled(false);
                    binding.pieChartPresupuestos.getLegend().setEnabled(false);
                    binding.pieChartPresupuestos.animateY(1000);
                    binding.pieChartPresupuestos.invalidate();

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error al procesar datos del gráfico", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error al obtener datos de presupuesto", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

