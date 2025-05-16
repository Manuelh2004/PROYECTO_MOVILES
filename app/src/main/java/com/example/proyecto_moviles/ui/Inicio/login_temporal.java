package com.example.proyecto_moviles.ui.Inicio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.proyecto_moviles.R;

public class login_temporal extends Fragment implements View.OnClickListener{
    private Button btnIngresoDirecto, btnCrearCuenta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login_temporal, container, false);

        btnIngresoDirecto = (Button) rootView.findViewById(R.id.btnIngresoDirecto);
        btnIngresoDirecto.setOnClickListener(this);

        btnCrearCuenta = (Button) rootView.findViewById(R.id.btnCrearCuenta);
        btnCrearCuenta.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v == btnIngresoDirecto){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_login_to_nav_presupuesto);
        }
        if(v == btnCrearCuenta){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_login_to_crearCuenta);
        }
    }
}