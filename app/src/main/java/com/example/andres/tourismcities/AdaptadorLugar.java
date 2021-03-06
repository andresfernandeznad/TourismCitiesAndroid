package com.example.andres.tourismcities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.andres.tourismcities.modelos.Lugar;

import java.util.List;

public class AdaptadorLugar extends RecyclerView.Adapter<AdaptadorLugar.LugarHolder> {

    private int layout;
    private static Context contexto;
    private List<Lugar> lugares = null;
    private OnItemClickListener listener;
    private int posicion;

    public AdaptadorLugar(int layout, Context contexto, List<Lugar> lugares, OnItemClickListener listener) {
        this.layout = layout;
        this.contexto = contexto;
        this.lugares = lugares;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LugarHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(this.layout, null);

        LugarHolder lugarHolder = new LugarHolder(v);

        return lugarHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LugarHolder lugarHolder, int i) {
        lugarHolder.bindItem(this.lugares.get(i), this.listener);
    }

    @Override
    public int getItemCount() {
        return this.lugares.size();
    }

    public int getPosicion() {
        return posicion;
    }

    public class LugarHolder extends RecyclerView.ViewHolder /*implements View.OnCreateContextMenuListener*/ {
        public TextView textView;
        public ImageView imageView;
        public Lugar lugar;

        public LugarHolder(@NonNull View itemView) {
            super(itemView);
            //itemView.setOnCreateContextMenuListener(this);

            textView = itemView.findViewById(R.id.nombreLugar);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    posicion = getAdapterPosition();
                    return false;
                }
            });
            imageView = itemView.findViewById(R.id.imagenLugar);
        }

        public void bindItem(final Lugar lugar, final OnItemClickListener listener) {
            textView.setText(lugar.getNombre());

            // String nombreImg = lugar.getImagen().substring(0, lugar.getImagen().length() - 4);
            //imageView.setImageResource(contexto.getResources().getIdentifier("@drawable/" + nombreImg, null, contexto.getPackageName()));
            Glide.with(contexto.getApplicationContext()).load(lugar.getImagen()).into(imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(lugar, getAdapterPosition());
                }
            });
        }

        //@Override
        //public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            /**
             * todo Cambiar el menú contextual por el método de Antonio
             * para así que utilizando el mismo adaptador pueda tener distintos menús contextuales
             */
        //    contextMenu.add(getAdapterPosition(), 1, 0, "Añadir a favoritos");
            /*MenuInflater menuInflater = ((PostLogin) contexto).getMenuInflater();
            menuInflater.inflate(R.menu.menu_lugar_anyadirfav, contextMenu);*/
        //}
    }

    /**
     * Implementamos una interface que nos permitirá definir el evento que responderá
     * a una pulsación del usuario sobre uno de los ítems del RecyclerView. El evento
     * recibirá el ítem sobre el que hemos pulsado y la posición de éste en el adapta-
     * dor.
     */
    public interface OnItemClickListener {
        void onItemClick(Lugar lugar, int position) ;
    }
}
