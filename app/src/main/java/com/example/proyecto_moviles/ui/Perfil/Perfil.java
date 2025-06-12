package com.example.proyecto_moviles.ui.Perfil;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.proyecto_moviles.R;

public class Perfil extends Fragment implements View.OnClickListener{
    private Button btnEditar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil, container, false);

        btnEditar = (Button) rootView.findViewById(R.id.btnEditar);
        btnEditar.setOnClickListener(this);

        return rootView;     }

    @Override
    public void onClick(View v) {
        if (v == btnEditar){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_perfil_to_editarPerfil);
        }
    }
}