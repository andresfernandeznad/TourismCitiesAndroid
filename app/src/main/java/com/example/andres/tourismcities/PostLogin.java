package com.example.andres.tourismcities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.andres.tourismcities.modelos.Lugar;
import com.example.andres.tourismcities.modelos.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class PostLogin extends AppCompatActivity {

    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private static List<Lugar> lugares = new ArrayList<Lugar>();

    private Usuario usuario = null;

    //private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);

        final Intent intent = getIntent();

        Boolean botonGoogle = intent.getBooleanExtra("botonGoogle", true);

        if (!botonGoogle) {
            usuario = (Usuario) intent.getSerializableExtra("usuario");
        }

        lugares = (List<Lugar>) intent.getSerializableExtra("lugares");

        //googleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).build();

        getSupportActionBar().setSubtitle("Todos los lugares");

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
        if (usuario != null) Toast.makeText(this, "¡Bienvenido/a " + usuario.getNombre() + "!" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        lugares.clear();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lugar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.cerrarsesionmenu:
                super.finish();
                break;

            case R.id.mislugaresmenu:
                Toast.makeText(getApplicationContext(), "Se abre mis lugares", Toast.LENGTH_SHORT).show();
                break;
            case R.id.perfilmenu:
                Toast.makeText(getApplicationContext(), "Se abre mi perfil", Toast.LENGTH_SHORT).show();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
