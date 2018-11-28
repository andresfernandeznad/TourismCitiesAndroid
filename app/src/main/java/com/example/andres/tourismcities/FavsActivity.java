package com.example.andres.tourismcities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Toast;

import com.example.andres.tourismcities.modelos.Favoritos;
import com.example.andres.tourismcities.modelos.Lugar;

import java.util.ArrayList;
import java.util.List;

public class FavsActivity extends AppCompatActivity {

    private static Favoritos lugaresFavoritos = null;
    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Lugar> lugares = new ArrayList<Lugar>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favs);

        Intent intent = getIntent();

        lugaresFavoritos = (Favoritos) intent.getSerializableExtra("lugaresFavoritos");

        lugares = lugaresFavoritos.getLugaresFavoritos();

        getSupportActionBar().setSubtitle("Mis lugares favoritos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerLugares);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdaptadorLugar(R.layout.lugar, this, lugares, new AdaptadorLugar.OnItemClickListener() {
            @Override
            public void onItemClick(Lugar lugar, int position) {
                Intent intent1 = new Intent(getApplicationContext(), LugarActivity.class);
                intent1.putExtra("lugar", lugar);
                startActivity(intent1);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }
}
