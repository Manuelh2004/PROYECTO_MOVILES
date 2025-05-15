package com.example.proyecto_moviles.ui.Visualizacion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VisualizacionViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public VisualizacionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}