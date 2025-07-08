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
import com.example.proyecto_moviles.ui.Clases.ServidorConfig;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_editar_perfil, container, false);

        etNombresEd = rootView.findViewById(R.id.etNombresEd);
        etApellidosEd = rootView.findViewById(R.id.etApellidosEd);
        etDocumentoEd = rootView.findViewById(R.id.etDocumentoEd);
        etFechaEd = rootView.findViewById(R.id.etFechaEd);
        etTelefonoEd = rootView.findViewById(R.id.etTelefonoEd);

        spGeneroEd = rootView.findViewById(R.id.spGeneroEd);
        spTipoDocumentoEd = rootView.findViewById(R.id.spTipoDocumentoEd);

        btnGuardarCambios = rootView.findViewById(R.id.btnGuardarCambios);
        btnGuardarCambios.setOnClickListener(this);

        cargarDatosPerfil();
        return rootView;
    }

    private void cargarDatosPerfil() {
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);

        AsyncHttpClient client = new AsyncHttpClient();
        String url = ServidorConfig.URL_SERVIDOR + "perfilController/obtener_datos_perfil2.php";
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("Response", response);

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject perfil = jsonResponse.getJSONObject("perfil");
                    String nombres = perfil.getString("nom_usuario");
                    String apellidos = perfil.getString("ape_usuario");
                    String documento = perfil.getString("num_usuario");
                    String fechaNacimiento = perfil.getString("fna_usuario");
                    String telefono = perfil.getString("tel_usuario");
                    int idGenero = perfil.getInt("id_genero");
                    int idTipoDocumento = perfil.getInt("id_tipo_documento");

                    etNombresEd.setText(nombres);
                    etApellidosEd.setText(apellidos);
                    etDocumentoEd.setText(documento);
                    etFechaEd.setText(fechaNacimiento);
                    etTelefonoEd.setText(telefono);

                    JSONArray generosArray = jsonResponse.getJSONArray("generos");
                    List<String> generosList = new ArrayList<>();
                    for (int i = 0; i < generosArray.length(); i++) {
                        JSONObject genero = generosArray.getJSONObject(i);
                        generosList.add(genero.getString("nom_genero"));
                    }
                    ArrayAdapter<String> adapterGenero = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, generosList);
                    adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spGeneroEd.setAdapter(adapterGenero);
                    spGeneroEd.setSelection(idGenero - 1);

                    JSONArray documentosArray = jsonResponse.getJSONArray("documentos");
                    List<String> documentosList = new ArrayList<>();
                    for (int i = 0; i < documentosArray.length(); i++) {
                        JSONObject documentoObj = documentosArray.getJSONObject(i);
                        documentosList.add(documentoObj.getString("nom_tipo_documento"));
                    }
                    ArrayAdapter<String> adapterDocumento = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, documentosList);
                    adapterDocumento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spTipoDocumentoEd.setAdapter(adapterDocumento);
                    spTipoDocumentoEd.setSelection(idTipoDocumento - 1);

                } catch (JSONException e) {
                    e.printStackTrace();
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
        String nombres = etNombresEd.getText().toString();
        String apellidos = etApellidosEd.getText().toString();
        String documento = etDocumentoEd.getText().toString();
        String fechaNacimiento = etFechaEd.getText().toString();
        String telefono = etTelefonoEd.getText().toString();

        String genero = spGeneroEd.getSelectedItem().toString();
        String tipoDocumento = spTipoDocumentoEd.getSelectedItem().toString();

        if (nombres.isEmpty() || apellidos.isEmpty() || fechaNacimiento.isEmpty() || telefono.isEmpty() || genero.isEmpty() || tipoDocumento.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", getActivity().MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        if (idUsuario == -1) {
            Toast.makeText(getActivity(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);
        params.put("nombres", nombres);
        params.put("apellidos", apellidos);
        params.put("documento", documento);
        params.put("fecha_nacimiento", fechaNacimiento);
        params.put("telefono", telefono);
        params.put("id_genero", getGeneroId(genero));
        params.put("id_tipo_documento", getTipoDocumentoId(tipoDocumento));

        AsyncHttpClient client = new AsyncHttpClient();
        String url = ServidorConfig.URL_SERVIDOR + "perfilController/actualizar_perfil.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("ServidorRespuesta", response);
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
        switch (genero) {
            case "Masculino":
                return 1;
            case "Femenino":
                return 2;
            case "Otro":
                return 3;
            default:
                return -1;
        }
    }

    // Método para convertir el nombre del tipo de documento a su ID
    private int getTipoDocumentoId(String tipoDocumento) {
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
                return -1;
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