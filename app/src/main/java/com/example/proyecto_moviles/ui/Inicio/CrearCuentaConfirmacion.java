package com.example.proyecto_moviles.ui.Inicio;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CrearCuentaConfirmacion extends Fragment implements View.OnClickListener{
    final String servidor = "http://10.0.2.2/proyecto_moviles/controladores/usuarioController";
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_crear_cuenta_confirmacion, container, false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // viene del json
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mAuth = FirebaseAuth.getInstance();

        LinearLayout googleLoginBtn = rootView.findViewById(R.id.btnGoogleLogin); // tu botón
        googleLoginBtn.setOnClickListener(v -> signInWithGoogle());


        return rootView;
    }
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("LoginFragment", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String nombreCompleto = user.getDisplayName();
                        String email = user.getEmail();

                        String[] partesNombre = nombreCompleto.split(" ", 2);
                        String nombre = partesNombre.length > 0 ? partesNombre[0] : "";
                        String apellido = partesNombre.length > 1 ? partesNombre[1] : "";

                        Toast.makeText(getContext(), "Bienvenido " + nombreCompleto, Toast.LENGTH_SHORT).show();

                        // 👇 Aquí llamas a tu backend con los datos del usuario
                        registrarUsuarioEnPHP(nombre, apellido, email);
                    } else {
                        Toast.makeText(getContext(), "Falló la autenticación", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registrarUsuarioEnPHP(String nombre, String apellido, String email) {
        String url = servidor + "/registrar_usuario_google.php"; // reemplaza por tu ruta real

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("nom_usuario", nombre);
        params.put("ape_usuario", apellido);
        params.put("em_usuario", email);

        client.post(url, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("Registro", "Respuesta del servidor: " + responseString);

                try {
                    JSONObject json = new JSONObject(responseString);
                    boolean success = json.getBoolean("success");
                    String message = json.getString("message");

                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                    if (success) {
                        // Si todo sale bien, quiero que redireccione a esto
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.action_nav_crear_cuenta_confirmacion_to_nav_presupuesto);
                    }

                } catch (Exception e) {
                    Log.e("Registro", "Error al parsear JSON: " + e.getMessage());
                    Toast.makeText(getContext(), "Error inesperado en la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("Registro", "Error en el registro: " + throwable.getMessage());
                Toast.makeText(getContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}