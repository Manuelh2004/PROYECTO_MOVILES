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

import com.example.proyecto_moviles.R;

import java.util.Locale;

public class PresupuestoFragment extends Fragment {

    TextView f_inicio, f_fin;
    EditText mon;
    Button agre, mos;
    Spinner cat;
    String modoSeleccion = "";
    String fechaSeleccionada = "";
    CalendarView cal;
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

            if (modoSeleccion.equals("inicio")) {
                f_inicio.setText("Inicio: " + fechaSeleccionada);
                modoSeleccion = ""; // reiniciar
            } else if (modoSeleccion.equals("fin")) {
                f_fin.setText("Fin: " + fechaSeleccionada);
                modoSeleccion = ""; // reiniciar
            } else {
                Toast.makeText(getContext(), "Toca una fecha a editar primero", Toast.LENGTH_SHORT).show();
            }
        });

        String[] elementos = {"Categoria", "Academia"};

        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>( getContext(),android.R.layout.simple_spinner_item,elementos);

        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cat.setAdapter(adapterCategoria);

        return rootView;
    }
}