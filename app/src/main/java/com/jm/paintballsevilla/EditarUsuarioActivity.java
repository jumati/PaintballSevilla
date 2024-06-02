package com.jm.paintballsevilla;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jm.paintballsevilla.model.Users;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EditarUsuarioActivity extends AppCompatActivity {

    // Variables
    private TextView email_textView;
    private EditText name_editText, last_name_editText, phone_editText;
    private CheckBox master_checkBox;
    private Button guardar_button, volver_button, eliminar_usuario_button, limpiar_campos_button, recuperar_contraseña_button;
    private String email;
    private FirebaseFirestore mfirestore;
    private DocumentReference userRef;
    private Intent intent;
    private Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        // Instancia Firestore
        mfirestore = FirebaseFirestore.getInstance();

        // Se recogen los campos
        name_editText = (EditText) findViewById(R.id.editar_usuario_name_activity);
        last_name_editText = (EditText) findViewById(R.id.editar_usuario_last_name_activity);
        email_textView = (TextView) findViewById(R.id.editar_usuario_email_activity);
        email = email_textView.getText().toString().trim();
        phone_editText = (EditText) findViewById(R.id.editar_usuario_phone_activity);
        master_checkBox = (CheckBox) findViewById(R.id.editar_usuario_master_activity);
        guardar_button = (Button) findViewById(R.id.editar_usuario_guardar_cambios_button_activity);
        volver_button = (Button) findViewById(R.id.editar_usuario_volver_activity);
        eliminar_usuario_button = (Button) findViewById(R.id.editar_usuario_eliminar_usuario_activity);
        limpiar_campos_button =(Button) findViewById(R.id.editar_usuario_limpiar_activity);


        intent = getIntent();
        if (intent != null && intent.hasExtra("user")) {
            user = intent.getParcelableExtra("user");
            if (user != null) {
                String userId = user.getId();
                userRef = mfirestore.collection("Users").document(userId);
                cargarDatosUsuario();
            }
        }

        // Llamadas a métodos
        editarUsuario();
        eliminarUsuario();
        limpiarCampos();

        // Volver a MasterActivity
        volver();
    }


    /**
     * EDITAR USUARIO
     *
     * Edita los usuarios con los datos introducidos en los EditTexts una vez han pasado las validaciones.
     */
    private void editarUsuario()
    {
        guardar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Comprobar validaciones
                // Primero comprueba que no haya campos vacíos
                if(!isEmptyValidation())
                {
                    // Luego te dice las validaciones en el resto de campos
                    if (sizeNameValidation() && phoneValidation())
                    {
                        // Guardamos los datos
                        String name = name_editText.getText().toString().trim();
                        String last_name = last_name_editText.getText().toString().trim();
                        int phone = Integer.parseInt(phone_editText.getText().toString().trim());
                        boolean master = master_checkBox.isChecked();

                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("last_name", last_name);
                        map.put("phone", phone);
                        map.put("master", master);

                        String userEmail = user.getEmail();

                        mfirestore.collection("Users")
                                .whereEqualTo("email", userEmail)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            // Obtiene el ID del primer documento encontrado
                                            // se entiende que hay solo uno con ese correo
                                            String userId = queryDocumentSnapshots.getDocuments().get(0).getId();

                                            // Actualizar los datos del usuario
                                            mfirestore.collection("Users").document(userId)
                                                    .update(map)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Usuario editado correctamente
                                                            Toast.makeText(EditarUsuarioActivity.this,
                                                                    "Usuario editado correctamente.",
                                                                    Toast.LENGTH_LONG).show();
                                                            // Puedes realizar alguna acción adicional aquí si es necesario
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Error al editar usuario
                                                            Toast.makeText(EditarUsuarioActivity.this,
                                                                    "Se ha producido un error al editar el usuario.",
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        } else {
                                            // No se encontró ningún usuario con el correo electrónico proporcionado
                                            Toast.makeText(EditarUsuarioActivity.this,
                                                    "Usuario no encontrado en Firebase Firestore",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error al buscar el usuario en Firestore
                                        Toast.makeText(EditarUsuarioActivity.this,
                                                "Error al buscar usuario en Firebase Firestore",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

            }
        });
    } // Fin editarUsuario()


    /**
     * Almacena los datos del intent en los campos de activity_editar_actividad
     */
    private void cargarDatosUsuario()
    {
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    user = documentSnapshot.toObject(Users.class);
                    if (user != null)
                    {
                        email_textView.setText(user.getEmail());
                        name_editText.setText(user.getName());
                        last_name_editText.setText(user.getLast_name());
                        phone_editText.setText(String.valueOf(user.getPhone()));
                        master_checkBox.setChecked(user.isMaster());
                    }
                } else {
                    Toast.makeText(EditarUsuarioActivity.this, "Usuario no encontrada", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@org.checkerframework.checker.nullness.qual.NonNull Exception e) {
                Toast.makeText(EditarUsuarioActivity.this, "Error al cargar el usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*
     * ELIMINAR
     */

    /**
     * Llama al método que elimina al usuario.
     *
     * Muestra un par de mensajes de confirmación para eliminar el usuario.
     */
    private void eliminarUsuario() {
        eliminar_usuario_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar un diálogo de confirmación antes de eliminar al usuario
                new AlertDialog.Builder(EditarUsuarioActivity.this)
                        .setTitle("Eliminar usuario")
                        .setMessage("¿Estás seguro de que quieres eliminar este usuario?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(EditarUsuarioActivity.this)
                                        .setTitle("Eliminar usuario")
                                        .setMessage("Una vez se elimine el usuario no podrá ser recuperado.\n\n¿Estás seguro de que quieres eliminar este usuario?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                eliminarUsuarioFirestore();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
    }

    /**
     * Eliminar Usuario de Firestore
     */
    private void eliminarUsuarioFirestore()
    {
        String email = user.getEmail();
        // Buscar al usuario por su correo electrónico en Firestore
        mfirestore.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Obtiene el ID del primer documento encontrado
                            // se entiende que hay solo uno con ese correo
                            String userId = queryDocumentSnapshots.getDocuments().get(0).getId();

                            // Eliminar usuario de Firestore
                            mfirestore.collection("Users").document(userId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Eliminar usuario de Auth
                                            // eliminarUsuarioAuth();

                                            // No se encontró ningún usuario con el correo electrónico proporcionado
                                            Toast.makeText(EditarUsuarioActivity.this,
                                                    "Usuario eliminado correctamente.",
                                                    Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error al eliminar usuario de Firestore
                                            Toast.makeText(EditarUsuarioActivity.this, "Error al eliminar usuario de Firebase Firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // No se encontró ningún usuario con el correo electrónico proporcionado
                            Toast.makeText(EditarUsuarioActivity.this,
                                    "Usuario no encontrado en Firebase Firestore",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al buscar el usuario en Firestore
                        Toast.makeText(EditarUsuarioActivity.this, "Error al buscar usuario en Firebase Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*
     * FIN ELIMINAR
     */



    /**
     * Vuelve a LoginActivity
     */
    private void volver()
    {
        volver_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se utiliza el finish para regresar a la misma pantalla que se tenía antes
                // Hay que cambiar de fragment o usar el refresh para que se actualicen los datos
                finish();
            }
        });
    }



    /*
     * VALIDACIONES
     */

    /**
     * Limpiar campos
     *
     * Limpia los campos EditText y los deja vacíos
     */
    private void limpiarCampos()
    {
        limpiar_campos_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name_editText.setText("");
                last_name_editText.setText("");
                phone_editText.setText("");
            }
        });
    }




    /*
     * VALIDACIONES
     */


    /**
     * Validación campos vacíos.
     *
     * Comprueba que todos los campos estén rellenos.
     * En caso de haber uno o más campos vacíos, mostrará un mensaje.
     */
    private boolean isEmptyValidation() {
        boolean empty = false;
        if (name_editText.getText().toString().trim().isEmpty())
        {
            empty = true;
            // Señalamos el error con un mensaje y marcando el campo en rojo
            name_editText.setError("Introduce tu nombre.");
        }

        if(last_name_editText.getText().toString().trim().isEmpty())
        {
            empty = true;
            // Señalamos el error con un mensaje y marcando el campo en rojo
            last_name_editText.setError("Introduce tus apellidos.");
        }

        if(phone_editText.getText().toString().trim().isEmpty())
        {
            empty = true;
            // Señalamos el error con un mensaje y marcando el campo en rojo
            phone_editText.setError("Introduce tu teléfono.");
        }

        if(empty)
        {
            // Si hay campos vacíos se muestra un mensaje.
            Toast.makeText(EditarUsuarioActivity.this,
                    "No se han rellenado todos los campos.",
                    Toast.LENGTH_LONG).show();
        }
        return empty;
    }


    /**
     * Validar longitud name y last_name.
     *
     * Se pondrá una longitud mínima y máxima para cada campo.
     */
    private boolean sizeNameValidation()
    {
        boolean validated = true;

        // Validación para name
        if(name_editText.getText().toString().trim().length() > 12
                || name_editText.getText().toString().trim().length() < 2)
        {
            validated = false;
            // Si es incorrecto se muestra un mensaje
            // Señalamos el error con un mensaje y marcando el campo en rojo
            name_editText.setError("El nombre debe contener entre 2 y 12 caracteres.");
            // Se limpia el campo
            name_editText.setText("");
        }

        // Validación para last_name
        if(last_name_editText.getText().toString().trim().length() > 20
                || last_name_editText.getText().toString().trim().length() < 4)
        {
            validated = false;
            // Si es incorrecto se muestra un mensaje
            // Señalamos el error con un mensaje y marcando el campo en rojo
            last_name_editText.setError("El apellido debe contener entre 4 y 20 caracteres.");
            // Se limpia el campo
            last_name_editText.setText("");
        }

        return validated;
    }


    /**
     * Validación del teléfono.
     *
     * Comprueba que el teléfono esté introducido correctamente.
     * En caso de no pasar la validación, mostrará un mensaje.
     * (Solo tiene en cuenta móviles de España)
     */
    private boolean phoneValidation()
    {
        boolean validated = true;

        // Pattern que utiliza un Regex para comprobar que el teléfono sea un móvil o un fijo español:
        // Puede empezar por prefijo español (+34, 0034, o 34) o sin él,
        // seguido de un número de 9 dígitos que comience por 6, 7, 8 o 9.
        Pattern pattern = Pattern.compile("^(\\+34|0034|34)?[6789][0-9]{8}$");

        // Se valida si el parámetro introducido coincide con el Regex del pattern o no.
        if (!pattern.matcher(phone_editText.getText().toString().trim()).matches())
        {
            validated = false;
            // Si el teléfono es incorrecto se muestra un mensaje
            // Señalamos el error con un mensaje y marcando el campo en rojo
            phone_editText.setError("El teléfono introducido no es correcto.");
            // Se limpia el campo
            phone_editText.setText("");
        }
        return validated;
    }


    /*
     * FIN VALIDACIONES
     */

}