package com.example.proyecto_moviles.ui.Inicio;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.example.proyecto_moviles.ui.Clases.Item;
import com.example.proyecto_moviles.ui.Clases.ServidorConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class CrearCuenta extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, LoginDialogFragment.LoginDialogListener{
    private FirebaseAuth mAuth;
    private EditText etNombres, etApellidos, etTelefono, etFechaNa, etDocumento;
    private Button btnCrearUsuario;
    private Spinner spGenero;
    private Spinner spTipoDoc;
    int idGenero = -1, idTipoDoc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_crear_cuenta, container, false);
        mAuth = FirebaseAuth.getInstance();

        etNombres = (EditText) rootView.findViewById(R.id.etNombres);
        etApellidos = (EditText) rootView.findViewById(R.id.etApellidos);
        etTelefono = (EditText) rootView.findViewById(R.id.etTelefono);
        etFechaNa = (EditText) rootView.findViewById(R.id.etFecha);
        etDocumento = (EditText) rootView.findViewById(R.id.etDocumento);

        spGenero = (Spinner) rootView.findViewById(R.id.spGenero);
        spGenero.setOnItemSelectedListener(this);
        spTipoDoc = (Spinner) rootView.findViewById(R.id.spTipoDocumento);
        spTipoDoc.setOnItemSelectedListener(this);
        btnCrearUsuario = (Button) rootView.findViewById(R.id.btnCrearUsuario);
        btnCrearUsuario.setOnClickListener(this);

        etFechaNa.setFocusable(false);
        etFechaNa.setClickable(true);

        etFechaNa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String formattedDate = String.format("%02d/%02d/%02d", dayOfMonth, monthOfYear + 1, year % 100);
                                etFechaNa.setText(formattedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
        obtenerGenero();
        obtenerTipoDoc();

        return  rootView;
    }

    private void obtenerTipoDoc() {
        String url = ServidorConfig.URL_SERVIDOR + "itemsController/obtener_tipo_doc.php";
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Item> lista = new ArrayList<>();
                lista.add(new Item(-1, "Seleccionar Tipo de Documento"));

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        lista.add(new Item(id, nombre));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spTipoDoc.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerGenero() {
        String url = ServidorConfig.URL_SERVIDOR + "itemsController/obtener_genero.php";
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Item> lista = new ArrayList<>();
                lista.add(new Item(-1, "Seleccionar genero"));

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        lista.add(new Item(id, nombre));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spGenero.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnCrearUsuario) {
            if (etNombres.getText().toString().isEmpty() ||
                    etApellidos.getText().toString().isEmpty() ||
                    etTelefono.getText().toString().isEmpty() ||
                    etDocumento.getText().toString().isEmpty() ||
                    etFechaNa.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String telefono = etTelefono.getText().toString();
            if (!telefono.matches("^9\\d{8}$")) {
                Toast.makeText(getActivity(), "El número debe tener 9 dígitos y comenzar con 9", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String[] fecha = etFechaNa.getText().toString().split("/");
                int dia = Integer.parseInt(fecha[0]);
                int mes = Integer.parseInt(fecha[1]) - 1;
                int anio = Integer.parseInt("20" + fecha[2]);

                Calendar fechaNacimiento = Calendar.getInstance();
                fechaNacimiento.set(anio, mes, dia);

                Calendar hoy = Calendar.getInstance();
                if (fechaNacimiento.after(hoy)) {
                    Toast.makeText(getActivity(), "La fecha de nacimiento no puede ser futura", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Formato de fecha inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (idGenero == -1) {
                Toast.makeText(getActivity(), "Por favor, seleccione un genero", Toast.LENGTH_SHORT).show();
                return;
            }
            if (idTipoDoc == -1) {
                Toast.makeText(getActivity(), "Por favor, seleccione un tipo de documento", Toast.LENGTH_SHORT).show();
                return;
            }
            LoginDialogFragment dialog = new LoginDialogFragment();
            dialog.setLoginDialogListener(this);
            dialog.show(getParentFragmentManager(), "LoginDialog");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent==spGenero)
        {
            Item selectedItem = (Item) parent.getItemAtPosition(position);
            if (selectedItem.id == -1) {
            } else {

                int selectedId = selectedItem.id;
                String selectedNombre = selectedItem.nombre;
                idGenero = selectedId;
            }
        }
        if(parent==spTipoDoc)
        {
            Item selectedItem = (Item) parent.getItemAtPosition(position);
            if (selectedItem.id == -1) {
            } else {
                int selectedId = selectedItem.id;
                String selectedNombre = selectedItem.nombre;
                idTipoDoc = selectedId;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onLoginDataEntered(String email, String password) {
        String nombres = etNombres.getText().toString();
        String apellidos = etApellidos.getText().toString();
        String telefono = etTelefono.getText().toString();
        String documento = etDocumento.getText().toString();
        String fechaNa = etFechaNa.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        firebaseUser.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                            if (verifyTask.isSuccessful()) {
                                Toast.makeText(getContext(), "Correo de verificación enviado", Toast.LENGTH_LONG).show();

                                Bundle bundle = new Bundle();
                                bundle.putString("nombres", nombres);
                                bundle.putString("apellidos", apellidos);
                                bundle.putInt("idGenero", idGenero);
                                bundle.putInt("idTipoDoc", idTipoDoc);
                                bundle.putString("telefono", telefono);
                                bundle.putString("documento", documento);
                                bundle.putString("fechaNa", fechaNa);

                                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                                navController.navigate(R.id.action_nav_crear_cuenta_to_nav_verificar_email, bundle);
                            } else {
                                Toast.makeText(getContext(), "No se pudo enviar el correo de verificación.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "Error al registrar usuario: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }
}