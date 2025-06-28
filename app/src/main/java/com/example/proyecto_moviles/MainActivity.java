package com.example.proyecto_moviles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_moviles.ui.BaseActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.proyecto_moviles.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.proyecto_moviles.R;


import javax.security.auth.callback.Callback;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    public int opc_resumen_finanzas, opc_presupuesto, opc_movimientos, opc_visual, opc_perfil, opc_administrador;

    private String servidor = "http://10.0.2.2/proyecto_moviles/controladores/usuarioController/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        /*if (opc_resumen_finanzas == 0){
            navigationView.getMenu().findItem(R.id.nav_resumen_finanzas).setVisible(false);
        }
        if (opc_presupuesto == 0){
            navigationView.getMenu().findItem(R.id.nav_presupuesto).setVisible(false);
        }
        if (opc_movimientos == 0){
            navigationView.getMenu().findItem(R.id.nav_movimiento).setVisible(false);
        }
        if (opc_visual == 0){
            navigationView.getMenu().findItem(R.id.nav_analisis_visual_egresos).setVisible(false);
        }
        if (opc_perfil == 0){
            navigationView.getMenu().findItem(R.id.nav_perfil).setVisible(false);
        }
        if (opc_administrador == 0){
            navigationView.getMenu().findItem(R.id.nav_administrador).setVisible(false);
        }*/

        // Configura top level destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_resumen_finanzas,R.id.nav_movimiento, R.id.nav_presupuesto, R.id.nav_analisis_visual_egresos, R.id.nav_perfil, R.id.nav_administrador)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Obtener el usuario logueado y actualizar el header
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Usuario logueado, actualizar el encabezado
            View headerView = navigationView.getHeaderView(0);

            ImageView logoImageView = headerView.findViewById(R.id.logoImageView); // Obtener la ImageView



       // Cambia el logo a uno nuevo (asegúrate de tener esta imagen en res/drawable)
        }

        // Manejo de clicks en el menú
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                // Limpiar SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                // Cerrar sesión Firebase
                FirebaseAuth.getInstance().signOut();

                // Navegar a login manualmente
                navController.navigate(R.id.nav_login);

                // Cerrar drawer
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }

            else {
                // Para otros items, usar navegación normal
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (handled) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return handled;
            }
        });

        // Control de visibilidad toolbar según fragmento
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_login ||
                    destination.getId() == R.id.nav_crear_cuenta ||
                    destination.getId() == R.id.nav_olvidarPassword ||
                    destination.getId() == R.id.nav_verificar_email) {
                binding.appBarMain.toolbar.setVisibility(View.GONE);
            } else {
                binding.appBarMain.toolbar.setVisibility(View.VISIBLE);
            }
        });
    }

    public void actualizarMenu(int opc_resumen_finanzas, int opc_presupuesto, int opc_movimientos, int opc_visual, int opc_perfil, int opc_administrador) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (opc_resumen_finanzas == 0){
            navigationView.getMenu().findItem(R.id.nav_resumen_finanzas).setVisible(false);
        }
        else{
            navigationView.getMenu().findItem(R.id.nav_resumen_finanzas).setVisible(true);
        }
        if (opc_presupuesto == 0){
            navigationView.getMenu().findItem(R.id.nav_presupuesto).setVisible(false);
        }
        else{
            navigationView.getMenu().findItem(R.id.nav_presupuesto).setVisible(true);
        }
        if (opc_movimientos == 0){
            navigationView.getMenu().findItem(R.id.nav_movimiento).setVisible(false);
        }else{
            navigationView.getMenu().findItem(R.id.nav_movimiento).setVisible(true);
        }
        if (opc_visual == 0){
            navigationView.getMenu().findItem(R.id.nav_analisis_visual_egresos).setVisible(false);
        }
        else {
            navigationView.getMenu().findItem(R.id.nav_analisis_visual_egresos).setVisible(true);
        }
        if (opc_perfil == 0){
            navigationView.getMenu().findItem(R.id.nav_perfil).setVisible(false);
        }
        else {
            navigationView.getMenu().findItem(R.id.nav_perfil).setVisible(true);
        }
        if (opc_administrador == 0){
            navigationView.getMenu().findItem(R.id.nav_administrador).setVisible(false);
        }
        else {
            navigationView.getMenu().findItem(R.id.nav_administrador).setVisible(true);
        }
    }

    public void ConsultarUsuario(int id_usuario, final Callback callback){

        String url = servidor + "consultar_usuario.php";

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();
        params.put("id_usuario",id_usuario);

        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient usuario = new AsyncHttpClient();

        usuario.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String

                try {
                    // Parsear el JSON recibido
                    JSONArray jsonArray = new JSONArray(response);


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                         opc_resumen_finanzas = jsonObject.getInt("opc_resumen_finanzas");
                         opc_presupuesto = jsonObject.getInt("opc_presupuesto");
                         opc_movimientos = jsonObject.getInt("opc_movimientos");
                         opc_visual = jsonObject.getInt("opc_visual");
                         opc_perfil = jsonObject.getInt("opc_perfil");
                         opc_administrador = jsonObject.getInt("opc_administrador");
                    }

                    callback.onUsuarioCargado(opc_resumen_finanzas, opc_presupuesto, opc_movimientos, opc_visual, opc_perfil, opc_administrador);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error al parsear el JSON", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public interface Callback {
        void onUsuarioCargado(int opc_resumen_finanzas, int opc_presupuesto, int opc_movimientos, int opc_visual, int opc_perfil, int opc_administrador);
    }
}