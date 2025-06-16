package com.example.proyecto_moviles.ui.Inicio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.proyecto_moviles.MainActivity;
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
import android.widget.TextView;
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

import javax.security.auth.callback.Callback;

import cz.msebera.android.httpclient.Header;

public class Login extends Fragment implements View.OnClickListener{
    private Button btnIngresoDirecto, btnCrearCuenta;
    private TextInputEditText Contraseña;
    private EditText Usuario;
    final String servidor = "http://10.0.2.2/proyecto_moviles/controladores/usuarioController/";
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private TextView txtOlvidarPassword;
    private MainActivity activity;
    private int id_usuario = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        Usuario = rootView.findViewById(R.id.etUsuario);
        Contraseña = rootView.findViewById(R.id.etPassword);
        btnIngresoDirecto = rootView.findViewById(R.id.btnIngresar);
        btnIngresoDirecto.setOnClickListener(this);
        btnCrearCuenta = rootView.findViewById(R.id.btnCrearCuenta);
        btnCrearCuenta.setOnClickListener(this);
        txtOlvidarPassword = rootView.findViewById(R.id.txtOlvidarPassword);
        txtOlvidarPassword.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mAuth = FirebaseAuth.getInstance();

        LinearLayout googleLoginBtn = rootView.findViewById(R.id.btnGoogleLogin);
        googleLoginBtn.setOnClickListener(v -> signInWithGoogle());

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();

        if (activity == null) {
            Toast.makeText(getActivity(), "Activity nula", Toast.LENGTH_SHORT).show();
            return;
        }

        // Aquí chequeamos la sesión y navegamos si ya está logueado
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        boolean logueado = prefs.getBoolean("logueado", false);
        if (logueado) {

            id_usuario = prefs.getInt("id_usuario", -1);
            if (id_usuario == -1) {
                Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
                return;
            }

            activity.ConsultarUsuario(id_usuario, new MainActivity.Callback() {
                @Override
                public void onUsuarioCargado(int opc_resumen_finanzas, int opc_presupuesto, int opc_movimientos, int opc_visual, int opc_perfil, int opc_administrador) {
                    ((MainActivity) getActivity()).actualizarMenu(opc_resumen_finanzas, opc_presupuesto, opc_movimientos, opc_visual, opc_perfil, opc_administrador);
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_nav_login_to_nav_resumen_finanzas);
                }
            });
            /*((MainActivity) getActivity()).actualizarMenu(activity.opc_resumen_finanzas, activity.opc_presupuesto, activity.opc_movimientos, activity.opc_visual, activity.opc_perfil, activity.opc_administrador);
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_nav_login_to_nav_resumen_finanzas);*/
        }
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
            String email = Usuario.getText().toString().trim();
            String password = Contraseña.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, complete los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("logueado", true);
                                    editor.apply();

                                    Toast.makeText(getContext(), "Ingreso exitoso", Toast.LENGTH_SHORT).show();
                                    obtenerIdUsuarioBackend(email);

                                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                                    navController.navigate(R.id.action_nav_login_to_nav_resumen_finanzas); // action_crearCuenta_to_nav_login
                                } else {
                                    Toast.makeText(getContext(), "Por favor, verifica tu correo antes de ingresar.", Toast.LENGTH_LONG).show();
                                    mAuth.signOut();
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "Correo y/o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else if (v == btnCrearCuenta) {
            NavController navController = Navigation.findNavController(getView());
            navController.navigate(R.id.action_nav_login_to_crearCuenta);
        } else if (v == txtOlvidarPassword) {
            NavController navController = Navigation.findNavController(getView());
            navController.navigate(R.id.action_nav_login_to_nav_olvidarPassword);
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
                        navController.navigate(R.id.action_nav_login_to_nav_resumen_finanzas);
                    } else {
                        Log.w("FirebaseAuth", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void enviarUsuarioAlServidor(FirebaseUser user) {
        String url = servidor + "crear_usuario_google.php";

        RequestParams params = new RequestParams();
        params.put("nombre", user.getDisplayName() != null ? user.getDisplayName().split(" ")[0] : "");
        params.put("apellido", user.getDisplayName() != null && user.getDisplayName().split(" ").length > 1 ?
                user.getDisplayName().split(" ")[1] : "");
        params.put("email", user.getEmail());
        params.put("password", "firebase");

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        int idUsuarioDelBackend = response.getInt("id_usuario");

                        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("id_usuario", idUsuarioDelBackend);
                        editor.putBoolean("logueado", true);
                        editor.apply();

                        NavController navController = Navigation.findNavController(getView());
                        navController.navigate(R.id.action_nav_login_to_nav_resumen_finanzas);
                    }
                    Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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

    private void obtenerIdUsuarioBackend(String email) {
        String url = servidor + "login_usuario.php";

        RequestParams params = new RequestParams();
        params.put("email", email);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        int idUsuarioDelBackend = response.getInt("id_usuario");

                        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("id_usuario", idUsuarioDelBackend);
                        editor.apply();

                        // Log para confirmar guardado
                        Log.d("Login", "Guardado id_usuario en SharedPreferences: " + idUsuarioDelBackend);

                        Toast.makeText(getContext(), "Ingreso exitoso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al obtener usuario backend", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al parsear respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
