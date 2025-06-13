package com.example.proyecto_moviles.ui.Movimiento;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MovimientoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MovimientoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}