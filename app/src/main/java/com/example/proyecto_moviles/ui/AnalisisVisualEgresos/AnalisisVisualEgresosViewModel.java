package com.example.proyecto_moviles.ui.AnalisisVisualEgresos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AnalisisVisualEgresosViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AnalisisVisualEgresosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}