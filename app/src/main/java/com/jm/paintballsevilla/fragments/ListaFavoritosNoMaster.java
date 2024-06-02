package com.jm.paintballsevilla.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jm.paintballsevilla.InfoActividadActivity;
import com.jm.paintballsevilla.R;
import com.jm.paintballsevilla.adapter.FavAdapter;
import com.jm.paintballsevilla.model.Actividades;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Map;

public class ListaFavoritosNoMaster extends Fragment {


    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private RecyclerView mRecycler;
    private FavAdapter mFavAdapter;
    private ArrayList<Actividades> listaFav;
    private FirebaseUser currentUser;
    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_favoritos_no_master, container, false);

        // Inicializas
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        listaFav = new ArrayList<>();

        // email del usuario actualmente logueado
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        // RecyclerView
        mRecycler = view.findViewById(R.id.recycler_fav_no_master);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        // Adapter
        mFavAdapter = new FavAdapter(listaFav);
        mRecycler.setAdapter(mFavAdapter);

        // Acceder a cada view
        mFavAdapter.setOnItemClickListener(new FavAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Actividades actividad) {
                // Accedemos a una Activity que permite ver y editar la actividad
                Intent intent = new Intent(getActivity(), InfoActividadActivity.class);
                intent.putExtra("actividad", actividad);
                startActivity(intent);
            }
        });

        // Obtener datos de Firestore
        obtenerFavoritos();

        return view;
    }


    /**
     * Comprueba la autenticación de Auth y almacena el correo.
     * Posteriormente busca a ese usuario en Firestore a través del correo.
     * Luego guarda el mapa del campo fav del usuario en un mapa local.
     * Por último llama al método encargado de filtrar la información
     */
    private void obtenerFavoritos() {
        DocumentReference userRef = mFirestore.collection("Users").document(userId);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> userData = documentSnapshot.getData();
                    if (userData != null && userData.containsKey("fav"))
                    {
                        Map<String, Object> favMap = (Map<String, Object>) userData.get("fav");
                        obtenerActividades(favMap);
                    }

                } else {
                    Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Recorre el mapa con los favoritos y luego comprueba que estén en Firestore en Actividades.
     *
     * He puesto logs por todos lados porque no consigo que funcione.
     * Ya funciona pero voy a dejar los logs para monitorear
     * los posibles problemas que puedan surgir.
     *
     * @param favMap
     */
    private void obtenerActividades(final Map<String, Object> favMap) {
        listaFav.clear();
        mFirestore.collection("Actividades")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String id = document.getId();
                                if (favMap.containsKey(id))
                                {
                                    String nombre = document.getString("nombre");
                                    String zona = document.getString("zona");
                                    String fecha = document.getString("fecha");
                                    Long plazasLong = document.getLong("plazas");
                                    int plazas = plazasLong != null ? plazasLong.intValue() : 0;
                                    String descripcion = document.getString("descripcion");

                                    Actividades actividad = new Actividades(id, nombre, zona, fecha, plazas, descripcion);
                                    actividad.setId(id);
                                    listaFav.add(actividad);
                                    Log.d("ListaFavoritos", "Actividad agregada: " + actividad.toString());
                                }
                            }
                            mFavAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("ListaFavoritos", "No se encontraron actividades.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error al obtener las actividades.", Toast.LENGTH_SHORT).show();
                        Log.e("ListaFavoritos", "Error al obtener las actividades.", e);
                    }
                });
    }
}