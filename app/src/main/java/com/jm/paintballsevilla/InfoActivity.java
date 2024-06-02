package com.jm.paintballsevilla;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    private Button volver_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Declaración de variables
        volver_button = (Button) findViewById(R.id.info_volver_button);

        // Ir a LoginActivity
        volver();

    } // Fin onCreate()

    /**
     * Vuelve a LoginActivity
     */
    private void volver()
    {
        volver_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InfoActivity.this, LoginActivity.class));
            }
        });
    }
}