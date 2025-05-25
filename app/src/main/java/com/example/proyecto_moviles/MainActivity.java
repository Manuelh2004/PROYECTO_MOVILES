package com.example.proyecto_moviles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

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

public class MainActivity extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show()
        );

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Configura top level destinations (agrega todos tus destinos principales)
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_movimiento, R.id.nav_presupuesto, R.id.nav_visualizacion, R.id.nav_perfil)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Aquí interceptamos los clicks del menú para manejar logout manualmente
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
                return true; // evento consumido
            } else {
                // Para otros items, usar navegación normal
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (handled) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return handled;
            }
        });

        // Control de visibilidad toolbar según fragmento (opcional)
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_login ||
                    destination.getId() == R.id.nav_crear_cuenta ||
                    destination.getId() == R.id.nav_olvidarPassword) {
                binding.appBarMain.toolbar.setVisibility(View.GONE);
            } else {
                binding.appBarMain.toolbar.setVisibility(View.VISIBLE);
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
}
