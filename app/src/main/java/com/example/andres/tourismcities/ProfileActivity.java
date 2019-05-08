package com.example.andres.tourismcities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.andres.tourismcities.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {

    private static Usuario usuario;
    private EditText textViewUsuario, textViewEmail;
    private ImageView imageView;
    protected static Uri uriRes;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        usuario = (Usuario) intent.getSerializableExtra("usuario");
        imageView = findViewById(R.id.imagenPerfil);
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        getDownloadUrlUser(storage.getReference().child("users/"+usuario.getIdUsuario()));
        textViewUsuario = findViewById(R.id.nombreApellidoUsuario);
        textViewEmail = findViewById(R.id.emailUsuario);
        if ( uriRes != null ) {
            Glide.with(getApplicationContext()).load(uriRes).dontTransform().into(imageView);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
        getSupportActionBar().setSubtitle("Perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textViewUsuario.setText(usuario.getNombre());
        textViewEmail.setText(usuario.getApellidos());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            progressBar.setVisibility(View.VISIBLE);
            Bundle extras = data.getExtras();
            final Bitmap imageBitmap = (Bitmap) extras.get("data");

            FirebaseStorage storage = FirebaseStorage.getInstance();
            String idUsuario = usuario.getIdUsuario();

            // Creamos una referencia a la carpeta y el nombre de la imagen donde se guardara
            StorageReference mountainImagesRef = storage.getReference().child("users/"+idUsuario);
            getDownloadUrlUser(mountainImagesRef);

            //Pasamos la imagen a un array de byte
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] datas = baos.toByteArray();
            // Empezamos con la subida a Firebase
            UploadTask uploadTask = mountainImagesRef.putBytes(datas);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    imageView.setImageBitmap(imageBitmap);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    protected void getDownloadUrlUser(StorageReference storageReference) {
        progressBar.setVisibility(View.VISIBLE);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).dontTransform().into(imageView);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("usuario/" + usuario.getIdUsuario());
        usuario.setNombre(textViewUsuario.getText().toString());
        usuario.setApellidos(textViewEmail.getText().toString());
        PostLogin.usuario.setNombre(textViewUsuario.getText().toString());
        PostLogin.usuario.setApellidos(textViewEmail.getText().toString());
        myRef.child("nombre").setValue(textViewUsuario.getText().toString());
        myRef.child("apellidos").setValue(textViewEmail.getText().toString());
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
