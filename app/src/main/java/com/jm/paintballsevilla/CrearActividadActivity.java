package com.jm.paintballsevilla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jm.paintballsevilla.model.Users;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CrearActividadActivity extends AppCompatActivity {

    // Variables
    private EditText nombre_editText, zona_editText, plazas_editText, descripcion_editText;
    private TextView hora_textView, fecha_textView;
    private Button hora_button, fecha_button, crear_paintball_button, volver_button;
    private FirebaseFirestore mfirestore;
    private Users usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_actividad);

        // Firestore instance
        mfirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        usuario = intent.getParcelableExtra("usuario");

        nombre_editText = (EditText) findViewById(R.id.crear_paintball_nombre);
        zona_editText = (EditText) findViewById(R.id.crear_paintball_zona);
        plazas_editText = (EditText) findViewById(R.id.crear_paintball_plazas);
        descripcion_editText = (EditText) findViewById(R.id.crear_paintball_descripcion);
        hora_button = (Button) findViewById(R.id.crear_paintball_hora);
        hora_textView = (TextView) findViewById(R.id.crear_paintball_hora_mostrar);
        fecha_button = (Button) findViewById(R.id.crear_paintball_fecha);
        fecha_textView = (TextView) findViewById(R.id.crear_paintball_fecha_mostrar);
        crear_paintball_button = (Button) findViewById(R.id.crear_paintball_crear_button);
        volver_button = (Button) findViewById(R.id.crear_paintball_volver_button);

        abrirCalendario();
        crearPaintball();
        volver();

    }

    /**
     * Registra un paintball
     */
    private void crearPaintball()
    {
        crear_paintball_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se comprueba si las cadenas de texto tienen las medidas indicadas en la validación.
                if(!isEmptyValidation())
                {
                    if(sizeValidation() && plazasValidation())
                    {
                        // Guardamos los datos
                        String nombre = nombre_editText.getText().toString().trim();
                        String zona = zona_editText.getText().toString().trim();
                        int plazas = Integer.parseInt(plazas_editText.getText().toString().trim());
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

                        // Creamos el mapa
                        Map<String, Object> map = new HashMap<>();
                        map.put("nombre", nombre);
                        map.put("zona", zona);
                        map.put("plazas", plazas);
                        map.put("descripcion", descripcion);
                        map.put("fecha", fecha_completa);

                        // Almacenamos el mapa en "Actividades"
                        mfirestore.collection("Actividades").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // Usuario creado. Muestra un mensaje
                                Toast.makeText(CrearActividadActivity.this,
                                        "La actividad se ha creado con éxito.",
                                        Toast.LENGTH_LONG).show();
                                // Volver a la actividad principal
                                Intent mainIntent = new Intent(CrearActividadActivity.this, MasterActivity.class);
                                mainIntent.putExtra("usuario", usuario);
                                startActivity(mainIntent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Si ocurre algún error se muestra un mensaje
                                Toast.makeText(CrearActividadActivity.this,
                                        "Ha ocurrido un error y no se ha registrado la actividad.",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
    }


    /*
     * FECHA Y HORA
     */

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(CrearActividadActivity.this,
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(CrearActividadActivity.this,
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
     * FIN FECHA Y HORA
     */


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
            Toast.makeText(CrearActividadActivity.this,
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

        String plazas_string = plazas_editText.getText().toString().trim();

        if (!pattern.matcher(plazas_string).matches())
        {
            // Señalamos el error con un mensaje y marcando el campo en rojo
            plazas_editText.setError("Introduce números de 1 o más dígitos.");
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
     * FIN VALIDACIONES
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






    /**
     * Vuelve a MasterActivity
     */
    private void volver()
    {
        volver_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Volver a la actividad principal
                Intent mainIntent = new Intent(CrearActividadActivity.this, MasterActivity.class);
                mainIntent.putExtra("usuario", usuario);
                startActivity(mainIntent);
            }
        });
    }

}