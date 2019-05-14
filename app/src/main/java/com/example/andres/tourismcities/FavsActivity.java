package com.example.andres.tourismcities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.andres.tourismcities.modelos.Favoritos;
import com.example.andres.tourismcities.modelos.Lugar;
import com.example.andres.tourismcities.modelos.Usuario;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavsActivity extends AppCompatActivity {

    private static Favoritos lugaresFavoritos = null;
    RecyclerView recyclerView;
    private AdaptadorLugar adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Lugar> lugares = new ArrayList<Lugar>();

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favs);

        Intent intent = getIntent();

        lugaresFavoritos = (Favoritos) intent.getSerializableExtra("lugaresFavoritos");

        usuario = (Usuario) intent.getSerializableExtra("usuario");

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

        registerForContextMenu(recyclerView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_lugar_borrarfav, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Lugar lugar = lugaresFavoritos.getLugaresFavoritos().get(adapter.getPosicion());
        borrarFromFirebase(lugar.getPosicionFirebaseFav(), lugar);
        return super.onContextItemSelected(item);
    }

    private void borrarFromFirebase(int posicion, Lugar lugar) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("favoritos/" + usuario.getIdUsuario() + "/lugaresFavoritos/" + posicion);
        AlertDialog alertDialog = new AlertDialog.Builder(FavsActivity.this).create();
        alertDialog.setTitle("Borrar de favoritos");
        alertDialog.setMessage("¿Estás seguro de borrar " + lugar.getNombre() + " de favoritos?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Se borra de favoritos", Toast.LENGTH_SHORT).show();
                lugaresFavoritos.getLugaresFavoritos().remove(adapter.getPosicion());
                ref.removeValue();
                adapter.notifyItemRemoved(adapter.getPosicion());
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Borrar lugar favorito cancelado", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }
}
