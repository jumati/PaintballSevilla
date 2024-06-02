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
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity {

    private String email;
    private EditText reset_email;
    private Button enviar_button, volver_button;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Variables
        reset_email = (EditText) findViewById(R.id.reset_password_email);
        enviar_button = (Button) findViewById(R.id.reset_password_enviar_button);
        volver_button = (Button) findViewById(R.id.reset_password_volver_button);

        // Enviar email
        enviarEmail();

        // Ir a LoginActivity
        volver();
    }

    /**
     * Enviar correo
     *
     * Envía un correo para recuperar contraseña.
     */
    private void enviarEmail()
    {
        enviar_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Validaciones. Cada validación contiene el mensaje que se mostrará al usuario.
                if(!IsEmptyValidation() && emailValidation())
                {
                    email = reset_email.getText().toString().trim();

                    resetPassword(email);
                }
            }


        });
    }


    /**
     * Recuperar contraseña
     *
     *
     */
    private void resetPassword(String email)
    {
        // Se realiza la instancia
        auth = FirebaseAuth.getInstance();
        // Elegimos el idioma del email.
        auth.setLanguageCode("es");
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                // Validar si la tarea ha tenido éxito y se ha enviado correctamente el email al usuario.
                if(task.isSuccessful())
                {
                    Toast.makeText(ResetPasswordActivity.this,
                            "Correo enviado para restablecer la contraseña.",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                }
                else
                {
                    Toast.makeText(ResetPasswordActivity.this,
                            "Se ha producido un error al enviar el email.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * Vuelve a LoginActivity
     */
    private void volver()
    {
        volver_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
            }
        });
    }



    /*
     * VALIDACIONES
     */

    /**
     * Validación campo vacío.
     *
     * Comprueba que el email no esté vacío.
     * En caso de estarlo, mostrará un mensaje.
     */
    private boolean IsEmptyValidation() {
        boolean empty = false;
        if(reset_email.getText().toString().trim().isEmpty())
        {
            empty = true;
            // Señalamos el error con un mensaje y marcando el campo en rojo
            reset_email.setError("Introduce un email.");
        }
        return empty;
    }

    /**
     * Valida el correo.
     *
     * Valida el correo para que sea compatible con correos de Google.
     * Por seguridad, no se le notificará al usuario si el email existe o no.
     * En caso de que el email no exista, Firebase no enviará el correo.
     */
    private boolean emailValidation()
    {
        boolean validated = true;

        // Comprueba que el correo sea el correcto con un Regex.
        Pattern pattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        if(!pattern.matcher(reset_email.getText().toString().trim()).matches())
        {
            validated = false;
            // Si el email es incorrecto se muestra un mensaje
            // Señalamos el error con un mensaje y marcando el campo en rojo
            reset_email.setError("El email introducido no es correcto.");
            // Se limpia el campo email.
            reset_email.setText("");
        }

        return validated;
    }

    /*
     * FIN VALIDACIONES
     */
}
