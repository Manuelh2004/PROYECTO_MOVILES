package com.example.proyecto_moviles.ui.Inicio;

import static java.security.AccessController.getContext;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.textfield.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.entity.mime.Header;

public class login_temporal extends Fragment implements View.OnClickListener{
    private Button btnIngresoDirecto, btnCrearCuenta;
    private TextInputEditText Contraseña;
    private EditText Usuario;
    final String servidor = "http://10.0.2.2/proyecto_app/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login_temporal, container, false);

        Usuario = rootView.findViewById(R.id.etUsuario);
        Contraseña = rootView.findViewById(R.id.etPassword);

        btnIngresoDirecto = (Button) rootView.findViewById(R.id.btnIngresoDirecto);
        btnIngresoDirecto.setOnClickListener(this);

        btnCrearCuenta = (Button) rootView.findViewById(R.id.btnCrearCuenta);
        btnCrearCuenta.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == btnIngresoDirecto) {
            String usuario = Usuario.getText().toString().trim();
            String contrasena = Contraseña.getText().toString().trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, complete los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = servidor + "consultar_usuario.php?em_usuario=" + usuario + "&pas_usuario=" + contrasena;

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, org.json.JSONArray response) {
                    if (response.length() > 0) {
                        // Usuario encontrado, navegar
                        Toast.makeText(getContext(), "Ingreso exitoso", Toast.LENGTH_SHORT).show();
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.action_nav_login_to_nav_presupuesto);
                    } else {
                        Toast.makeText(getContext(), "Correo y/o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, org.json.JSONObject errorResponse) {
                    Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (v == btnCrearCuenta) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_login_to_crearCuenta);
        }
    }





}


//if(v == btnIngresoDirecto){
//            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
//            navController.navigate(R.id.action_nav_login_to_nav_presupuesto);
//        }
//        if(v == btnCrearCuenta){
//            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
//            navController.navigate(R.id.action_nav_login_to_crearCuenta);
//        }