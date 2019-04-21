package com.example.andres.tourismcities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostLogin extends AppCompatActivity {

    RecyclerView recyclerView;
    private AdaptadorLugar adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton floatingActionButton;

    private static List<Lugar> lugares = new ArrayList<Lugar>();

    private Usuario usuario = null;

    private static Favoritos lugaresFavoritos = null;

    //private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);

        final Intent intent = getIntent();

        pedirPermisos();
        usuario = (Usuario) intent.getSerializableExtra("usuario");


        lugares = (List<Lugar>) intent.getSerializableExtra("lugares");

        //googleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).build();

        getSupportActionBar().setSubtitle("Todos los lugares");

        recyclerView = findViewById(R.id.recyclerLugares);

        layoutManager = new LinearLayoutManager(this);
        floatingActionButton = findViewById(R.id.anyadirFoto);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
            }
        });

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Intent intent = new Intent(getApplicationContext(), NuevoLugar.class);
            Bundle extras = data.getExtras();
            intent.putExtra("lugarBundle", extras);
            startActivity(intent);
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            FirebaseStorage storage = FirebaseStorage.getInstance();
            String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());

            // Creamos una referencia a la carpeta y el nombre de la imagen donde se guardara
            //todo Añadir la url de los lugares ponerle el nombre sacándolo de la ubicación de alguna forma
            StorageReference mountainImagesRef = storage.getReference().child("lugar/"+timeStamp+".jpg");
//Pasamos la imagen a un array de byte
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] datas = baos.toByteArray();

// Empezamos con la subida a Firebase
            UploadTask uploadTask = mountainImagesRef.putBytes(datas);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getBaseContext(),"Hubo un error",Toast.LENGTH_LONG);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getBaseContext(),"Subida con exito",Toast.LENGTH_LONG);

                }
            });
        }
    }

    private void pedirPermisos(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getApplicationContext(), "Todos los permisos garantizados.", Toast.LENGTH_SHORT).show();
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    public void onBackPressed() {
        lugares.clear();
        FirebaseAuth.getInstance().signOut();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lugar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_lugar_anyadirfav, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Lugar seAnyade = lugares.get(adapter.getPosicion());
        addFavoritos(seAnyade);
        //Toast.makeText(this, "Se añade a favoritos " + lugaresFavoritos.getLugaresFavoritos().get(adapter.getPosicion() - 1).getNombre(), Toast.LENGTH_LONG).show() ;
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.cerrarsesionmenu:
                lugaresFavoritos = null;
                FirebaseAuth.getInstance().signOut();
                super.finish();
                break;

            case R.id.mislugaresmenu:
                Intent intent = new Intent(getApplicationContext(), FavsActivity.class);
                intent.putExtra("lugaresFavoritos", lugaresFavoritos);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
                break;
            case R.id.perfilmenu:
                // todo Que se abre actividad en la que se pueda ver mi perfil y configuración sobre el mismo
                intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("usuario", usuario);
                //Toast.makeText(getApplicationContext(), "Se abre mi perfil", Toast.LENGTH_SHORT).show();
                startActivity(intent);
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
                                int lugarFirebaseFav = Integer.parseInt(jsonObject.get("posicionFirebaseFav").toString());
                                //Error de que esta malformado
                                /*String descripcion = jsonObject.get("descripcion").toString().replaceAll("^\"|\"$", "");
                                descripcion = descripcion.replace(".", " ");*/
                                Lugar lugar = new Lugar(nombre, latitud, longitud, imagen, "descripcion");
                                lugar.setPosicionFirebaseFav(lugarFirebaseFav);
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
        Toast.makeText(getApplicationContext(), "Se añade a favoritos " + seAnyade.getNombre(), Toast.LENGTH_SHORT).show();
        seAnyade.setPosicionFirebaseFav(lugaresFavoritos.getLugaresFavoritos().size());
        lugaresFavoritos.addLugar(seAnyade);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("favoritos/" + usuario.getIdUsuario());
        ref.setValue(lugaresFavoritos);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
