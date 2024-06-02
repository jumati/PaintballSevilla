package com.jm.paintballsevilla;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jm.paintballsevilla.fragments.EditarUsuario;
import com.jm.paintballsevilla.fragments.ListaActividades;
import com.jm.paintballsevilla.fragments.ListaFavoritos;
import com.jm.paintballsevilla.fragments.ListaUsers;
import com.jm.paintballsevilla.model.Users;

public class MasterActivity extends AppCompatActivity {

    private ListaActividades actividades_fragment = new ListaActividades();
    private ListaUsers lista_usuarios_fragment = new ListaUsers();
    private ListaFavoritos favoritos_fragment = new ListaFavoritos();
    private EditarUsuario editar_usuario_fragment = new EditarUsuario();
    private FirebaseAuth auth;
    private FirebaseFirestore mFirestore;
    private Users user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        // Instancia de Auth y Firestore
        auth = FirebaseAuth.getInstance();
        // mFirestore = FirebaseFirestore.getInstance();

        // Recoger el objeto Users del intent
        Intent intent = getIntent();
        user = (Users) intent.getParcelableExtra("usuario");

        // Pasa el objeto Users al fragmento EditarUsuario
        Bundle bundle = new Bundle();
        bundle.putParcelable("usuario", user);
        editar_usuario_fragment.setArguments(bundle);

        BottomNavigationView navigation = findViewById(R.id.nav2);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Carga por defecto el fragmento de ListaActividades al iniciar MasterActivity
        loadFragment(actividades_fragment);

    }




    /**
     * Selecciona la opción del menú superior que lleva a una nueva activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean t = false;
        int id = item.getItemId();
        if (id == R.id.menu_new_paintball)
        {
            // Nueva Activity para crear un paintball
            Intent intent = new Intent(MasterActivity.this, CrearActividadActivity.class);
            intent.putExtra("usuario", user);
            startActivity(intent);
            t = true;
        }
        else if (id == R.id.menu_refresh_master)
        {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.layout_master);
            if (currentFragment != null)
            {
                // Crear una nueva instancia del mismo fragmento
                Fragment newInstance = null;
                if (currentFragment instanceof ListaActividades)
                {
                    newInstance = new ListaActividades();
                }
                else if (currentFragment instanceof ListaUsers)
                {
                    newInstance = new ListaUsers();
                }
                else if (currentFragment instanceof ListaFavoritos)
                {
                    newInstance = new ListaFavoritos();
                }
                // Reemplazar el fragmento actual por la nueva instancia del mismo fragmento
                if (newInstance != null)
                {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.layout_master, newInstance);
                    transaction.commit();
                }
            }
            t = true;
        }
        return t;
    }




    /**
     * Selecciona el fragmento que va a cargar en la pantalla
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            boolean t = false;
            if(R.id.actividades_fragment == item.getItemId())
            {
                loadFragment(actividades_fragment);
                t = true;
            }
            if(R.id.lista_usuarios_fragment == item.getItemId())
            {
                loadFragment(lista_usuarios_fragment);
                t = true;
            }
            else if(R.id.favoritos_fragment == item.getItemId())
            {
                loadFragment(favoritos_fragment);
                t = true;
            }
            else if(R.id.editar_usuario_fragment == item.getItemId())
            {
                loadFragment(editar_usuario_fragment);
                t = true;
            }
            else if(R.id.logOut == item.getItemId())
            {
                // LOGOUT
                logOut();
                t = true;
            }
                return t;
        }
    };



    /**
     * LOGOUT
     *
     * Pregunta por mensaje antes de cerrar sesión
     */
    private void logOut()
    {
        // Mensaje que pregunta si se quiere cerrar sesión.
        new AlertDialog.Builder(this)
                .setTitle("¿Desea cerrar sesión?")
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // LOGOUT
                        auth.signOut();
                        // Volver a la pantalla login
                        startActivity(new Intent(MasterActivity.this, LoginActivity.class));
                    }
                }).show();
    }


    /**
     * Carga el fragmento
     */
    public void loadFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layout_master, fragment);
        transaction.commit();
    }
}