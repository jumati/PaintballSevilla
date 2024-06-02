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
import com.jm.paintballsevilla.fragments.EditarUsuario;
import com.jm.paintballsevilla.fragments.ListaActividadesNoMaster;
import com.jm.paintballsevilla.fragments.ListaFavoritos;
import com.jm.paintballsevilla.fragments.ListaFavoritosNoMaster;
import com.jm.paintballsevilla.model.Users;

public class NoMasterActivity extends AppCompatActivity {

    ListaActividadesNoMaster actividades_no_master_fragment = new ListaActividadesNoMaster();
    ListaFavoritosNoMaster favoritos_no_master_fragment = new ListaFavoritosNoMaster();
    EditarUsuario editar_usuario_fragment = new EditarUsuario();
    FirebaseAuth auth;
    private Users user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_master);

        // Instancia de Auth
        auth = FirebaseAuth.getInstance();

        // Recoger el objeto Users del intent
        Intent intent = getIntent();
        user = (Users) intent.getParcelableExtra("usuario");

        // Pasa el objeto Users al fragmento EditarUsuario
        Bundle bundle = new Bundle();
        bundle.putParcelable("usuario", user);
        editar_usuario_fragment.setArguments(bundle);

        BottomNavigationView navigation = findViewById(R.id.nav3);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Carga por defecto el fragmento de ListaActividades al iniciar MasterActivity
        loadFragment(actividades_no_master_fragment);

    }




    /**
     * Selecciona la opción del menú superior que lleva a una nueva activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_no_master, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean t = false;
        int id = item.getItemId();
        if (id == R.id.menu_refresh_no_master)
        {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.layout_no_master);
            if (currentFragment != null)
            {
                // Crear una nueva instancia del mismo fragmento
                Fragment newInstance = null;
                if (currentFragment instanceof ListaActividadesNoMaster)
                {
                    newInstance = new ListaActividadesNoMaster();
                }
                else if (currentFragment instanceof ListaFavoritos)
                {
                    newInstance = new ListaFavoritosNoMaster();
                }
                // Reemplazar el fragmento actual por la nueva instancia del mismo fragmento
                if (newInstance != null)
                {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.layout_no_master, newInstance);
                    transaction.commit();
                }
            }
            t = true;
        }
        return t;
    }


    /**
     * Selecciona el fragmento que va a cargar
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            boolean t = false;
            if(R.id.actividades_no_master_fragment == item.getItemId())
            {
                loadFragment(actividades_no_master_fragment);
                t = true;
            }
            else if(R.id.favoritos_no_master_fragment == item.getItemId())
            {
                loadFragment(favoritos_no_master_fragment);
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
                        startActivity(new Intent(NoMasterActivity.this, LoginActivity.class));
                    }
                }).show();
    }


    /**
     * Carga el fragmento
     */
    public void loadFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layout_no_master, fragment);
        transaction.commit();
    }
}