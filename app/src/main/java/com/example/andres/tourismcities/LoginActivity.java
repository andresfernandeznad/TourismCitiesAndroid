package com.example.andres.tourismcities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.andres.tourismcities.modelos.Lugar;
import com.example.andres.tourismcities.modelos.Usuario;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private static List<Lugar> lugares = new ArrayList<Lugar>();
    private static List<String> lugaresDownloadUrl = new ArrayList<String>();

    Button btnLogin, btnRegister;
    EditText nomUsu, contra;

    private RequestQueue queue;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putSerializable(FirebaseAnalytics.Param.START_DATE, new Date());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        //fillFirebaseDB();

        //Para evitar lugares repetidos
        lugares.clear();

        leerFromFirebase();
        // Añadir forma de coger todo desde firebase
        //setImagenUrl();
        queue = Volley.newRequestQueue(this);
        btnLogin = findViewById(R.id.loginButton);
        btnRegister = findViewById(R.id.registerButton);
        nomUsu = findViewById(R.id.loginUsuario);
        contra = findViewById(R.id.loginPassword);

        nomUsu.setText(getIntent().getStringExtra("email"));

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usr = nomUsu.getText().toString().trim();
                String psw = contra.getText().toString().trim();



                if (usr.isEmpty() || psw.isEmpty()) {
                    Snackbar.make(view, R.string.login_error_login_vacia, Snackbar.LENGTH_LONG).show();
                } else {
                    loginWithFirebase(usr, psw);
                }

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra("usu", nomUsu.getText().toString());
                startActivity(intent);
                /*Intent intent = new Intent(getApplicationContext(), SliderLoginActivity.class);
                startActivity(intent);*/
            }
        });
    }

    private void setImagenUrl() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("downloadsUrl");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                JsonParser parser = new JsonParser();
                int contador = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String downloadUrl = dataSnapshot1.getValue().toString();
                    lugaresDownloadUrl.add(downloadUrl);
                    contador++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loginWithFirebase(String usr, String clv) {

        // Logueamos utilizando Firebase
        String usuario = nomUsu.getText().toString().trim() ;
        String clave   = contra.getText().toString().trim() ;

        if (!usuario.isEmpty() && !clave.isEmpty()) {

            // Obtenemos instancia de Firebase (Authenticate)
            final FirebaseAuth mAuth = FirebaseAuth.getInstance() ;

            // Loguearnos con Firebase utilizando el correo y la contraseña
            mAuth.signInWithEmailAndPassword(usuario, clave)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                // Recuperamos la información de la base de datos
                                // de Firebase
                                FirebaseDatabase db = FirebaseDatabase.getInstance() ;

                                // Creamos una referencia al documento USUARIOS
                                DatabaseReference ref = db.getReference("usuario") ;

                                // Obtenemos la información del usuario
                                ref.child(mAuth.getCurrentUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.exists()) {

                                                    // Rescatamos la información devuelta por Firebase
                                                    Usuario usuario = dataSnapshot.getValue(Usuario.class) ;

                                                    // Creamos la intención
                                                    Intent intent = new Intent(LoginActivity.this, PostLogin.class) ;

                                                    intent.putExtra("usuario", usuario);
                                                    intent.putExtra("lugares", (Serializable) lugares);
                                                    intent.putExtra("lugaresDownload", (Serializable) lugaresDownloadUrl);

                                                    intent.putExtra("botonGoogle", false);

                                                    // Lanzar la actividad ListActivity
                                                    startActivity(intent) ;

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Toast.makeText(LoginActivity.this, "El usuario o la contraseña son erróneos", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }
                    }) ;


        } else {
            Toast.makeText(LoginActivity.this, "El usuario y/o la contraseña no pueden estar vacíos", Toast.LENGTH_LONG).show();
        }

    }

    private void fillFirebaseDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("lugar");

        myRef.child("malaga").setValue(new Lugar("Málaga", 36.7182015,-4.519307, "https://firebasestorage.googleapis.com/v0/b/tourism-cities.appspot.com/o/lugar%2Fmalaga.jpg?alt=media&token=d8842715-74a0-4170-bb13-88460b5c7050", "descripcion"));
        myRef.child("madrid").setValue(new Lugar("Madrid", 40.4378698,-3.8196207, "https://firebasestorage.googleapis.com/v0/b/tourism-cities.appspot.com/o/lugar%2Fmadrid.jpg?alt=media&token=b5dfc1e7-61a2-4170-93d2-41cbe05ec349", "descripcion"));
        myRef.child("barcelona").setValue(new Lugar("Barcelona", 41.3947688,2.0787279, "https://firebasestorage.googleapis.com/v0/b/tourism-cities.appspot.com/o/lugar%2Fbarcelona.jpg?alt=media&token=6826d2b0-fe85-41c2-a8fb-1a437c993481", "descripcion"));
        myRef.child("sevilla").setValue(new Lugar("Sevilla", 37.3753501,-6.0250983, "https://firebasestorage.googleapis.com/v0/b/tourism-cities.appspot.com/o/lugar%2Fsevilla.jpg?alt=media&token=49c11879-97c1-4b46-9cb8-1a7eec5c3f86", "descripcion"));
        myRef.child("cadiz").setValue(new Lugar("Cadiz", 36.5163813,-6.3174866, "https://firebasestorage.googleapis.com/v0/b/tourism-cities.appspot.com/o/lugar%2Fcadiz.jpg?alt=media&token=5b1ca5d8-2057-49ad-bea1-048a5ef00c19", "descripcion"));
        myRef.child("valencia").setValue(new Lugar("Valencia", 39.4077013,-0.5015956, "https://firebasestorage.googleapis.com/v0/b/tourism-cities.appspot.com/o/lugar%2Fvalencia.png?alt=media&token=34372fd2-5a09-4ba4-af1a-7282605be027", "descripcion"));
        myRef.child("andorra").setValue(new Lugar("Andorra", 42.5421846,1.4575882, "https://firebasestorage.googleapis.com/v0/b/tourism-cities.appspot.com/o/lugar%2Fandorra.jpg?alt=media&token=7e8d0adb-a892-453a-a518-06209972c338", "descripcion"));


    }

    protected void leerFromFirebase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("lugar");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                JsonParser parser = new JsonParser();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Object obj = ds.getValue();
                    String nombre = (String) ((HashMap) obj).get("nombre");
                    String imagen = (String) ((HashMap) obj).get("imagen");
                    String descripcion = (String) ((HashMap) obj).get("descripcion");
                    double latitud = (double) ((HashMap) obj).get("latitud");
                    double longitud = (double) ((HashMap) obj).get("longitud");
                    Lugar lugar = new Lugar(nombre, latitud, longitud, imagen, descripcion);
                    lugares.add(lugar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Lugar getDownloadUrl(final Lugar lugar) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference().child("lugar/" + lugar.getImagen());
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                anyadirUrlBaseDeDatos(uri, lugar.getNombre());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        return lugar;
    }

    private void anyadirUrlBaseDeDatos(Uri uri, String nombreImg) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("downloadsUrl");
        myRef.child(nombreImg).setValue(uri.toString());
    }

    @Override
    protected void onRestart() {
        nomUsu.setText("");
        contra.setText("");
        super.onRestart();
    }
}
