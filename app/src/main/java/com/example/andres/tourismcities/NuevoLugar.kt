package com.example.andres.tourismcities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.andres.tourismcities.modelos.Lugar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class NuevoLugar : AppCompatActivity() {

    private var cancelarBoton: Button? = null
    private var anyadirBoton: Button? = null
    private var imageView: ImageView? = null
    private var nombre: EditText? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_lugar)
        val intent = intent
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        imageView = findViewById(R.id.imageView2)
        var imageView2 = imageView
        nombre = findViewById(R.id.editText)
        cancelarBoton = findViewById(R.id.cancelarFoto)
        anyadirBoton = findViewById(R.id.anyadirFotoBoton)
        var bundle = intent.getBundleExtra("lugarBundle")
        var imagen: Bitmap = bundle["data"] as Bitmap
        imageView2!!.setImageBitmap(imagen)
        cancelarBoton?.setOnClickListener {
            super.finish()
        }
        anyadirBoton?.setOnClickListener {
            subirAFirebase(imagen)
            var lugar: Lugar? = null
            // todo Recoger la localizacion de alguna forma
            fusedLocationClient.lastLocation.addOnSuccessListener {
                lugar = Lugar(nombre?.text.toString(), it.latitude, it.longitude, "", "")
            }
            if (lugar != null) addLugarToFB(lugar!!)
            super.finish()
        }
    }

    private fun addLugarToFB (lugar: Lugar) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("lugar")
        myRef.child(lugar.nombre).setValue(lugar)
    }

    private fun subirAFirebase(imageBitmap: Bitmap) {
        var firebaseStorage = FirebaseStorage.getInstance()
        var storageReference = firebaseStorage.reference.child("lugar/${nombre?.text.toString()}.jpg")
        var byteArrayOutputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        var datas = byteArrayOutputStream.toByteArray()
        var uploadTask = storageReference.putBytes(datas)
        uploadTask.addOnCompleteListener {
            Toast.makeText(getBaseContext(),"Subida con exito",Toast.LENGTH_LONG);
        }
    }
}
