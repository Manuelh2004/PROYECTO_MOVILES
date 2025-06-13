package com.example.proyecto_moviles.ui.Presupuesto;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PresupuestoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PresupuestoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}