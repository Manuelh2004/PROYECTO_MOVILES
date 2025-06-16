package com.example.proyecto_moviles.ui.Administrador;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.proyecto_moviles.R;

public class Administrador extends Fragment implements View.OnClickListener{
    Button btnCofiguracionItems, btnVisualizacionComentarios, btnGestionUsuarios, btnConfiguracionNotificaciones;

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
        btnConfiguracionNotificaciones = rootView.findViewById(R.id.btnConfiguracionNotificaciones);
        btnConfiguracionNotificaciones.setOnClickListener(this);


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
        if(v == btnConfiguracionNotificaciones){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_administrador_to_nav_usuario);
        }

    }
}