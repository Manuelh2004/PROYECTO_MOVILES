package com.example.proyecto_moviles.ui.Perfil;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class Perfil extends Fragment implements View.OnClickListener{
    private Button btnEditar, btnEnviarComentario, btnDownloadHistory;
    private EditText etComentario;
    private TextView txtNombre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        Log.d("PerfilFragment", "ID Usuario: " + idUsuario);

        if (idUsuario != -1) {
            obtenerNombreUsuario(idUsuario);
        } else {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
        }

        etComentario = (EditText) rootView.findViewById(R.id.etComentario);
        btnEditar = (Button) rootView.findViewById(R.id.btnEditar);
        btnEnviarComentario = (Button) rootView.findViewById(R.id.btnEnviarComentario);
        btnDownloadHistory = (Button) rootView.findViewById(R.id.btnDownloadHistory);

        btnEditar.setOnClickListener(this);
        btnEnviarComentario.setOnClickListener(this);
        btnDownloadHistory.setOnClickListener(this);

        return rootView;
    }

    private void obtenerNombreUsuario(int idUsuario) {
        String url = ServidorConfig.URL_SERVIDOR + "perfilController/obtener_nombre.php?id_usuario=" + idUsuario;
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    Log.d("PerfilFragment", "Respuesta del servidor: " + response);
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");

                    if (status.equals("success")) {
                        String nombreUsuario = jsonResponse.getString("nombre_usuario");
                        txtNombre = getView().findViewById(R.id.txtNombre);
                        txtNombre.setText(nombreUsuario);
                    } else {
                        Toast.makeText(getActivity(), "No se encontró el usuario", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnEditar){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_perfil_to_editarPerfil);
        }
        if (v == btnEnviarComentario) {
            String comentario = etComentario.getText().toString();
            if (comentario.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, ingresa un comentario", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
            int idUsuario = prefs.getInt("id_usuario", -1);

            if (idUsuario == -1) {
                Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
                return;
            }
            enviarComentario(idUsuario, comentario);
        }

        if (v == btnDownloadHistory) {
            SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
            int idUsuario = prefs.getInt("id_usuario", -1);

            if (idUsuario == -1) {
                Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
                return;
            }
            enviarExcel(idUsuario);
        }
    }

    private void enviarExcel(int idUsuario) {
        String url = ServidorConfig.URL_SERVIDOR + "perfilController/obtener_datos_perfil.php";
        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    if (!response.equalsIgnoreCase("error")) {
                        JSONObject json = new JSONObject(response);
                        String email = json.getString("em_usuario");
                        String nombre = json.getString("nom_usuario");

                        RequestParams params = new RequestParams();
                        params.put("id_usuario", idUsuario);
                        params.put("email", email);
                        params.put("nombre", nombre);
                        AsyncHttpClient client = new AsyncHttpClient();
                        String url = ServidorConfig.URL_SERVIDOR + "perfilController/generar_excel.php";

                        client.post(url, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Toast.makeText(getActivity(), "Reporte enviado por correo", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(getActivity(), "Error al enviar el reporte", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "No se encontró el usuario", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                    Log.e("CorreoSaludo", "Error JSON", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error de conexión al obtener datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarComentario(int idUsuario, String comentario) {
        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);
        params.put("men_comentario", comentario);
        params.put("est_comentario", "1");

        AsyncHttpClient client = new AsyncHttpClient();
        String url = ServidorConfig.URL_SERVIDOR + "comentarioController/registrar_comentario.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.contains("success")) {
                    Toast.makeText(getActivity(), "Comentario enviado correctamente", Toast.LENGTH_SHORT).show();
                    etComentario.setText("");
                } else {
                    Toast.makeText(getActivity(), "Error al enviar el comentario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}