package com.example.andres.tourismcities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.andres.tourismcities.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText nombre, apellidos, usuario, email, pass, passConf;

    private Button registerBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Bundle bund = getIntent().getExtras();

        nombre = findViewById(R.id.nombreRegistro);
        apellidos = findViewById(R.id.apellidosRegistro);
        usuario = findViewById(R.id.usuarioRegistro);
        email = findViewById(R.id.correoRegistro);
        pass = findViewById(R.id.passwordRegistro);
        passConf = findViewById(R.id.passwordRegistroConf);
        progressBar = findViewById(R.id.progress2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);

        email.setText(bund.getString("usu"));

        registerBtn = findViewById(R.id.botonRegistro);

        InputFilter alfabeticos = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i))) {
                        Toast.makeText(RegisterActivity.this, "Error alfabético", Toast.LENGTH_SHORT).show();
                        //Cuando encuentra un caracter que no sea una letra
                        return "";
                    }
                }

                return null;
            }
        };

        InputFilter alfanumericos = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i)) && !Character.isDigit(source.charAt(i))) {
                        Toast.makeText(getApplicationContext(), "Carácter no válido", Toast.LENGTH_SHORT).show();

                        return "";
                    }
                }

                return null;
            }
        };

        nombre.setFilters(new InputFilter[] {alfabeticos});
        usuario.setFilters(new InputFilter[] {alfanumericos, new InputFilter.LengthFilter(16)});
        apellidos.setFilters(new InputFilter[] {alfabeticos});
        pass.setFilters(new InputFilter[] {new InputFilter.LengthFilter(15)});
        passConf.setFilters(new InputFilter[] {new InputFilter.LengthFilter(15)});

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean enviar = true;
                if (usuario.getText().toString().length() < 5) {
                    Toast.makeText(getApplicationContext(), "La longitud mínima de usuario es 5", Toast.LENGTH_SHORT).show();
                    enviar = false;
                }

                if (pass.getText().toString().length() < 5 || passConf.getText().toString().length() < 5) {
                    Toast.makeText(getApplicationContext(), "La longitud mínima de contraseña es 5", Toast.LENGTH_SHORT).show();
                    enviar = false;
                } else if (!pass.getText().toString().equals(passConf.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
                    enviar = false;
                }

                if (enviar) {
                    progressBar.setVisibility(View.VISIBLE);
                    //Toast.makeText(getApplicationContext(), "Te estás registrando", Toast.LENGTH_SHORT).show();
                    String usuario = email.getText().toString().trim();
                    String clave = pass.getText().toString().trim();
                    //Si se puede enviar el registro hacemos lo siguiente
                    //Guarda información del usuario
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(usuario, clave).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Este método se lanzará cuando se cumpla la autenticación
                                // Si nuestro usuario tiene más información asociada tendremos que guardarla en la BBDD.
                                //Obtenemos una instancia de la BBDD asocida a nuestra aplicación
                                db = FirebaseDatabase.getInstance();

                                //Obtenemos una referencia al documento(tabla) que contendrá la información
                                //Si el documento no existe firebase lo crea!!
                                DatabaseReference ref = db.getReference("usuario");

                                //Obtener los datos proporcionados por firebase del usuario registrado
                                FirebaseUser fbUser = mAuth.getCurrentUser();

                                //Preguntamos por el identificador(UID) del usuario
                                String uid = fbUser.getUid();

                                //Creamos nuestro objeto usuario con los datos proporcionados a través del formulario
                                Usuario miUsuario = new Usuario(uid, nombre.getText().toString(), apellidos.getText().toString(),
                                        email.getText().toString(), null);

                                //Guardamos la información en la BBDD de Firebase asociados al UID.
                                ref.child(uid).setValue(miUsuario);
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Se ha creado con éxito", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                                intent.putExtra("email", email.getText().toString());

                                finish();
                                startActivity(intent);
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Error en el registro", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
