package com.example.proyecto_moviles.ui.Perfil;

import android.content.SharedPreferences;
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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class Perfil extends Fragment implements View.OnClickListener{
    private Button btnEditar, btnEnviarComentario;
    private EditText etComentario;
    final String servidor = "http://10.0.2.2/proyecto_moviles/controladores/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil, container, false);

        etComentario = (EditText) rootView.findViewById(R.id.etComentario);

        btnEditar = (Button) rootView.findViewById(R.id.btnEditar);
        btnEditar.setOnClickListener(this);
        btnEnviarComentario = (Button) rootView.findViewById(R.id.btnEnviarComentario);
        btnEnviarComentario.setOnClickListener(this);

        return rootView;
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
            // Obtener el id_usuario desde SharedPreferences
            SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
            int idUsuario = prefs.getInt("id_usuario", -1);

            if (idUsuario == -1) {
                Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
                return;
            }
            enviarComentario(idUsuario, comentario);
        }
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