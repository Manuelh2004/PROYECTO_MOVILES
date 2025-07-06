package com.example.proyecto_moviles;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MonedaViewModel extends ViewModel {
    private final MutableLiveData<Boolean> mostrarEnDolares = new MutableLiveData<>();

    public void setMostrarEnDolares(boolean valor) {
        mostrarEnDolares.setValue(valor);
    }

    public LiveData<Boolean> getMostrarEnDolares() {
        return mostrarEnDolares;
    }
}
