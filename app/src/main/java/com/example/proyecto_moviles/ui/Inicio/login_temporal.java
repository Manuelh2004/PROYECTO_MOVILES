package com.example.proyecto_moviles.ui.Inicio;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.loopj.android.http.AsyncHttpClient;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class login_temporal extends Fragment implements View.OnClickListener{
    private Button btnIngresoDirecto, btnCrearCuenta;
    private TextInputEditText Contraseña;
    private EditText Usuario;
    final String servidor = "http://10.0.2.2/proyecto_app/";
   // final String servidor = "http://10.0.2.2/proyecto_moviles/controladores/usuarioController/"; -> PARA QUE FUNCIONE EL INICIO POR GOOGLE
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        Usuario = rootView.findViewById(R.id.etUsuario);
        Contraseña = rootView.findViewById(R.id.etPassword);

        btnIngresoDirecto = (Button) rootView.findViewById(R.id.btnIngresoDirecto);
        btnIngresoDirecto.setOnClickListener(this);

        btnCrearCuenta = (Button) rootView.findViewById(R.id.btnCrearCuenta);
        btnCrearCuenta.setOnClickListener(this);

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mAuth = FirebaseAuth.getInstance();

        // Botón de login con Google
        LinearLayout googleLoginBtn = rootView.findViewById(R.id.btnGoogleLogin);
        googleLoginBtn.setOnClickListener(v -> signInWithGoogle());

        return rootView;
    }
    private void signInWithGoogle() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(getActivity(), task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
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
                        enviarUsuarioAlServidor(user);

                        NavController navController = Navigation.findNavController(getView());
                        navController.navigate(R.id.action_nav_login_to_nav_presupuesto); // Temporal
                    } else {
                        Log.w("FirebaseAuth", "signInWithCredential:failure", task.getException());
                    }
                });
    }


    private void enviarUsuarioAlServidor(FirebaseUser user) {
        String url = servidor + "crear_usuario.php"; // cambia por tu ruta

        RequestParams params = new RequestParams();
        params.put("nombre", user.getDisplayName() != null ? user.getDisplayName().split(" ")[0] : "");
        params.put("apellido", user.getDisplayName() != null && user.getDisplayName().split(" ").length > 1 ?
                user.getDisplayName().split(" ")[1] : "");
        params.put("email", user.getEmail());
        params.put("password", "firebase"); // Firebase no devuelve la contraseña. (Valor predeterminado)

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("RESPUESTA_BACKEND", response.toString());  // <-- Esto te ayuda a ver la respuesta JSON
                try {
                    boolean success = response.getBoolean("success");
                    String message = response.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al parsear respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
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