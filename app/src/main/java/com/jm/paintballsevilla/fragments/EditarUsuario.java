package com.jm.paintballsevilla.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jm.paintballsevilla.LoginActivity;
import com.jm.paintballsevilla.R;
import com.jm.paintballsevilla.model.Users;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EditarUsuario extends Fragment {

    private TextView email_textView;
    private EditText name_editText, last_name_editText, phone_editText;
    private Button guardar_button, eliminar_usuario_button, limpiar_campos_button;
    private EditText old_password_editText, new_password_editText, confirm_new_password_editText;
    private Button cambiar_password_button;
    private FirebaseFirestore mfirestore;
    private FirebaseAuth auth;
    private Users user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Instancia Firestore
        mfirestore = FirebaseFirestore.getInstance();

        Bundle bundle = getArguments();
        user = (Users) bundle.getParcelable("usuario");

        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_editar_usuario, container, false);

        // Datos del usuario
        email_textView = view.findViewById(R.id.editar_usuario_email);
        name_editText = view.findViewById(R.id.editar_usuario_name);
        last_name_editText = view.findViewById(R.id.editar_usuario_last_name);
        phone_editText = view.findViewById(R.id.editar_usuario_phone);
        guardar_button = view.findViewById(R.id.editar_usuario_guardar_cambios_button);
        limpiar_campos_button = view.findViewById(R.id.editar_usuario_limpiar);
        eliminar_usuario_button = view.findViewById(R.id.editar_usuario_eliminar_usuario);

        // Contraseña del usuario
        old_password_editText = view.findViewById(R.id.editar_usuario_old_password);
        new_password_editText = view.findViewById(R.id.editar_usuario_new_password);
        confirm_new_password_editText = view.findViewById(R.id.editar_usuario_confirm_new_password);
        cambiar_password_button = view.findViewById(R.id.editar_usuario_cambiar_password_button);

        obtenerDatosUsuario(user.getEmail());


        // Botones
        editarUsuario();
        cambiarPassword();
        eliminarUsuario();
        limpiarCampos();

        // Inflate the layout for this fragment
        return view;
    }



    /**
     * Almacena los datos del intent en los campos
     */
    private void obtenerDatosUsuario(String email) {
        mfirestore.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            Users usuarioFirestore = documentSnapshot.toObject(Users.class);

                            // Actualizar los campos con los datos del usuario obtenidos de Firestore
                            user.setId(documentSnapshot.getId());
                            email_textView.setText(usuarioFirestore.getEmail());
                            user.setEmail(usuarioFirestore.getEmail());
                            name_editText.setText(usuarioFirestore.getName());
                            user.setName(usuarioFirestore.getName());
                            last_name_editText.setText(usuarioFirestore.getLast_name());
                            user.setLast_name(usuarioFirestore.getLast_name());
                            phone_editText.setText(String.valueOf(usuarioFirestore.getPhone()));
                            user.setPhone(usuarioFirestore.getPhone());
                        } else {
                            // El usuario no fue encontrado en Firestore
                            Toast.makeText(getContext(), "Usuario no encontrado en la base de datos.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al obtener los datos del usuario de Firestore
                        Toast.makeText(getContext(), "Error al obtener datos del usuario...", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * EDITAR USUARIO
     *
     * Update
     * Comprobamos las validaciones.
     * Modificamos los datos del usuario.
     *
     * No se modifica la contraseña. Para eso ya hay otros métodos.
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

                        // Instancias Firebase
                        // auth = FirebaseAuth.getInstance();

                        // Guardamos los datos
                        String name = name_editText.getText().toString().trim();
                        String last_name = last_name_editText.getText().toString().trim();
                        String email = email_textView.getText().toString().trim();
                        int phone = Integer.parseInt(phone_editText.getText().toString().trim());

                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("last_name", last_name);
                        map.put("phone", phone);

                        mfirestore.collection("Users").document(user.getId())
                                .update(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Éxito en la actualización
                                        Toast.makeText(getContext(), "Usuario actualizado correctamente.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error en la actualización
                                        Toast.makeText(getContext(), "Error al actualizar usuario!!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

            }
        });
    } // Fin editarUsuario()


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
                new AlertDialog.Builder(getContext())
                        .setTitle("Eliminar usuario")
                        .setMessage("¿Estás seguro de que quieres eliminar este usuario?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Eliminar usuario")
                                        .setMessage("Una vez se elimine el usuario no podrá ser recuperado.\n\n¿Estás seguro de que quieres eliminar este usuario?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                eliminarUsuarioFirestore(user.getId());
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
     * Elimina el usuario actual.
     * @param userId
     */
    private void eliminarUsuarioFirestore(String userId) {
        mfirestore.collection("Users")
                .document(userId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Eliminar usuario de Firebase Authentication
                        FirebaseAuth.getInstance().getCurrentUser().delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Se ha elmininado bien
                                        Toast.makeText(getContext(), "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getContext(), LoginActivity.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error al eliminar al usuario de Firebase Authentication
                                        Toast.makeText(getContext(), "Error al eliminar usuario de Firebase Authentication", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al eliminar al usuario
                        Toast.makeText(getContext(), "Error al eliminar usuario de Firebase Firestore", Toast.LENGTH_SHORT).show();
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



    /*
     * PASSWORD VALIDACIONES
     */

    private void cambiarPassword()
    {
        cambiar_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmptyPasswordValidation())
                {
                    if(passwordRegexValidation())
                    {
                        if(!passwordIguales())
                        {
                            auth = FirebaseAuth.getInstance();
                            // Obtener correo electrónico y contraseñas
                            String email = user.getEmail();
                            String oldPassword = old_password_editText.getText().toString().trim();
                            final String newPassword = new_password_editText.getText().toString().trim();
                            // Autenticar al usuario con su correo electrónico y contraseña actual
                            auth.signInWithEmailAndPassword(email, oldPassword)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            // Autenticación exitosa, proceder a cambiar la contraseña
                                            FirebaseUser user = auth.getCurrentUser();
                                            user.updatePassword(newPassword)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Contraseña cambiada
                                                            Toast.makeText(getContext(), "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Error al cambiar la contraseña
                                                            Toast.makeText(getContext(), "Error al cambiar contraseña: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error en la autenticación
                                            Toast.makeText(getContext(), "La contraseña actual no es correcta.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            // Limpiar campos de contraseña
                            old_password_editText.setText("");
                            new_password_editText.setText("");
                            confirm_new_password_editText.setText("");
                        }
                    }
                }
            }
        });
    }

    /**
     * Contraseñas iguales
     *
     * Comprueba que las contraseñas new_password y confirm_new_password sean idénticas
     */
    private boolean passwordIguales()
    {
        boolean validated = false;
        String pass = new_password_editText.getText().toString().trim();
        String comfirm_pass = confirm_new_password_editText.getText().toString().trim();

        if(!pass.equals(comfirm_pass))
        {
            validated = false;
            // Si los campos no son idénticos se muestra un mensaje
            // Señalamos el error con un mensaje y marcando los campos en rojo
            new_password_editText.setError("Las contraseñas no son idénticas.");
            confirm_new_password_editText.setError("Las contraseñas no son idénticas.");
            // Se limpian ambas contraseñas
            new_password_editText.setText("");
            confirm_new_password_editText.setText("");
        }

        return validated;
    }



    /**
     * Validar longitud password.
     *
     * La contraseña debe tener al entre 8 y 16 caracteres y, al menos,
     * un dígito, una minúscula y una mayúscula.
     * Solo se validará password. El campo confirm_password no es
     * necesario al tener que ser idéntico que password.
     */
    private boolean passwordRegexValidation()
    {
        boolean validated = true;

        Pattern pattern = Pattern.compile("^(?=.*[0-9])" // Al menos un dígito
                + "(?=.*[a-z])" // Al menos una letra minúscula
                + "(?=.*[A-Z])" // Al menos una letra mayúscula
                + "(?=\\S+$)" // No puede haber espacios en blanco
                + ".{8,20}$"); // Entre 8 y 20 caracteres
        if(!pattern.matcher(new_password_editText.getText().toString().trim()).matches())
        {
            validated = false;
            // Señalamos el error con un mensaje y marcando el campo en rojo
            new_password_editText.setError("La contraseña debe tener al entre 8 y 20 caracteres y, al menos, " +
                    "un dígito, una minúscula y una mayúscula.");
            // Se limpian ambos campos: password y confirm_password.
            new_password_editText.setText("");
            confirm_new_password_editText.setText("");
        }

        return validated;
    }

    /**
     * Validación campos de contraseña vacíos.
     *
     * Comprueba que todos los campos de la contraseña estén rellenos.
     * En caso de haber uno o más campos vacíos, mostrará un mensaje.
     */
    private boolean isEmptyPasswordValidation() {
        boolean empty = false;
        if (old_password_editText.getText().toString().trim().isEmpty())
        {
            empty = true;
            // Señalamos el error con un mensaje y marcando el campo en rojo
            old_password_editText.setError("Introduce tu contraseña actual.");
        }
        else
        {
            if(new_password_editText.getText().toString().trim().isEmpty())
            {
                empty = true;
                // Señalamos el error con un mensaje y marcando el campo en rojo
                new_password_editText.setError("Introduce tus apellidos.");
            }

            if(confirm_new_password_editText.getText().toString().trim().isEmpty())
            {
                empty = true;
                // Señalamos el error con un mensaje y marcando el campo en rojo
                confirm_new_password_editText.setError("Introduce tu teléfono.");
            }
        }


        return empty;
    }

    /*
     * FIN PASSWORD VALIDACIONES
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
                old_password_editText.setText("");
                new_password_editText.setText("");
                confirm_new_password_editText.setText("");
            }
        });
    }
}