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
        Log.d("ad", "onContextItemSelected: ");
        switch (item.getItemId()) {
            case 1: //Es el id del item del menu que hemos puesto en el adaptador, lo ponemos a mano no cogiendolo del layout
                Lugar seAnyade = lugares.get(item.getGroupId());
                addFavoritos(seAnyade);
                Toast.makeText(getApplicationContext(), "Se añade a mi lista de favoritos " + seAnyade.getNombre(), Toast.LENGTH_SHORT).show();

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
                lugaresFavoritos = null;
                super.finish();
                break;

            case R.id.mislugaresmenu:
                Intent intent = new Intent(getApplicationContext(), FavsActivity.class);
                intent.putExtra("lugaresFavoritos", lugaresFavoritos);

                startActivity(intent);
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
                JsonParser parser = new JsonParser();
                /**
                 * Me crea en la bbdd unos nuevos favoritos si el usuario es nuevo
                 */
                if (dataSnapshot.getValue() == null) {
                    crearNuevosFavsEnFirebase(idUsuario);
                } else {
                    lugaresFavoritos = new Favoritos(idUsuario);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (!snapshot.getKey().equals("id")) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                String lugarJSON = snapshot1.getValue().toString();
                                JsonObject jsonObject = parser.parse(lugarJSON).getAsJsonObject();
                                String nombre = jsonObject.get("nombre").toString().replaceAll("^\"|\"$", "");
                                double latitud = Double.parseDouble(jsonObject.get("latitud").toString());
                                double longitud = Double.parseDouble(jsonObject.get("longitud").toString());
                                String imagen = jsonObject.get("imagen").toString().replaceAll("^\"|\"$", "");
                                //Error de que esta malformado
                                /*String descripcion = jsonObject.get("descripcion").toString().replaceAll("^\"|\"$", "");
                                descripcion = descripcion.replace(".", " ");*/
                                Lugar lugar = new Lugar(nombre, latitud, longitud, imagen, "descripcion");
                                lugaresFavoritos.addLugar(lugar);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Creamos una nueva lista de favoritos en Firebase ya que es un usuario nuevo en la aplicación
     * @param idUsuario
     */
    protected void crearNuevosFavsEnFirebase(String idUsuario) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("favoritos/" + idUsuario);
        lugaresFavoritos = new Favoritos(idUsuario);
        myRef.setValue(new Favoritos(idUsuario, lugaresFavoritos.getLugaresFavoritos()));
    }

    /**
     * Añadimos a la lista local un lugar nuevo favorito.
     * @param seAnyade
     */
    protected void addFavoritos(Lugar seAnyade) {
        lugaresFavoritos.addLugar(seAnyade);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("favoritos/" + usuario.getIdUsuario());
        ref.setValue(lugaresFavoritos);
    }
}
