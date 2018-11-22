package com.example.andres.tourismcities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres.tourismcities.modelos.Lugar;

import java.util.List;

public class AdaptadorLugar extends RecyclerView.Adapter<AdaptadorLugar.LugarHolder> {

    private int layout;
    private static Context contexto;
    private List<Lugar> lugares = null;
    private OnItemClickListener listener;

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

    public class LugarHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public Lugar lugar;

        public LugarHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.nombreLugar);
            imageView = itemView.findViewById(R.id.imagenLugar);
        }

        public void bindItem(final Lugar lugar, final OnItemClickListener listener) {
            textView.setText(lugar.getNombre());

            String nombreImg = lugar.getImagen().substring(0, lugar.getImagen().length() - 4);
            imageView.setImageResource(contexto.getResources().getIdentifier("@drawable/" + nombreImg, null, contexto.getPackageName()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(lugar, getAdapterPosition());
                }
            });
        }
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
