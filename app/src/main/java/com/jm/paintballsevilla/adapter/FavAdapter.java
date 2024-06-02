package com.jm.paintballsevilla.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jm.paintballsevilla.R;
import com.jm.paintballsevilla.model.Actividades;

import java.util.ArrayList;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.FavViewHolder> {

    private ArrayList<Actividades> listaActividades;
    private OnItemClickListener listener;

    public FavAdapter(ArrayList<Actividades> listaActividades) {
        this.listaActividades = listaActividades;
    }

    @Override
    public FavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_fav, null, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavViewHolder holder, int position) {
        final Actividades actividad = listaActividades.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(actividad);
                }
            }
        });
        holder.nombre.setText(listaActividades.get(position).getNombre());
        holder.zona.setText(listaActividades.get(position).getZona());
        holder.plazas.setText(String.valueOf(listaActividades.get(position).getPlazas()));

        String fecha = listaActividades.get(position).getFecha();
        if (fecha != null && !fecha.isEmpty()) {
            holder.fecha.setText(fecha);
        } else {
            holder.fecha.setText("Fecha no disponible");
        }
    }

    @Override
    public int getItemCount() {
        return listaActividades.size();
    }

    public class FavViewHolder extends RecyclerView.ViewHolder
    {
        TextView nombre, zona, fecha, plazas;
        public FavViewHolder (View itemView)
        {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.view_fav_master_nombre);
            zona = (TextView) itemView.findViewById(R.id.view_fav_master_zona);
            plazas = (TextView) itemView.findViewById(R.id.view_fav_master_plazas);
            fecha = (TextView) itemView.findViewById(R.id.view_fav_master_fecha);
        }

    }

    // Métodos para acceder a la actividad cuando se haga click sobre ella
    public interface OnItemClickListener {
        void onItemClick(Actividades actividad);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
