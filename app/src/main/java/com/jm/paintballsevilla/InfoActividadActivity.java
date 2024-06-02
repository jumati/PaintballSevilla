package com.jm.paintballsevilla;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jm.paintballsevilla.model.Actividades;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;


public class InfoActividadActivity extends AppCompatActivity {

    private TextView nombre_textView, zona_textView, plazas_textView, descripcion_textView, mostrar_fecha_textView;
    private Button volver_button, fav_button;
    private FirebaseFirestore mFirestore;
    private DocumentReference actividadRef;
    private Actividades actividad;
    private Intent intent;
    private String userId, actividadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_actividad);

        mFirestore = FirebaseFirestore.getInstance();
        // Obtener uid del usuario autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }
        else {
            Toast.makeText(this, "No se ha encontrado un usuario autenticado", Toast.LENGTH_SHORT).show();
            finish(); // Cerrar actividad si no hay usuario autenticado
            return;
        }

        nombre_textView = findViewById(R.id.info_actividad_nombre);
        zona_textView = findViewById(R.id.info_actividad_zona);
        plazas_textView = findViewById(R.id.info_actividad_plazas);
        descripcion_textView = findViewById(R.id.info_actividad_descripcion);
        mostrar_fecha_textView = findViewById(R.id.info_actividad_fecha);
        volver_button = findViewById(R.id.info_actividad_volver_button);
        fav_button = findViewById(R.id.info_actividad_fav_button);

        /*
        // Lo comento porque lo voy a hacer mejor por un método
        // Mostrar los datos que hemos pasado por el intent anterior.
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("actividad")) {
            Actividades actividad = intent.getParcelableExtra("actividad");
            actividadId = actividad.getId();
            nombre_textView.setText(actividad.getNombre());
            zona_textView.setText(actividad.getZona());
            plazas_textView.setText(String.valueOf(actividad.getPlazas()));
            descripcion_textView.setText(actividad.getDescripcion());
            // mostrar_fecha_textView.setText(actividad.getFecha());
        }*/
        intent = getIntent();
        if (intent != null && intent.hasExtra("actividad"))
        {
            actividad = intent.getParcelableExtra("actividad");
            if (actividad != null)
            {
                String actividadId = actividad.getId();
                actividadRef = mFirestore.collection("Actividades").document(actividadId);
                cargarDatosActividad();
            }
        }
        // Convertir el ID de la actividad a String
        actividadId = String.valueOf(actividad.getId());
        // Llama a la función que define el icono de fav
        cargarDatosFav(userId, actividadId);




        // Pongo el onClick aquí porque me da pereza ponerlo fuera
        fav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarFavoritos(userId, actividadId);
            }
        });




        volver_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simplemente cierra la actividad actual y vuelve a la actividad anterior
                // Siempre prefiero tener el control de a dónde voy
                // pero considero esta opción más viable en este caso
                // ya que no vamos a editar nada, solo mostrar información y regresar.
                finish();
            }
        });

    }




    /**
     * FAVORITOS
     *
     * Añade o elimina la id de la actividad del mapa fav del usuario.
     * También suma o resta 1 al número de plazas según se añada o borre de favoritos.
     *
     * Funciona recogiendo los datos del mapa de firestore, añadiendo el nuevo id al mapa,
     * y reemplazando el mapa antiguo por el nuevo.
     *
     * La parte de las plazas recoge el número de plazas que hay y le suma o resta según sea el favorito.
     * Después se almacena en la base de datos y se actualiza el TextView.
     *
     * FUNCIONA. NO TOCAR!
     *
     * @param userId
     * @param actividadId
     */
    private void actualizarFavoritos(String userId, String actividadId) {
        DocumentReference userRef = mFirestore.collection("Users").document(userId);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> userData = documentSnapshot.getData();
                    if (userData != null && userData.containsKey("fav"))
                    {
                        Map<String, Object> favMap = (Map<String, Object>) userData.get("fav");
                        Map<String, Boolean> updatedFavMap = new HashMap<>();
                        // Convertir claves a cadenas y copiar al nuevo mapa
                        for (Map.Entry<String, Object> entry : favMap.entrySet())
                        {
                            // Verificar si la clave ya es una cadena
                            if (entry.getKey() instanceof String)
                            {
                                updatedFavMap.put((String) entry.getKey(), (Boolean) entry.getValue());
                            } else {
                                // Si la clave no es una cadena, convertirla a cadena y agregarla al nuevo mapa
                                updatedFavMap.put(entry.getKey().toString(), (Boolean) entry.getValue());
                            }
                        }

                        actividad = intent.getParcelableExtra("actividad");
                        int plazas = Integer.parseInt(plazas_textView.getText().toString().trim());
                        boolean isRemovingFavorite = updatedFavMap.containsKey(actividadId);

                        if (isRemovingFavorite)
                        {
                            updatedFavMap.remove(actividadId);
                            plazas++;
                            plazas_textView.setText(String.valueOf(plazas));
                            fav_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_white_50px, 0, 0, 0);
                        }
                        else
                        {
                            if (plazas > 0)
                            {
                                updatedFavMap.put(actividadId, true);
                                plazas--;
                                plazas_textView.setText(String.valueOf(plazas));
                                fav_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_fill_white_50px, 0, 0, 0);
                            }
                            else
                            {
                                Toast.makeText(InfoActividadActivity.this, "No hay plazas disponibles", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        actividad.setPlazas(plazas);
                        // Obtener la referencia al documento que queremos actualizar
                        DocumentReference actividadRef = mFirestore.collection("Actividades").document(actividad.getId());

                        // Actualizar los datos del documento en Firestore
                        actividadRef.update("plazas", plazas)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Actualización correcta

                                        // Actualiza el mapa en Firestore
                                        userRef.update("fav", updatedFavMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(InfoActividadActivity.this, "Favoritos actualizados", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(InfoActividadActivity.this, "Error al actualizar favoritos", Toast.LENGTH_SHORT).show();
                                                        // Si ocurre un error y no se actualiza Firestore, devolvemos el número original a su EditText.
                                                        int plazas2 = Integer.parseInt(plazas_textView.getText().toString().trim());
                                                        // Si se elimina se resta y si no pues no
                                                        plazas_textView.setText(String.valueOf(plazas2 + (isRemovingFavorite ? -1 : 1)));
                                                        // Y devolvemos el número a la base de datos.
                                                        actividadRef.update("plazas", plazas2 + (isRemovingFavorite ? -1 : 1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                            }
                                                        });
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error al actualizar
                                        Toast.makeText(InfoActividadActivity.this, "Error: No se ha podido modificar el número de plazas.", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    else
                    {
                        // Si el campo "fav" no existe, crearlo y agregar el ID de la actividad
                        actividad = intent.getParcelableExtra("actividad");
                        int plazas = Integer.parseInt(plazas_textView.getText().toString().trim());
                        if (plazas > 0) {
                            plazas++;
                            actividad.setPlazas(plazas);
                            // Obtener la referencia al documento que queremos actualizar
                            DocumentReference actividadRef = mFirestore.collection("Actividades").document(actividad.getId());
                            // Actualizar los datos del documento en Firestore
                            actividadRef.update("plazas", plazas)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Actualización correcta

                                            // Si el campo "fav" no existe, crearlo y agregar el ID de la actividad
                                            Map<String, Boolean> favMap = new HashMap<>();
                                            favMap.put(actividadId, true);
                                            userRef.update("fav", favMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(InfoActividadActivity.this, "Favoritos actualizados", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(InfoActividadActivity.this, "Error al actualizar favoritos", Toast.LENGTH_SHORT).show();
                                                            // Si ocurre un error y no se actualiza Firestore, devolvemos el número original a su EditText.
                                                            int plazas2 = Integer.parseInt(plazas_textView.getText().toString().trim());
                                                            plazas2--;
                                                            plazas_textView.setText(String.valueOf(plazas2));
                                                            // Y devolvemos el número a la base de datos.
                                                            actividadRef.update("plazas", plazas2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                }
                                                            });
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error al actualizar
                                            Toast.makeText(InfoActividadActivity.this, "Error: No se ha podido modificar el número de plazas.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                        else
                        {
                            Toast.makeText(InfoActividadActivity.this, "No hay plazas disponibles", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(InfoActividadActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InfoActividadActivity.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * FIN FAVORITOS Y PLAZAS
     */



    /*
     * CARGAR DATOS
     */

    /**
     * CARGAR DATOS FAVORITOS
     *
     * Recoge el mapa fav y lo almacena en un nuevo mapa local. Luego recorre dicho mapa
     * Define un icono de favoritos dependiendo de si encuentra o no la id de la actividad actual.
     *
     * @param userId
     * @param actividadId
     */
    private void cargarDatosFav(String userId, String actividadId)
    {
        DocumentReference userRef = mFirestore.collection("Users").document(userId);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> userData = documentSnapshot.getData();
                    if (userData != null && userData.containsKey("fav"))
                    {
                        Map<String, Object> favExistMap = (Map<String, Object>) userData.get("fav");
                        if (favExistMap.containsKey(actividadId))
                        {
                            fav_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_fill_white_50px, 0, 0, 0);
                        }
                        else
                        {
                            fav_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_white_50px, 0, 0, 0);
                        }
                    }
                    else
                    {
                        // Si el campo "fav" no existe, definimos el icono de la estrella vacía por defecto
                        fav_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_white_50px, 0, 0, 0);
                    }
                } else {
                    Toast.makeText(InfoActividadActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InfoActividadActivity.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Almacena los datos en los campos de activity_editar_actividad
     */
    private void cargarDatosActividad()
    {
        actividadRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    actividad = documentSnapshot.toObject(Actividades.class);
                    if (actividad != null)
                    {
                        nombre_textView.setText(actividad.getNombre());
                        zona_textView.setText(actividad.getZona());
                        plazas_textView.setText(String.valueOf(actividad.getPlazas()));
                        descripcion_textView.setText(actividad.getDescripcion());
                        mostrar_fecha_textView.setText(actividad.getFecha());
                    }
                } else {
                    Toast.makeText(InfoActividadActivity.this, "Actividad no encontrada", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InfoActividadActivity.this, "Error al cargar la actividad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * FIN CARGAR DATOS
     */
}