package com.example.proyecto_moviles;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.proyecto_moviles.ui.BaseActivity;
import com.example.proyecto_moviles.ui.Clases.ServidorConfig;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
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

import cz.msebera.android.httpclient.Header;

public class MainActivity extends BaseActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private MonedaViewModel monedaViewModel;
    public int opc_resumen_finanzas, opc_presupuesto, opc_movimientos, opc_visual, opc_perfil, opc_administrador;
    private boolean modoMoneda = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        monedaViewModel = new ViewModelProvider(this).get(MonedaViewModel.class);
        monedaViewModel.setMostrarEnDolares(modoMoneda);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Configura top level destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_resumen_finanzas,R.id.nav_movimiento, R.id.nav_presupuesto, R.id.nav_analisis_visual_egresos, R.id.nav_perfil, R.id.nav_administrador)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            View headerView = navigationView.getHeaderView(0);
            ImageView logoImageView = headerView.findViewById(R.id.logoImageView);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                navController.navigate(R.id.nav_login);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }

            else {
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (handled) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return handled;
            }
        });

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

        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        if (idUsuario != -1) {
            ConsultarUsuario(idUsuario, new Callback() {
                @Override
                public void onUsuarioCargado(int resumen, int presupuesto, int movimientos, int visual, int perfil, int administrador) {
                    actualizarMenu(resumen, presupuesto, movimientos, visual, perfil, administrador);
                }
            });
        }
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
        String url = ServidorConfig.URL_SERVIDOR + "usuarioController/consultar_usuario.php";
        RequestParams params = new RequestParams();
        params.put("id_usuario",id_usuario);
        AsyncHttpClient usuario = new AsyncHttpClient();

        usuario.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
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
        MenuItem itemCambioMoneda = menu.findItem(R.id.action_cambio_moneda);
        if (itemCambioMoneda != null && itemCambioMoneda.hasSubMenu()) {
            SubMenu subMenu = itemCambioMoneda.getSubMenu();

            if (subMenu != null) {
                subMenu.findItem(R.id.action_soles_a_dolares).setOnMenuItemClickListener(item -> {
                    monedaViewModel.setMostrarEnDolares(true);
                    Toast.makeText(this, "Mostrando en dólares", Toast.LENGTH_SHORT).show();
                    modoMoneda = true;
                    return true;
                });

                subMenu.findItem(R.id.action_dolares_a_soles).setOnMenuItemClickListener(item -> {
                    monedaViewModel.setMostrarEnDolares(false);
                    Toast.makeText(this, "Mostrando en soles", Toast.LENGTH_SHORT).show();
                    modoMoneda = false;
                    return true;
                });
            }
        }

        MenuItem itemInstrucciones = menu.findItem(R.id.action_instrucciones);
        if (itemInstrucciones != null) {
            itemInstrucciones.setOnMenuItemClickListener(item -> {
                mostrarVideoInstrucciones();
                return true;
            });
        }
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void mostrarVideoInstrucciones() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_video_instrucciones, null);
        WebView webView = dialogView.findViewById(R.id.webViewInstrucciones);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // ID del video de YouTube Shorts
        String videoId = "b10hQPeLOi8";

        // HTML limpio para insertar video con <iframe>
        String html = "<html><body style='margin:0;padding:0;'>"
                + "<iframe width='100%' height='100%' "
                + "src='https://www.youtube.com/embed/" + videoId + "?autoplay=1' "
                + "frameborder='0' allow='accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture' "
                + "allowfullscreen></iframe>"
                + "</body></html>";

        // Cargar el contenido HTML en el WebView
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        // Mostrar el diálogo
        new AlertDialog.Builder(this)
                .setTitle("Instrucciones")
                .setView(dialogView)
                .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                .show();
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