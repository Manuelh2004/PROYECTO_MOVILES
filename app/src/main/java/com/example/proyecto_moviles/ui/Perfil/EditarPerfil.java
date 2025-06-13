package com.example.proyecto_moviles.ui.Perfil;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class EditarPerfil extends Fragment {
    private EditText etNombresEd, etApellidosEd, etDocumentoEd, etFechaEd, etTelefonoEd, etPasswordEd;
    private Spinner spGeneroEd, spTipoDocumentoEd;
    final String servidor = "http://10.0.2.2/proyecto_moviles/controladores/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_editar_perfil, container, false);

        // Inicializar los EditText y Spinner
        etNombresEd = rootView.findViewById(R.id.etNombresEd);
        etApellidosEd = rootView.findViewById(R.id.etApellidosEd);
        etDocumentoEd = rootView.findViewById(R.id.etDocumentoEd);
        etFechaEd = rootView.findViewById(R.id.etFechaEd);
        etTelefonoEd = rootView.findViewById(R.id.etTelefonoEd);
        etPasswordEd = rootView.findViewById(R.id.etPasswordEd);

        spGeneroEd = rootView.findViewById(R.id.spGeneroEd);
        spTipoDocumentoEd = rootView.findViewById(R.id.spTipoDocumentoEd);

        // Cargar los datos del perfil del usuario logueado
        cargarDatosPerfil();

        return rootView;
    }

    private void cargarDatosPerfil() {
        // Obtener el ID del usuario desde SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Solicitar los datos del perfil desde el servidor
        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(servidor + "perfilController/obtener_datos_perfil.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("Response", response);  // Imprime la respuesta en el Log

                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    // Obtener los datos del perfil del usuario
                    String nombres = jsonResponse.getString("nom_usuario");
                    String apellidos = jsonResponse.getString("ape_usuario");
                    String documento = jsonResponse.getString("num_usuario");
                    String fechaNacimiento = jsonResponse.getString("fna_usuario");
                    String telefono = jsonResponse.getString("tel_usuario");

                    // Llenar los campos con los datos obtenidos
                    etNombresEd.setText(nombres);
                    etApellidosEd.setText(apellidos);
                    etDocumentoEd.setText(documento);
                    etFechaEd.setText(fechaNacimiento);
                    etTelefonoEd.setText(telefono);
                    etPasswordEd.setText("");  // No mostramos la contraseña por seguridad, solo se edita en el backend

                } catch (JSONException e) {
                    e.printStackTrace();
                    // Mostrar el contenido de la respuesta completa para depuración
                    Toast.makeText(getActivity(), "Error al cargar los datos del perfil: " + response, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para guardar los cambios realizados en el perfil
    private void guardarCambios() {
        // Obtener los datos editados
        String nombres = etNombresEd.getText().toString();
        String apellidos = etApellidosEd.getText().toString();
        String documento = etDocumentoEd.getText().toString();
        String fechaNacimiento = etFechaEd.getText().toString();
        String telefono = etTelefonoEd.getText().toString();
        String password = etPasswordEd.getText().toString();

        // Validar que los campos no estén vacíos
        if (nombres.isEmpty() || apellidos.isEmpty() || documento.isEmpty() || fechaNacimiento.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el ID del usuario desde SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar los datos modificados al servidor
        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);
        params.put("nombres", nombres);
        params.put("apellidos", apellidos);
        params.put("documento", documento);
        params.put("fecha_nacimiento", fechaNacimiento);
        params.put("telefono", telefono);
        params.put("password", password);  // Se puede enviar en blanco si no se cambia

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(servidor + "perfilController/actualizar_perfil.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.contains("success")) {
                    Toast.makeText(getActivity(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}