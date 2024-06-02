package com.jm.paintballsevilla.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jm.paintballsevilla.EditarActividad;
import com.jm.paintballsevilla.R;
import com.jm.paintballsevilla.adapter.ActividadesAdapterMaster;
import com.jm.paintballsevilla.model.Actividades;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class ListaActividades extends Fragment {

    private FirebaseFirestore mFirestore;
    private RecyclerView mRecycler;
    private ActividadesAdapterMaster mActividadesAdapter;
    private ArrayList<Actividades> listaActividades;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_actividades, container, false);

        // Inicializar Firestore
        mFirestore = FirebaseFirestore.getInstance();
        listaActividades = new ArrayList<>();

        // RecyclerView
        mRecycler = view.findViewById(R.id.recycler_actividades_master);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        // Adapter
        mActividadesAdapter = new ActividadesAdapterMaster(listaActividades);
        mRecycler.setAdapter(mActividadesAdapter);

        // Acceder a cada view
        mActividadesAdapter.setOnItemClickListener(new ActividadesAdapterMaster.OnItemClickListener() {
            @Override
            public void onItemClick(Actividades actividad) {
                // Accedemos a una Activity que permite ver y editar la actividad
                Intent intent = new Intent(getActivity(), EditarActividad.class);
                intent.putExtra("actividad", actividad);
                startActivity(intent);
            }
        });

        // Obtener datos de Firestore
        obtenerActividades();

        return view;
    }

    /**
     * Recoge los datos a través de una Query y los envía a través de Actividades actividad
     */
    private void obtenerActividades() {
        // Se acceden a los datos
        mFirestore.collection("Actividades")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            listaActividades.clear();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String id = document.getId();
                                String nombre = document.getString("nombre");
                                String zona = document.getString("zona");
                                String fecha = document.getString("fecha");
                                int plazas = document.getLong("plazas").intValue();
                                String descripcion = document.getString("descripcion");

                                Actividades actividad = new Actividades(id, nombre, zona, fecha, plazas, descripcion);
                                actividad.setId(id);
                                listaActividades.add(actividad);
                            }
                            mActividadesAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Se ha producido un error al obtener los datos de la base de datos.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}