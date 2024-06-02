package com.jm.paintballsevilla;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jm.paintballsevilla.model.Users;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private EditText email_editText, password_editText;
    private Button login_button, registrar_button, reset_password_button, info_button;
    private FirebaseAuth auth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Declaración de variables
        email_editText = (EditText) findViewById(R.id.login_email);
        password_editText = (EditText) findViewById(R.id.login_password);
        login_button = (Button) findViewById(R.id.login_iniciar_sesion_button);
        registrar_button = (Button) findViewById(R.id.login_registrar_button);
        reset_password_button = (Button) findViewById(R.id.login_recuperar_password_button);
        info_button = (Button) findViewById(R.id.login_informacion_button);

        // Instancia de Auth
        auth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Logearse e ir a GuidesActivity
        goLogin();

        // Ir a RegistroActivity
        goRegistro();

        // Ir a InfoActivity
        goResetPassword();

        // Ir a InfoActivity
        goInfo();


    } // Fin onCreate()



    /*
     * BOTONES
     */

    /**
     * Go Login
     *
     * Realiza las validaciones y si las supera, realiza el login y accede a
     * la lista de las guias en GuidesActivity.
     */
    private void goLogin()
    {
        Context ctx = this;
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = email_editText.getText().toString().trim();
                String password = password_editText.getText().toString().trim();

                // Validaciones
                if(!isEmptyValidation() && emailValidation())
                {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        checkIsMaster(email);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Si el email o la contraseña son incorrectos se muestra un mensaje.
                                    Toast.makeText(LoginActivity.this,
                                            "email o contraseña incorrectos.",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }

    /**
     * Comprueba si el usuario es master o no.
     *
     * @param email
     */
    private void checkIsMaster(String email)
    {
        mFirestore.collection("Users").whereEqualTo("email", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            Users user = document.toObject(Users.class);
                            if (user != null)
                            {
                                Intent intent;
                                if (user.isMaster())
                                {
                                    intent = new Intent(LoginActivity.this, MasterActivity.class);
                                }
                                else
                                {
                                    intent = new Intent(LoginActivity.this, NoMasterActivity.class);
                                }
                                intent.putExtra("usuario", user);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                startActivity(new Intent(LoginActivity.this, NoMasterActivity.class));
                            }
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Error: Usuario no encontrado.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,
                                "Error: imposible obtener datos del usuario.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }




    /**
     * Ir a RegistroActivity
     */
    private void goRegistro()
    {
        registrar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
            }
        });
    }

    /**
     * Ir a ResetPasswordActivity
     */
    private void goResetPassword()
    {
        reset_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
    }

    /**
     * Ir a InfoActivity
     */
    private void goInfo()
    {
        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, InfoActivity.class));
            }
        });
    }

    /*
     * FIN BOTONES
     */


    /*
     * VALIDACIONES
     */

    /**
     * Valida si es un usuario master o no.
     *
     * Si es Master, se enviará a MasterActivity.
     * Si NO es Master, se enviará a NoMasterActivity.
     *
     * @return
     */
    private Boolean isMaster()
    {
        boolean master = false;

        // Comprobación temporal
        if(!master)
        {
            master = true;
        }
        else
        {
            master = false;
        }




        return master;
    }


    private boolean isEmptyValidation() {
        boolean empty = false;

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

        if(empty)
        {
            // Si hay campos vacíos se muestra un mensaje.
            Toast.makeText(LoginActivity.this,
                    "Introduce el email y la contraseña",
                    Toast.LENGTH_LONG).show();
        }
        return empty;
    }

    private boolean emailValidation()
    {
        boolean validated = true;

        // Regex de Google
        // ([a-z0-9]+(\.?[a-z0-9])*)+@(([a-z]+)\.([a-z]+))+

        // Comprueba que el correo sea el correcto con un Regex.
        Pattern pattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        if(!pattern.matcher(email_editText.getText().toString().trim()).matches())
        {
            validated = false;
            // Si el email es incorrecto se muestra un mensaje
            // Señalamos el error con un mensaje y marcando el campo en rojo
            email_editText.setError("El email introducido no es correcto.");
            // Se limpia el campo email.
            email_editText.setText("");
        }

        return validated;
    }






    /*
     * FIN VALIDACIONES
     */
}
