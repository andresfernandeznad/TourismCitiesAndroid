package com.example.andres.tourismcities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres.tourismcities.modelos.Lugar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LugarActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView tv;
    ImageView iv;

    GoogleMap map;

    Lugar lugar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugar);

        Intent intent = getIntent();

        lugar = (Lugar) intent.getSerializableExtra("lugar");

        getSupportActionBar().setSubtitle(lugar.getNombre());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv = findViewById(R.id.nombreLug);
        iv = findViewById(R.id.imagenLugar);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        tv.setText(lugar.getNombre());
        iv.setImageResource(getResources().getIdentifier("@drawable/" + lugar.getImagen().substring(0, lugar.getImagen().length() - 4), null, getPackageName()));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (lugar != null) {
            LatLng deLugar = new LatLng(lugar.getLatitud(), lugar.getLongitud());
            int zoom = 8;
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deLugar, zoom));
            googleMap.addMarker(new MarkerOptions().position(deLugar).title(lugar.getNombre()).draggable(true));
            map = googleMap;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
