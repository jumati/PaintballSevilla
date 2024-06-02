package com.jm.paintballsevilla;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EditarActividad extends AppCompatActivity {

    private EditText nombre_editText, zona_editText, plazas_editText, descripcion_editText;
    private TextView hora_textView, fecha_textView;
    private Button hora_button, fecha_button, editar_button, volver_button, eliminar_button, fav_button;
    private Actividades actividad;
    private Intent intent;
    private FirebaseFirestore mFirestore;
    private DocumentReference actividadRef;
    private String userId, actividadIdString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_actividad);

        // Instancia Firestore
        mFirestore = FirebaseFirestore.getInstance();
        // Obtener id del usuario autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(this, "No se ha encontrado un usuario autenticado", Toast.LENGTH_SHORT).show();
            finish(); // Cerrar actividad si no hay usuario autenticado
            return;
        }

        nombre_editText = findViewById(R.id.editar_actividad_nombre);
        zona_editText = findViewById(R.id.editar_actividad_zona);
        plazas_editText = findViewById(R.id.editar_actividad_plazas);
        descripcion_editText = findViewById(R.id.editar_actividad_descripcion);
        hora_textView = findViewById(R.id.editar_actividad_hora_mostrar);
        fecha_textView = findViewById(R.id.editar_actividad_fecha_mostrar);
        hora_button = findViewById(R.id.editar_actividad_hora);
        fecha_button = findViewById(R.id.editar_actividad_fecha);
        editar_button = findViewById(R.id.editar_actividad_editar_button);
        volver_button = findViewById(R.id.editar_actividad_volver_button);
        eliminar_button = findViewById(R.id.editar_actividad_eliminar_actividad_button);
        fav_button = findViewById(R.id.editar_actividad_fav_button);


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
        actividadIdString = String.valueOf(actividad.getId());
        // Llama a la función que define el icono de fav
        cargarDatosFav(userId, actividad.getId());




        // Llamadas a funciones
        editarActividad();
        abrirCalendario();
        borrar();
        volver();

        // Pongo el onClick aquí porque me da pereza ponerlo fuera
        fav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarFavoritos(userId, actividadIdString);
            }
        });
    }

    /*
     * EDITAR ACTIVIDAD
     */

    private void editarActividad()
    {
        editar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se comprueba si las cadenas de texto tienen las medidas indicadas en la validación.
                if(!isEmptyValidation()) {
                    if (sizeValidation() && plazasValidation()) {
                        actividad = intent.getParcelableExtra("actividad");
                        String nombre = nombre_editText.getText().toString().trim();
                        String zona = zona_editText.getText().toString().trim();
                        int plazas = Integer.parseInt(plazas_editText.getText().toString().trim());
                        // String descripcion = descripcion_editText.getText().toString().trim();
                        String fecha = fecha_textView.getText().toString().trim();
                        String hora = hora_textView.getText().toString().trim();
                        String fecha_completa = fecha + " - " + hora;
                        String descripcion;
                        // Si no hay descripción, se pasará el campo vacío
                        if(descripcion_editText.getText().toString().trim().isEmpty())
                        {
                            // Limpiamos cualquier residuo que pueda producirse
                            descripcion = "";
                        }
                        else
                        {
                            descripcion = descripcion_editText.getText().toString().trim();
                        }

                        // Voy a utilizar el intent que ya tengo creado
                        // y voy a actualizar los datos con dicho intent
                        // Así también me ahorro tener que comparar la id y eso
                        actividad.setNombre(nombre);
                        actividad.setZona(zona);
                        actividad.setPlazas(plazas);
                        actividad.setDescripcion(descripcion);
                        actividad.setFecha(fecha_completa);

                        // Obtener la referencia al documento que queremos actualizar
                        actividadRef = mFirestore.collection("Actividades").document(actividad.getId());

                        // Actualizar los datos del documento en Firestore
                        actividadRef.update("nombre", nombre,
                                        "zona", zona,
                                        "plazas", plazas,
                                        "fecha", fecha_completa,
                                        "descripcion", descripcion)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Actualización correcta
                                        Toast.makeText(EditarActividad.this,
                                                "La actividad se ha editado con éxito.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error al actualizar
                                        Toast.makeText(EditarActividad.this,
                                                "Ha ocurrido un error y no se ha modificado la actividad.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            }
        });
    }

    /*
     * FIN EDITAR USUARIO
     */



    /*
     * CALENDARIO
     * FECHA Y HORA
     */

    /**
     * Separar Fecha y Hora
     *
     * Separa la cadena con .split() introduciendo el punto de separacion " - "
     * Luego almacena cada parte en un array.
     * Por último, los añade a sus textViews correspondientes
     */
    private void separarFechaHora()
    {
        // No es necesario el .toString().trim() pero tampoco molesta.
        String fechaHora = actividad.getFecha().toString().trim();
        String[] partes = fechaHora.split(" - ");
        if (partes.length == 2)
        {
            // Fecha
            fecha_textView.setText(partes[0].trim());
            // Hora
            hora_textView.setText(partes[1].trim());
        }
        else
        {
            // En caso de que el formato no sea el esperado, se pueden manejar errores aquí
            fecha_textView.setText("");
            hora_textView.setText("");
        }
    }

    /**
     * Calendario. Fecha y Hora
     *
     * Abre un calendario en el que podremos elegir la fecha y la hora según el botón que pulsemos.
     */
    private void abrirCalendario()
    {
        fecha_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                month++;
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditarActividad.this,
                        R.style.fecha,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                fecha_textView.setText("");
                                month++;
                                String fecha = dayOfMonth + "/" + month + "/" + year;
                                fecha_textView.setText(fecha);
                            }
                        }, year, month, day);
                // Color de fondo
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
                datePickerDialog.show();
            }
        });

        hora_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EditarActividad.this,
                        R.style.hora,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hora_textView.setText("");
                                String hora = hourOfDay + ":" + minute;
                                hora_textView.setText(hora);
                            }
                        }, 0, 0, true);
                // Color de fondo
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
                timePickerDialog.show();
            }
        });
    }

    /*
     * FIN CALENDARIO
     * FECHA Y HORA
     */



    /*
     * BORRAR
     */

    /**
     * Abre un cuadro de dialogo que el usuario con privilegios deberá aceptar antes de borrar la actividad.
     */
    private void borrar()
    {
        eliminar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Actividades actividad = intent.getParcelableExtra("actividad");

                AlertDialog.Builder builder = new AlertDialog.Builder(EditarActividad.this);
                builder.setMessage("¿Estás seguro de que quieres borrar esta actividad?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Confirma el borrado de la actividad
                                borrarActividad(actividad);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Cancela el borrado de la actividad
                                dialog.dismiss();
                            }
                        });
                // Muestra el diálogo de confirmación
                builder.create().show();
            }
        });
    }

    /**
     * Borra la actividad.
     *
     * @param actividad
     */
    private void borrarActividad(Actividades actividad)
    {
        String idActividad = actividad.getId();

        // Referencia al documento de la actividad en Firestore
        DocumentReference docRef = mFirestore.collection("Actividades").document(idActividad);

        // Borra la actividad de Firestore
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La actividad fue borrada con éxito
                        Toast.makeText(EditarActividad.this, "Actividad borrada exitosamente", Toast.LENGTH_SHORT).show();
                        // Volver a la pantalla MasterActivity
                        startActivity(new Intent(EditarActividad.this, MasterActivity.class));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Maneja el error si la actividad no puede ser borrada
                        Toast.makeText(EditarActividad.this, "Error al borrar la actividad", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*
     * FIN BORRAR
     */



    /*
     * FAVORITOS Y PLAZAS
     */

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
     * Después se almacena en la base de datos y se actualiza el EditText.
     *
     * Este método es muy complejo y cualquier cambio puede hacer que deje de funcionar.
     * NO TOCAR!
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
                        int plazas = Integer.parseInt(plazas_editText.getText().toString().trim());
                        boolean isRemovingFavorite = updatedFavMap.containsKey(actividadId);

                        if (isRemovingFavorite)
                        {
                            updatedFavMap.remove(actividadId);
                            plazas++;
                            plazas_editText.setText(String.valueOf(plazas));
                            fav_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_white_50px, 0, 0, 0);
                        }
                        else
                        {
                            if (plazas > 0)
                            {
                                updatedFavMap.put(actividadId, true);
                                plazas--;
                                plazas_editText.setText(String.valueOf(plazas));
                                fav_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_fill_white_50px, 0, 0, 0);
                            }
                            else
                            {
                                Toast.makeText(EditarActividad.this, "No hay plazas disponibles", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        actividad.setPlazas(plazas);
                        // Obtener la referencia al documento que queremos actualizar
                        DocumentReference actividadRef = mFirestore.collection("Actividades").document(actividad.getId());

                        // Actualiza los datos del documento en Firestore
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
                                                        Toast.makeText(EditarActividad.this, "Favoritos actualizados", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(EditarActividad.this, "Error al actualizar favoritos", Toast.LENGTH_SHORT).show();
                                                        // Si ocurre un error y no se actualiza Firestore, devolvemos el número original a su EditText.
                                                        int plazas2 = Integer.parseInt(plazas_editText.getText().toString().trim());
                                                        // Si se elimina se resta y si no pues no
                                                        plazas_editText.setText(String.valueOf(plazas2 + (isRemovingFavorite ? -1 : 1)));
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
                                        Toast.makeText(EditarActividad.this, "Error: No se ha podido modificar el número de plazas.", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    else
                    {
                        // Si el campo "fav" no existe, crearlo y agregar el ID de la actividad
                        actividad = intent.getParcelableExtra("actividad");
                        int plazas = Integer.parseInt(plazas_editText.getText().toString().trim());
                        if (plazas > 0) {
                            plazas++;
                            actividad.setPlazas(plazas);
                            // Obtenemos la referencia al documento que queremos actualizar
                            DocumentReference actividadRef = mFirestore.collection("Actividades").document(actividad.getId());
                            // Actualiza los datos del documento en Firestore
                            actividadRef.update("plazas", plazas)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Actualización correcta
                                            // Si el campo "fav" no existe, se crea y agregar el ID de la actividad
                                            Map<String, Boolean> favMap = new HashMap<>();
                                            favMap.put(actividadId, true);
                                            userRef.update("fav", favMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(EditarActividad.this, "Favoritos actualizados", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(EditarActividad.this, "Error al actualizar favoritos", Toast.LENGTH_SHORT).show();
                                                            // Si ocurre un error y no se actualiza Firestore, devolvemos el número original a su EditText.
                                                            int plazas2 = Integer.parseInt(plazas_editText.getText().toString().trim());
                                                            plazas2--;
                                                            plazas_editText.setText(String.valueOf(plazas2));
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
                                            Toast.makeText(EditarActividad.this, "Error: No se ha podido modificar el número de plazas.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                        else
                        {
                            Toast.makeText(EditarActividad.this, "No hay plazas disponibles", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(EditarActividad.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarActividad.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
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
                        if (favExistMap.containsKey(actividadIdString))
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
                    Toast.makeText(EditarActividad.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarActividad.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
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
                        nombre_editText.setText(actividad.getNombre());
                        zona_editText.setText(actividad.getZona());
                        plazas_editText.setText(String.valueOf(actividad.getPlazas()));
                        descripcion_editText.setText(actividad.getDescripcion());
                        separarFechaHora();
                    }
                } else {
                    Toast.makeText(EditarActividad.this, "Actividad no encontrada", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarActividad.this, "Error al cargar la actividad", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*
     * CARGAR DATOS
     */



    private void volver()
    {
        volver_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Volvemos a cargar el Activity para que se recarguen los datos limpiamente
                startActivity(new Intent(EditarActividad.this, MasterActivity.class));
            }
        });

    }



    /*
     * VALIDACIONES
     */

    /**
     * Validación campos vacíos.
     *
     * Comprueba que todos los campos de esta Activity estén rellenos.
     * En caso de haber uno o más campos vacíos, mostrará un mensaje.
     */
    private boolean isEmptyValidation() {
        boolean empty = false;
        if (nombre_editText.getText().toString().trim().isEmpty()) {
            empty = true;
            // Señalamos el error con un mensaje y marcando el campo en rojo
            nombre_editText.setError("Introduce el nombre de la actividad.");
        }

        if (zona_editText.getText().toString().trim().isEmpty()) {
            empty = true;
            // Señalamos el error con un mensaje y marcando el campo en rojo
            zona_editText.setError("Introduce la dirección donde se va a realizar la actividad.");
        }

        if (plazas_editText.getText().toString().trim().isEmpty()) {
            empty = true;
            // Señalamos el error con un mensaje y marcando el campo en rojo
            plazas_editText.setError("Introduce el número de plazas.");
        }

        if (fecha_textView.getText().toString().trim().isEmpty()
                || fecha_textView.getText().toString().trim().equals("Fecha")
                || hora_textView.getText().toString().trim().isEmpty()
                || hora_textView.getText().toString().trim().equals("Hora")) {
            empty = true;
            // Señalamos el error con un mensaje
            // Este error lo notificamos por Toast porque el TextView no tiene
            // la opción de marcar un errror con el .setError() como hace el EditText
            Toast.makeText(EditarActividad.this,
                    "Debes introducir la hora y la fecha.",
                    Toast.LENGTH_LONG).show();
        }

        // El campo descripción es opcional por lo que no se realizará ninguna comprobación.

        return empty;
    }



    /**
     * Valida la longitud de las cadenas de los campos nombre y zona.
     *
     * @return
     */
    private boolean sizeValidation()
    {
        boolean validated = true;

        // Validación para nombre
        if(nombre_editText.getText().toString().trim().length() > 30
                || nombre_editText.getText().toString().trim().length() < 2)
        {
            validated = false;

            // Si es incorrecto se muestra un mensaje
            // Señalamos el error con un mensaje y marcando el campo en rojo
            nombre_editText.setError("El nombre debe contener entre 2 y 30 caracteres.");
            // Se limpia el campo
            limpiarCampos(nombre_editText);
        }

        // Validación para la ubicación
        if(zona_editText.getText().toString().trim().length() > 45
                || zona_editText.getText().toString().trim().length() < 4)
        {
            validated = false;

            // Si es incorrecto se muestra un mensaje
            // Señalamos el error con un mensaje y marcando el campo en rojo
            zona_editText.setError("El nombre debe contener entre 4 y 45 caracteres.");
            // Se limpia el campo
            limpiarCampos(zona_editText);
        }

        // Validación para la descripción
        if(descripcion_editText.getText().toString().trim().length() > 100)
        {
            validated = false;

            // Si es incorrecto se muestra un mensaje
            // Señalamos el error con un mensaje y marcando el campo en rojo
            descripcion_editText.setError("El nombre debe contener menos de 100 caracteres.");
            // No se limpia el campo ya que se trata de una descripción larga
        }
        return validated;
    }

    /**
     * Valida la longitud y el formato de las plazas.
     *
     * @return
     */
    private boolean plazasValidation() {
        boolean validated = true;

        // Este Regex permite digitos del 0 al 9 una o dos veces.
        // Ej: 1, 4, 8, 10, 50, etc
        Pattern pattern = Pattern.compile("^(\\d{1}|\\d{2})$");
        // Lo del regex es muy rebuscado pero hoy se me ha antojado hacerlo así y funciona bien.
        String plazas_string = plazas_editText.getText().toString().trim();
        if (!pattern.matcher(plazas_string).matches())
        {
            // Señalamos el error con un mensaje y marcando el campo en rojo
            plazas_editText.setError("Introduce números de 1 a 2 dígitos.");
            validated = false;
        }
        else
        {
            int plazas = Integer.parseInt(plazas_editText.getText().toString().trim());
            if (plazas > 99)
            {
                // Señalamos el error con un mensaje y marcando el campo en rojo
                plazas_editText.setError("El número máximo de plazas es 99.");
                validated = false;
            } else if (plazas < 0)
            {
                // Esta comprobación es redundante al estar el Regex pero por si acaso.
                // Señalamos el error con un mensaje y marcando el campo en rojo
                plazas_editText.setError("El número de plazas introducido es incorrecto.");
                validated = false;
            }
        }
        return validated;
    }


    /*
     * Fin VALIDACIONES
     */




    /**
     * Limpiar un campo
     *
     * Limpia un campo EditText del fragment y lo deja vacío
     */
    private void limpiarCampos(EditText editText)
    {
        editText.setText("");
    }

}