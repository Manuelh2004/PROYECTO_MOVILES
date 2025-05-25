package com.example.proyecto_moviles.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.proyecto_moviles.MainActivity;

public class BaseActivity extends AppCompatActivity {
    private static final long NOTIFICATION_TIMEOUT = 25 * 60 * 1000; // 25 minutos
    private static final long FINAL_TIMEOUT = 5 * 60 * 1000; // 5 minutos más

    private Handler handler = new Handler();
    private Runnable warnRunnable;
    private Runnable logoutRunnable;

    @Override
    protected void onResume() {
        super.onResume();
        startInactivityTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopInactivityTimers();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetInactivityTimers();
    }

    private void startInactivityTimers() {
        stopInactivityTimers(); // Evita duplicados

        warnRunnable = () -> showSessionWarning();
        handler.postDelayed(warnRunnable, NOTIFICATION_TIMEOUT);
    }

    private void stopInactivityTimers() {
        if (warnRunnable != null) handler.removeCallbacks(warnRunnable);
        if (logoutRunnable != null) handler.removeCallbacks(logoutRunnable);
    }

    private void resetInactivityTimers() {
        stopInactivityTimers();
        startInactivityTimers();
    }

    private void showSessionWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sesión por expirar");
        builder.setMessage("Tu sesión está por expirar por inactividad. ¿Deseas continuar?");
        builder.setCancelable(false);

        builder.setPositiveButton("Sí, continuar", (dialog, which) -> {
            // Usuario responde → reinicia el temporizador
            resetInactivityTimers();
        });

        builder.setNegativeButton("Cerrar sesión", (dialog, which) -> {
            logoutUser();
        });

        builder.show();

        // Si no responde en 5 minutos, cerrar sesión
        logoutRunnable = () -> logoutUser();
        handler.postDelayed(logoutRunnable, FINAL_TIMEOUT);
    }

    private void logoutUser() {
        // Aquí limpias la sesión (SharedPreferences o lo que uses)
        Intent intent = new Intent(this, MainActivity.class); // Donde está el LoginFragment
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
