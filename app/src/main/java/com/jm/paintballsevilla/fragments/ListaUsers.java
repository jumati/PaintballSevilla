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
import com.jm.paintballsevilla.EditarUsuarioActivity;
import com.jm.paintballsevilla.R;
import com.jm.paintballsevilla.adapter.UsersAdapter;
import com.jm.paintballsevilla.model.Users;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class ListaUsers extends Fragment {

    private FirebaseFirestore mFirestore;
    private RecyclerView mRecycler;
    private UsersAdapter mUsersAdapter;
    private ArrayList<Users> listaUsers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_users, container, false);

        // Inicializar Firestore
        mFirestore = FirebaseFirestore.getInstance();
        listaUsers = new ArrayList<>();

        // RecyclerView
        mRecycler = view.findViewById(R.id.recycler_users);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        // Adapter
        mUsersAdapter = new UsersAdapter(listaUsers);
        mRecycler.setAdapter(mUsersAdapter);

        // Acceder a cada view
        mUsersAdapter.setOnItemClickListener(new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Users user) {
                // Accedemos a una Activity que permite ver y editar al usuario.
                Intent intent = new Intent(getActivity(), EditarUsuarioActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        // Obtener datos de Firestore
        obtenerUsers();

        return view;
    }

    /**
     * Recoge los datos a través de una Query y los envía a través de Users user
     */
    private void obtenerUsers() {
        // Se acceden a los datos
        mFirestore.collection("Users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            listaUsers.clear();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String id = document.getId();
                                String email = document.getString("email");
                                String name = document.getString("name");
                                String last_name = document.getString("last_name");
                                int phone = document.getLong("phone").intValue();
                                boolean master = document.getBoolean("master");

                                Users user = new Users(email, name, last_name, phone, master);
                                user.setId(id);
                                listaUsers.add(user);
                            }
                            mUsersAdapter.notifyDataSetChanged();
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