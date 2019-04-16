package com.example.andres.tourismcities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.andres.tourismcities.modelos.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private Usuario usuario;
    private TextView textView;
    private ImageView imageView;
    private final int GALLERY = 1, CAMERA = 2;
    private Button btn;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        storageReference = FirebaseStorage.getInstance().getReference();
        usuario = (Usuario) intent.getSerializableExtra("usuario");
        try {
            downloadProfilePhoto();
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView = findViewById(R.id.nombreApellidoUsuario);
        imageView = findViewById(R.id.imagenPerfil);
        btn = findViewById(R.id.btn);
        getSupportActionBar().setSubtitle("Perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textView.setText(usuario.getNombreCompleto());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoImagen();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoImagen();
            }
        });
    }

    private void mostrarDialogoImagen() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("¿Qué quieres hacer?");
        String[] pictureDialogItems = {
                "Escoge una foto de la galería",
                "Hacer una foto con la cámara" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                abrirGaleria();
                                break;
                            case 1:
                                abrirCamara();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void abrirGaleria() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void abrirCamara() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap imagenGaleria = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = guardarImagen(imagenGaleria);
                    Toast.makeText(ProfileActivity.this, "Imagen guardada", Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(imagenGaleria);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Error fatal", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap imagenCam = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(imagenCam);
            guardarImagen(imagenCam);
            Toast.makeText(ProfileActivity.this, "Imagen guardada", Toast.LENGTH_SHORT).show();
        }
    }

    public String guardarImagen(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "/imagenesPerfil");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            subirFotosAlStorage(f);
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "Archivo guardado;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void subirFotosAlStorage(File file) {
        Uri fileUrl = Uri.fromFile(file);
        StorageReference riversRef = storageReference.child("UsersPhotos/" + usuario.getEmail());

        riversRef.putFile(fileUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.e("DESCARGADA", downloadUrl.toString());
                        usuario.setUrlProfilePhoto(downloadUrl);
                        FirebaseDatabase db = FirebaseDatabase.getInstance() ;

                        // Creamos una referencia al documento USUARIOS
                        DatabaseReference ref = db.getReference("usuario") ;
                        ref.child(usuario.getIdUsuario() + "/photoURL").setValue(downloadUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
    }

    private void downloadProfilePhoto() throws IOException {
        StorageReference riversRef = storageReference.child("UsersPhotos/" + usuario.getEmail() + ".jpg");
        File localFile = File.createTempFile("images", "jpg");
        riversRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
