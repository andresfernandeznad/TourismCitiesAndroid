package com.example.andres.tourismcities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.andres.tourismcities.modelos.Favoritos
import com.example.andres.tourismcities.modelos.Lugar
import com.example.andres.tourismcities.modelos.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList

class PostLoginAdmin : AppCompatActivity() {

    internal var recyclerView: RecyclerView? = null
    private var adapter: AdaptadorLugar? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    private var lugares: MutableList<Lugar> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_login_admin)
        val intent = intent
        lugares = intent.getSerializableExtra("lugares") as MutableList<Lugar>

        supportActionBar!!.setSubtitle("Todos los lugares")
        recyclerView = findViewById < RecyclerView >(R.id.recyclerLugares)

        layoutManager = LinearLayoutManager(this)
        recyclerView!!.setLayoutManager(layoutManager)
        adapter = AdaptadorLugar(R.layout.lugar, this, lugares, AdaptadorLugar.OnItemClickListener { lugar, position ->
            Toast.makeText(this, "test", Toast.LENGTH_LONG).show()
        })
        recyclerView!!.setAdapter(adapter)
        registerForContextMenu(recyclerView)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.borrarlugar, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val lugar = lugares[adapter!!.getPosicion()]
        borrarLugar(lugar)
        //Toast.makeText(this, "Se añade a favoritos " + lugaresFavoritos.getLugaresFavoritos().get(adapter.getPosicion() - 1).getNombre(), Toast.LENGTH_LONG).show() ;
        return super.onContextItemSelected(item)
    }

    private fun borrarLugar(lugar: Lugar) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("lugar/" + lugar.nombre)
        lugares.removeAt(adapter!!.getPosicion())
        ref.removeValue()
        adapter!!.notifyItemRemoved(adapter!!.getPosicion())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.cerrarsesionsolo, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.cerrarsesionmenu -> {
                FirebaseAuth.getInstance().signOut()
                super.finish()
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        lugares.clear()
        FirebaseAuth.getInstance().signOut()
        super.onBackPressed()
    }
}
