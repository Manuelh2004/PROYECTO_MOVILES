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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class EditarPerfil extends Fragment implements View.OnClickListener{
    private EditText etNombresEd, etApellidosEd, etDocumentoEd, etFechaEd, etTelefonoEd;
    private Spinner spGeneroEd, spTipoDocumentoEd;
    private Button btnGuardarCambios;
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

        spGeneroEd = rootView.findViewById(R.id.spGeneroEd);
        spTipoDocumentoEd = rootView.findViewById(R.id.spTipoDocumentoEd);

        btnGuardarCambios = rootView.findViewById(R.id.btnGuardarCambios);
        btnGuardarCambios.setOnClickListener(this);

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
        client.post(servidor + "perfilController/obtener_datos_perfil2.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("Response", response);  // Imprime la respuesta en el Log

                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    // Obtener los datos del perfil del usuario
                    JSONObject perfil = jsonResponse.getJSONObject("perfil");
                    String nombres = perfil.getString("nom_usuario");
                    String apellidos = perfil.getString("ape_usuario");
                    String documento = perfil.getString("num_usuario");
                    String fechaNacimiento = perfil.getString("fna_usuario");
                    String telefono = perfil.getString("tel_usuario");
                    int idGenero = perfil.getInt("id_genero");
                    int idTipoDocumento = perfil.getInt("id_tipo_documento");

                    // Llenar los campos con los datos obtenidos
                    etNombresEd.setText(nombres);
                    etApellidosEd.setText(apellidos);
                    etDocumentoEd.setText(documento);
                    etFechaEd.setText(fechaNacimiento);
                    etTelefonoEd.setText(telefono);

                    // Llenar el Spinner de Género
                    JSONArray generosArray = jsonResponse.getJSONArray("generos");
                    List<String> generosList = new ArrayList<>();
                    for (int i = 0; i < generosArray.length(); i++) {
                        JSONObject genero = generosArray.getJSONObject(i);
                        generosList.add(genero.getString("nom_genero"));
                    }
                    ArrayAdapter<String> adapterGenero = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, generosList);
                    adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spGeneroEd.setAdapter(adapterGenero);
                    spGeneroEd.setSelection(idGenero - 1);  // Ajuste según el valor de id_genero

                    // Llenar el Spinner de Tipo de Documento
                    JSONArray documentosArray = jsonResponse.getJSONArray("documentos");
                    List<String> documentosList = new ArrayList<>();
                    for (int i = 0; i < documentosArray.length(); i++) {
                        JSONObject documentoObj = documentosArray.getJSONObject(i);
                        documentosList.add(documentoObj.getString("nom_tipo_documento"));
                    }
                    ArrayAdapter<String> adapterDocumento = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, documentosList);
                    adapterDocumento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spTipoDocumentoEd.setAdapter(adapterDocumento);
                    spTipoDocumentoEd.setSelection(idTipoDocumento - 1);  // Ajuste según el valor de id_tipo_documento

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


    private void setSpinnerSelection(Spinner spinner, String value) {
        // Método que establece la selección del Spinner según el valor recibido (sexo o tipo de documento)
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    // Método para guardar los cambios realizados en el perfil
    private void guardarCambios() {
        // Obtener los datos editados
        String nombres = etNombresEd.getText().toString();
        String apellidos = etApellidosEd.getText().toString();
        String documento = etDocumentoEd.getText().toString();
        String fechaNacimiento = etFechaEd.getText().toString();
        String telefono = etTelefonoEd.getText().toString();

        // Obtener el género y el tipo de documento seleccionados
        String genero = spGeneroEd.getSelectedItem().toString();  // Género seleccionado
        String tipoDocumento = spTipoDocumentoEd.getSelectedItem().toString();  // Tipo de documento seleccionado

        // Validar que los campos no estén vacíos
        if (nombres.isEmpty() || apellidos.isEmpty() || fechaNacimiento.isEmpty() || telefono.isEmpty() || genero.isEmpty() || tipoDocumento.isEmpty()) {
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

        // Obtener el ID correspondiente al género y tipo de documento
        params.put("id_genero", getGeneroId(genero));  // Método que convierte el nombre a ID de género
        params.put("id_tipo_documento", getTipoDocumentoId(tipoDocumento));  // Método que convierte el nombre a ID de tipo de documento

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(servidor + "perfilController/actualizar_perfil.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("ServidorRespuesta", response);  // Añadir esto para ver la respuesta completa
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

    // Método para convertir el nombre del género a su ID
    private int getGeneroId(String genero) {
        // Aquí deberás tener una lista de géneros, y retornar el ID correspondiente
        switch (genero) {
            case "Masculino":
                return 1;
            case "Femenino":
                return 2;
            case "Otro":
                return 3;
            default:
                return -1;  // Si no se encuentra, retornamos un valor inválido
        }
    }

    // Método para convertir el nombre del tipo de documento a su ID
    private int getTipoDocumentoId(String tipoDocumento) {
        // Aquí deberás tener una lista de tipos de documento, y retornar el ID correspondiente
        switch (tipoDocumento) {
            case "DNI":
                return 1;
            case "Pasaporte":
                return 2;
            case "Cédula de identidad":
                return 3;
            case "Tarjeta de residencia":
                return 4;
            default:
                return -1;  // Si no se encuentra, retornamos un valor inválido
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnGuardarCambios){
            guardarCambios();
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_editar_perfil_to_nav_perfil);
        }
    }
}