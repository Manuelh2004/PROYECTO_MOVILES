package com.example.proyecto_moviles.ui.Inicio;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.ServidorConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class VerificacionEmail extends Fragment implements View.OnClickListener{
    private Button btnRevisarVerificacion, btnReenviarCorreo;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String nombres, apellidos, telefono, documento, fechaNa, email;
    private int idGenero, idTipoDoc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_verificacion_email, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        btnRevisarVerificacion = rootView.findViewById(R.id.btnRevisarVerificacion);
        btnReenviarCorreo = rootView.findViewById(R.id.btnReenviarCorreo);
        btnRevisarVerificacion.setOnClickListener(this);
        btnReenviarCorreo.setOnClickListener(this);

        Bundle args = getArguments();
        if (args != null) {
            nombres = args.getString("nombres");
            apellidos = args.getString("apellidos");
            telefono = args.getString("telefono");
            documento = args.getString("documento");
            fechaNa = args.getString("fechaNa");
            idGenero = args.getInt("idGenero", -1);
            idTipoDoc = args.getInt("idTipoDoc", -1);
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == btnRevisarVerificacion) {
            if (currentUser == null) {
                Toast.makeText(getContext(), "Usuario no autenticado.", Toast.LENGTH_SHORT).show();
                return;
            }
            currentUser.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (currentUser.isEmailVerified()) {
                        Toast.makeText(getContext(), "Correo verificado. Creando cuenta...", Toast.LENGTH_SHORT).show();

                        email = currentUser.getEmail();
                        String uid = currentUser.getUid();
                        // Llamar al método para registrar en BD
                        RegistrarUsuario(nombres, apellidos, telefono, documento, fechaNa, idGenero, idTipoDoc, email, uid);
                    } else {
                        Toast.makeText(getContext(), "Correo no verificado aún. Revisa tu bandeja.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error al refrescar estado del usuario.", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (v == btnReenviarCorreo) {
            if (currentUser == null) {
                Toast.makeText(getContext(), "Usuario no autenticado.", Toast.LENGTH_SHORT).show();
                return;
            }
            currentUser.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Correo de verificación reenviado.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al reenviar correo.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void RegistrarUsuario(String nombres, String apellidos, String telefono, String documento, String fechaNa,
                                  int idGenero, int idTipoDoc, String email, String uidFirebase) {
        String url = ServidorConfig.URL_SERVIDOR + "usuarioController/crear_usuario.php";
        RequestParams params = new RequestParams();
        params.put("nombres", nombres);
        params.put("apellidos", apellidos);
        params.put("telefono", telefono);
        params.put("documento", documento);
        params.put("fechaNa", fechaNa);
        params.put("idGenero", idGenero);
        params.put("idTipoDoc", idTipoDoc);
        params.put("email", email);
        params.put("uid_firebase", uidFirebase);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    boolean exito = response.getBoolean("exito");
                    String mensaje = response.getString("mensaje");
                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
                    if (exito) {
                        int idUsuario = response.getInt("id_usuario");
                        // GUARDAR EN SHARED PREFERENCES
                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("id_usuario", idUsuario);
                        editor.apply();

                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                        //navController.navigate(R.id.action_nav_verificar_email_to_nav_login);
                        navController.navigate(R.id.action_nav_verificar_email_to_nav_resumen_finanzas);
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
}