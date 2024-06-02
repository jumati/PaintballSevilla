package com.jm.paintballsevilla;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    private EditText name_editText, last_name_editText, email_editText,
            password_editText, confirm_password_editText, phone_editText;
    private Button registrar_button, volver_button, limpiar_campos_button;
    private FirebaseFirestore mfirestore;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Se recogen los campos
        name_editText = (EditText) findViewById(R.id.registro_name);
        last_name_editText = (EditText) findViewById(R.id.registro_last_name);
        email_editText = (EditText) findViewById(R.id.registro_email);
        password_editText = (EditText) findViewById(R.id.registro_password);
        confirm_password_editText = (EditText) findViewById(R.id.registro_confirm_password);
        phone_editText = (EditText) findViewById(R.id.registro_phone);
        registrar_button = (Button) findViewById(R.id.registro_registrar_button);
        limpiar_campos_button = (Button) findViewById(R.id.registro_limpiar);
        volver_button = (Button) findViewById(R.id.registro_volver_button);

        FirebaseApp.initializeApp(this);

        mfirestore = FirebaseFirestore.getInstance();

        // Botón Registrar Usuario
        registrar();
        // Limpiar Campos
        limpiarCampos();
        // Ir a LoginActivity
        volver();
    } // Fin onCreate

    /**
     * Registra un usuario
     */
    private void registrar()
    {
        registrar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Comprobar validaciones
                // Primero comprueba que no haya campos vacíos
                if(!isEmptyValidation()) {
                    // Luego te dice las validaciones en el resto de campos
                    if (sizeNameValidation() && emailValidation()
                            && passwordValidation() && phoneValidation()) {

                        // Instancias Firebase
                        auth = FirebaseAuth.getInstance();

                        // Guardamos los datos
                        String name = name_editText.getText().toString().trim();
                        String last_name = last_name_editText.getText().toString().trim();
                        String email = email_editText.getText().toString().trim();
                        String password = password_editText.getText().toString().trim();
                        int phone = Integer.parseInt(phone_editText.getText().toString().trim());

                        Map<String, Object> map = new HashMap<>();
                        map.put("email", email);
                        map.put("name", name);
                        map.put("last_name", last_name);
                        map.put("phone", phone);
                        map.put("master", false);
                        map.put("fav", new HashMap<String, Object>());

                        // Llamamos a la funcion para crear el usuario
                        createAuth(email, password, map);

                        // Se cierra sesión para evitar que se mantenga logeado el usuario
                        auth.signOut();

                        // Se limpian los campos una vez registrado el usuario
                        name_editText.setText("");
                        last_name_editText.setText("");
                        email_editText.setText("");
                        password_editText.setText("");
                        confirm_password_editText.setText("");
                        phone_editText.setText("");
                    }
                }

            }
        });
    } // Fin registrar()

    /**
     * Registrar Usuario y Contraseña
     */
    private void createAuth(final String email, final String password, final Map<String, Object> map)
    {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = auth.getCurrentUser().getUid();
                            mfirestore.collection("Users").document(userId).set(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegistroActivity.this,
                                                        "El usuario se ha registrado con éxito.",
                                                        Toast.LENGTH_LONG).show();
                                                limpiarCampos();
                                                auth.signOut();
                                                startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
                                            } else {
                                                Toast.makeText(RegistroActivity.this,
                                                        "Se ha producido un error al registrar el usuario.",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegistroActivity.this,
                                    "El usuario ya existe o se ha producido un error.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


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
                email_editText.setText("");
                password_editText.setText("");
                confirm_password_editText.setText("");
                phone_editText.setText("");
            }
        });
    }


    /**
     * Vuelve a LoginActivity
     */
    private void volver()
    {
        limpiarCampos();
        volver_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // LOGOUT
                auth.signOut();
                startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
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

        if(email_editText.getText().toString().trim().isEmpty())
        {
            empty = true;
            // Señalamos el error con un mensaje y marcando el campo en rojo
            email_editText.setError("Introduce un email.");
        }

        if(password_editText.getText().toString().trim().isEmpty())
        {
            empty = true;
            // Señalamos el error con un mensaje y marcando el campo en rojo
            password_editText.setError("Introduce una contraseña.");
        }

        if(confirm_password_editText.getText().toString().trim().isEmpty())
        {
            empty = true;
            // Señalamos el error con un mensaje y marcando el campo en rojo
            confirm_password_editText.setError("Confirma la contraseña.");
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
            Toast.makeText(RegistroActivity.this,
                    "No se han rellenado todos los campos",
                    Toast.LENGTH_LONG).show();
        }
        return empty;
    }


    /*
     * VALIDAR EMAIL
     */

    /**
     * Valida el email.
     *
     * Valida el email para que sea compatible con emails de Google.
     */
    private boolean emailValidation()
    {
        boolean validated = true;

        String email = email_editText.getText().toString().trim();

        // Regex de Google
        // ([a-z0-9]+(\.?[a-z0-9])*)+@(([a-z]+)\.([a-z]+))+

        // Comprueba que el correo sea el correcto con un Regex.
        Pattern pattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        if(!pattern.matcher(email).matches())
        {
            validated = false;
            // Si el email es incorrecto se muestra un mensaje
            // Señalamos el error con un mensaje y marcando el campo en rojo
            email_editText.setError("El email introducido no es correcto.");
            // Se limpia el campo email.
            email_editText.setText("");
        }
        else if (emailExist(email))
        {
            // Paramos la ejecución para que pueda consultar la busqueda.
            // Esto es porque el método usado para comprobar el email es asíncrono
            // Y esto soluciona ese pequeño problema con los diferentes procesos.
            try {
                // 1 segundos = 1000 milisegundos)
                // Se va a establecer a 1/4 de segundo
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            validated = false;
            email_editText.setError("El email introducido ya existe.");
        }
        return validated;
    }

    /**
     * Realiza una busqueda del email que se le introduzca en la tabla Users
     * de la base de datos de Firestore
     *
     * @param email
     * @return
     */
    private boolean emailExist(String email) {
        final boolean[] exist = {false};

        mfirestore.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                exist[0] = true;
                                // Introducimos un break porque ya hemos encontrado
                                // a un usuario con ese email.
                                break;
                            }
                        } else {
                            // Manejar el error de la consulta
                            Toast.makeText(RegistroActivity.this,
                                    "Error al consultar la base de datos.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

        return exist[0];
    }

    /*
     * FIN VALIDAR EMAIL
     */


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
     * Validación de password y confirm_password.
     *
     * Llama al método  que comprueba que la contraseña sea correcta.
     * Comprueba que los campos password y confirm_password sean identicos.
     */
    private boolean passwordValidation()
    {
        boolean validated = true;
        String pass = password_editText.getText().toString().trim();
        String comfirm_pass = confirm_password_editText.getText().toString().trim();

        // Comprobación de que los campos password y confirm_password sean idénticos.
        if(!passwordRegexValidation())
        {
            // El mensaje se muestra en passwordRegexValidation().
            validated = false;
        }
        else if(!pass.equals(comfirm_pass))
        {
            validated = false;
            // Si los campos no son idénticos se muestra un mensaje
            // Señalamos el error con un mensaje y marcando los campos en rojo
            password_editText.setError("Las contraseñas no son idénticas.");
            confirm_password_editText.setError("Las contraseñas no son idénticas.");
            // También lanzamos un mensaje global indicando que deben ser idénticas.
            Toast.makeText(RegistroActivity.this,
                    "Las contraseñas no son idénticas.",
                    Toast.LENGTH_LONG).show();
            // Se limpian ambas contraseñas
            password_editText.setText("");
            confirm_password_editText.setText("");
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
        if(!pattern.matcher(password_editText.getText().toString().trim()).matches())
        {
            validated = false;
            // Señalamos el error con un mensaje y marcando el campo en rojo
            password_editText.setError("La contraseña debe tener al entre 8 y 20 caracteres y, al menos, " +
                                        "un dígito, una minúscula y una mayúscula.");
            // Se limpian ambos campos: password y confirm_password.
            password_editText.setText("");
            confirm_password_editText.setText("");
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