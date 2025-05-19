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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class VerificacionEmail extends Fragment implements View.OnClickListener{
    private EditText etCodigo;
    private Button btnValidarCodigo, btnReenviarCodigo;
    private String servidor = "http://10.0.2.2/proyecto_moviles/controladores/";
    private String emailUsuario; // Debes recibir el email para validar o reenviar código

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_verificacion_email, container, false);

        etCodigo = rootView.findViewById(R.id.etCodigo);
        btnValidarCodigo = rootView.findViewById(R.id.btnValidarCodigo);
        btnReenviarCodigo = rootView.findViewById(R.id.btnReenviarCodigo);

        btnValidarCodigo.setOnClickListener(this);
        btnReenviarCodigo.setOnClickListener(this);

        if (getArguments() != null) {
            emailUsuario = getArguments().getString("email");
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == btnValidarCodigo) {
            String codigoIngresado = etCodigo.getText().toString().trim();

            if (codigoIngresado.isEmpty()) {
                Toast.makeText(getContext(), "Ingresa el código recibido en el correo", Toast.LENGTH_SHORT).show();
                return;
            }

            validarCodigo(emailUsuario, codigoIngresado);

        } else if (v == btnReenviarCodigo) {
            reenviarCodigo(emailUsuario);
        }
    }
    private void validarCodigo(String email, String codigo) {
        String url = servidor + "usuarioController/validar_codigo.php";

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("codigo", codigo);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    boolean exito = response.getBoolean("exito");
                    String mensaje = response.getString("mensaje");
                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();

                    if (exito) {
                        // Navegar a siguiente pantalla, ejemplo login
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.action_verificacionEmail_to_nav_presupuesto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reenviarCodigo(String email) {
        String url = servidor + "usuarioController/reenviar_codigo.php";

        RequestParams params = new RequestParams();
        params.put("email", email);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    boolean exito = response.getBoolean("exito");
                    String mensaje = response.getString("mensaje");
                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}