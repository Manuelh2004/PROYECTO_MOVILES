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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Perfil extends Fragment implements View.OnClickListener{
    private Button btnEditar, btnEnviarComentario, btnDownloadHistory;
    private EditText etComentario;
    private TextView txtNombre;  // Cambiar EditText a TextView
    final String servidor = "http://10.0.2.2/proyecto_moviles/controladores/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Obtener id_usuario desde SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1); // Obtener el id_usuario

        // Depuración: Verificar si los valores se cargan correctamente
        Log.d("PerfilFragment", "ID Usuario: " + idUsuario); // Verifica el id

        // Verificar si id_usuario es válido
        if (idUsuario != -1) {
            // Solicitar el nombre del usuario desde el servidor
            obtenerNombreUsuario(idUsuario);
        } else {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
        }

        // Inicializar EditText y botones
        etComentario = (EditText) rootView.findViewById(R.id.etComentario);
        btnEditar = (Button) rootView.findViewById(R.id.btnEditar);
        btnEnviarComentario = (Button) rootView.findViewById(R.id.btnEnviarComentario);
        btnDownloadHistory = (Button) rootView.findViewById(R.id.btnDownloadHistory);

        // Establecer los listeners de los botones
        btnEditar.setOnClickListener(this);
        btnEnviarComentario.setOnClickListener(this);

        btnDownloadHistory.setOnClickListener(this);

        return rootView;
    }

    private void obtenerNombreUsuario(int idUsuario) {
        // URL del servidor para obtener el nombre del usuario
        String url = servidor + "perfilController/obtener_nombre.php?id_usuario=" + idUsuario;

        // Realizar solicitud GET al servidor
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    Log.d("PerfilFragment", "Respuesta del servidor: " + response);

                    // Procesar la respuesta JSON
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");

                    // Si el status es "success", obtener el nombre
                    if (status.equals("success")) {
                        String nombreUsuario = jsonResponse.getString("nombre_usuario");
                        // Mostrar el nombre en el TextView
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
            // Navegar a la pantalla de edición del perfil
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_perfil_to_editarPerfil);
        }
        if (v == btnEnviarComentario) {
            String comentario = etComentario.getText().toString();

            // Verificar si el comentario no está vacío
            if (comentario.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, ingresa un comentario", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener el id_usuario desde SharedPreferences
            SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
            int idUsuario = prefs.getInt("id_usuario", -1);

            // Verificar si id_usuario es válido
            if (idUsuario == -1) {
                Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
                return;
            }

            // Enviar el comentario
            enviarComentario(idUsuario, comentario);
        }

        if (v == btnDownloadHistory) {
            // Obtener id_usuario
            SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
            int idUsuario = prefs.getInt("id_usuario", -1);

            if (idUsuario == -1) {
                Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
                return;
            }
            // Obtener nombre y correo del usuario, y luego enviar saludo
            obtenerCorreoYNombreYEnviarSaludo(idUsuario);
        }
    }

    private void obtenerCorreoYNombreYEnviarSaludo(int idUsuario) {
        String url = servidor + "perfilController/obtener_datos_perfil.php";

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

                        // Enviar el saludo con PHPMailer
                        RequestParams saludoParams = new RequestParams();
                        saludoParams.put("email", email);
                        saludoParams.put("nombre", nombre);

                        AsyncHttpClient correoClient = new AsyncHttpClient();
                        correoClient.post(servidor + "usuarioController/enviar_email.php", saludoParams, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Toast.makeText(getActivity(), "Saludo enviado al correo de " + nombre, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(getActivity(), "Error al enviar el saludo", Toast.LENGTH_SHORT).show();
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
        // Crear los parámetros para la solicitud POST
        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);
        params.put("men_comentario", comentario);
        params.put("est_comentario", "1"); // Estado activo del comentario

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(servidor + "comentarioController/registrar_comentario.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.contains("success")) {
                    Toast.makeText(getActivity(), "Comentario enviado correctamente", Toast.LENGTH_SHORT).show();
                    etComentario.setText("");  // Limpiar el campo de texto
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