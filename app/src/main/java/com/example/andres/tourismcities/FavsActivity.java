package com.example.andres.tourismcities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.andres.tourismcities.modelos.Favoritos;

public class FavsActivity extends AppCompatActivity {

    private static Favoritos lugaresFavoritos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favs);

        Intent intent = getIntent();
    }
}
