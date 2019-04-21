package com.example.andres.tourismcities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.andres.tourismcities.modelos.Lugar
import com.google.android.gms.common.api.GoogleApiClient
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
    //private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private var hasGPS = false
    private var hasNetwork = false
    private var locationGPS: Location? = null
    private var locationNetwork: Location? = null
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_lugar)
        val intent = intent
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        imageView = findViewById(R.id.imageView2)
        var imageView2 = imageView
        nombre = findViewById(R.id.editText)
        cancelarBoton = findViewById(R.id.cancelarFoto)
        anyadirBoton = findViewById(R.id.anyadirFotoBoton)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var bundle = intent.getBundleExtra("lugarBundle")
        var imagen: Bitmap = bundle["data"] as Bitmap
        imageView2!!.setImageBitmap(imagen)
        cancelarBoton?.setOnClickListener {
            super.finish()
        }
        anyadirBoton?.setOnClickListener {
            subirAFirebase(imagen)
            var lugar: Lugar? = null
//            // todo Recoger la localizacion de alguna forma, hecho
            // todo Crear objeto lugar y añadirlo al recyclerview principal
//            fusedLocationClient.lastLocation.addOnSuccessListener {
//                lugar = Lugar(nombre?.text.toString(), it.latitude, it.longitude, "", "")
//            }
//            if (lugar != null) addLugarToFB(lugar!!)
            getLocation()
            Toast.makeText(this, locationGPS.toString(), Toast.LENGTH_LONG).show()
            Toast.makeText(this, locationNetwork.toString(), Toast.LENGTH_LONG).show()
            super.finish()
        }
        getLocation()
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

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGPS || hasNetwork) {
            if (hasGPS) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object : LocationListener{
                    override fun onLocationChanged(p0: Location?) {
                        if (p0 != null) {
                            locationGPS = p0
                        }
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderEnabled(p0: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderDisabled(p0: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })
                val localGPSLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGPSLocation != null) {
                    locationGPS = localGPSLocation
                }
            }
            if (hasNetwork) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object : LocationListener{
                    override fun onLocationChanged(p0: Location?) {
                        if (p0 != null) {
                            locationNetwork = p0
                        }
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

                    }

                    override fun onProviderEnabled(p0: String?) {

                    }

                    override fun onProviderDisabled(p0: String?) {

                    }

                })
                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null) {
                    locationNetwork = localNetworkLocation
                }
                if (locationGPS != null && locationNetwork != null) {
                    if (locationGPS!!.accuracy > locationNetwork!!.accuracy) {
                        println(locationGPS.toString())
                    } else {
                        println(locationNetwork.toString())
                    }
                }
            }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }
}
