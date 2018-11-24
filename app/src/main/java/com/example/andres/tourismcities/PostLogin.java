package com.example.andres.tourismcities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.andres.tourismcities.modelos.Favoritos;
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

    private static Favoritos lugaresFavoritos = null;

    //private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);

        final Intent intent = getIntent();


        usuario = (Usuario) intent.getSerializableExtra("usuario");


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

        Toast.makeText(this, "Mantén presionado en el lugar que quieras para añadirlo a favoritos", Toast.LENGTH_SHORT).show();

        Snackbar.make(findViewById(R.id.recyclerLugares), "¡Bienvenido/a " + usuario.getNombre() + "!", Snackbar.LENGTH_LONG).show();

        crearNuevosFavsEnFirebase(usuario.getIdUsuario());

        if (usuario != null) downloadFavsFromFirebase(usuario.getIdUsuario());

        registerForContextMenu(recyclerView);
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
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.anyadirAFavs:
                //todo Añadir el lugar de este elemento del recycler view a mi lista de lugares favoritos.
                Toast.makeText(getApplicationContext(), "Se añade a mi lista de favoritos este lugar", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.cerrarsesionmenu:
                super.finish();
                break;

            case R.id.mislugaresmenu:
                //todo Que se abra actividad en la cual se vean sólo mis lugares favoritos
                Toast.makeText(getApplicationContext(), "Se abre mis lugares", Toast.LENGTH_SHORT).show();
                break;
            case R.id.perfilmenu:
                //todo Que se abre actividad en la que se pueda ver mi perfil y configuración sobre el mismo
                Toast.makeText(getApplicationContext(), "Se abre mi perfil", Toast.LENGTH_SHORT).show();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    protected void downloadFavsFromFirebase(final String idUsuario) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("favoritos/" + idUsuario);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //todo Hay que terminar esto y plantear como hacer para guardar en la base de datos los lugares favoritos

                JsonParser parser = new JsonParser();

                String cadenaJSON = dataSnapshot.getValue().toString();

                JsonObject jsonObject = parser.parse(cadenaJSON).getAsJsonObject();

                lugaresFavoritos = new Favoritos(jsonObject.get("id").toString().replaceAll("^\"|\"$", ""));

                /*if (jsonObject.get("lugares").toString() != null) {
                    String lugar = jsonObject.get("lugares").toString();
                    //lugaresFavoritos.addLugar(null);
                }*/

                /*JsonParser parser = new JsonParser();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String cadenaJSON = dataSnapshot1.getValue().toString();

                    JsonObject jsonObject = parser.parse(cadenaJSON).getAsJsonObject();
                    String nombre = jsonObject.get("nombre").toString().replaceAll("^\"|\"$", "");
                    double latitud = Double.parseDouble(jsonObject.get("latitud").toString());
                    double longitud = Double.parseDouble(jsonObject.get("longitud").toString());
                    String imagen = jsonObject.get("imagen").toString().replaceAll("^\"|\"$", "");
                    String descripcion = jsonObject.get("descripcion").toString().replaceAll("^\"|\"$", "");
                    descripcion = descripcion.replace(".", " ");
                    Lugar lugar = new Lugar(nombre, latitud, longitud, imagen, descripcion);
                    lugares.add(lugar);
                }

                Log.i("TOURISMCITIES:", "Insertados: " + lugares.size()) ;
                Toast.makeText(getApplicationContext(), "AÑADIDOS LUGARES", Toast.LENGTH_SHORT).show();*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    protected void crearNuevosFavsEnFirebase(String idUsuario) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("favoritos/" + idUsuario);

        myRef.setValue(new Favoritos(idUsuario));
    }
}
