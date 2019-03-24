package com.example.andres.tourismcities;

import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private static List<Lugar> lugares = new ArrayList<Lugar>();

    Button btnLogin, btnRegister;
    EditText nomUsu, contra;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fillFirebaseDB();

        //Para evitar lugares repetidos
        lugares.clear();

        leerFromFirebase();

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

                                                    intent.putExtra("botonGoogle", false);

                                                    // Lanzar la actividad ListActivity
                                                    startActivity(intent) ;

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        myRef.child("malaga").setValue(new Lugar("Málaga", 36.7182015,-4.519307, "malaga.jpg", "descripcion"));
        myRef.child("madrid").setValue(new Lugar("Madrid", 40.4378698,-3.8196207, "madrid.jpg", "descripcion"));
        myRef.child("barcelona").setValue(new Lugar("Barcelona", 41.3947688,2.0787279, "barcelona.jpg", "descripcion"));
        myRef.child("sevilla").setValue(new Lugar("Sevilla", 37.3753501,-6.0250983, "sevilla.jpg", "descripcion"));
        myRef.child("cadiz").setValue(new Lugar("Cadiz", 36.5163813,-6.3174866, "cadiz.jpg", "descripcion"));
        myRef.child("valencia").setValue(new Lugar("Valencia", 39.4077013,-0.5015956, "valencia.png", "descripcion"));
        myRef.child("andorra").setValue(new Lugar("Andorra", 42.5421846,1.4575882, "andorra.jpg", "descripcion"));


    }

    protected void leerFromFirebase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("lugar");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                JsonParser parser = new JsonParser();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String cadenaJSON = dataSnapshot1.getValue().toString();

                    JsonObject jsonObject = parser.parse(cadenaJSON).getAsJsonObject();
                    String nombre = jsonObject.get("nombre").toString().replaceAll("^\"|\"$", "");
                    double latitud = Double.parseDouble(jsonObject.get("latitud").toString());
                    double longitud = Double.parseDouble(jsonObject.get("longitud").toString());
                    String imagen = jsonObject.get("imagen").toString().replaceAll("^\"|\"$", "");
                    String descripcion = jsonObject.get("descripcion").toString().replaceAll("^\"|\"$", "");
                    Lugar lugar = new Lugar(nombre, latitud, longitud, imagen, descripcion);
                    lugares.add(lugar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onRestart() {
        nomUsu.setText("");
        contra.setText("");
        super.onRestart();
    }
}
