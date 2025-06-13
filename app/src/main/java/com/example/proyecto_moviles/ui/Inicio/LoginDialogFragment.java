package com.example.proyecto_moviles.ui.Inicio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto_moviles.R;

public class LoginDialogFragment extends DialogFragment {
    private EditText etEmail, etPassword;
    private Button btnAceptar, btnCancelar;
    private LoginDialogListener listener;

    public interface LoginDialogListener {
        void onLoginDataEntered(String email, String password);
    }

    public void setLoginDialogListener(LoginDialogListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_login, container, false);

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnAceptar = view.findViewById(R.id.btnIngresar);
        btnCancelar = view.findViewById(R.id.btnCancelar);

        btnAceptar.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            listener.onLoginDataEntered(email, password);
            dismiss();
        });

        btnCancelar.setOnClickListener(v -> dismiss());

        return view;
    }
}
