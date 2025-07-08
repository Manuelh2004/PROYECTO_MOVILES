package com.example.proyecto_moviles.ui.Inicio;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.google.firebase.auth.FirebaseAuth;

public class OlvidarPassword extends Fragment implements View.OnClickListener{
    private Button btnCancelar, btnEnviar;
    private EditText etEmailRecuperar;
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_olvidar_password, container, false);
        mAuth = FirebaseAuth.getInstance();

        etEmailRecuperar = (EditText) rootView.findViewById(R.id.etEmailRecuperar);
        btnEnviar = (Button) rootView.findViewById(R.id.btnIngresar);
        btnEnviar.setOnClickListener(this);
        btnCancelar = (Button) rootView.findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnIngresar) {
            enviarCorreoRestablecimiento();
        } else if (id == R.id.btnCancelar) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_olvidarPassword_to_nav_login);
        }
    }

    private void enviarCorreoRestablecimiento() {
        String email = etEmailRecuperar.getText().toString().trim();

        if (email.isEmpty()) {
            etEmailRecuperar.setError("Por favor ingresa tu correo");
            etEmailRecuperar.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailRecuperar.setError("Ingresa un correo válido");
            etEmailRecuperar.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Se envió un correo para restablecer la contraseña", Toast.LENGTH_LONG).show();
                    etEmailRecuperar.setText("");

                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.action_nav_olvidarPassword_to_nav_login);
                } else {
                    Toast.makeText(getContext(), "Error al enviar el correo, verifica que el email sea correcto", Toast.LENGTH_LONG).show();
                }
            });
    }
}