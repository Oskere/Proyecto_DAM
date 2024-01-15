package com.example.reto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private ViewFlipper vf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        vf = findViewById(R.id.viewFlipper);

        Button btnLogin  = findViewById(R.id.btnLogin);
        Button btnRegistro = findViewById(R.id.registro);

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vf.showNext();
            }
        });

        // Repite el proceso para los botones de la segunda vista si es necesario
        Button btnLoginRegistro = findViewById(R.id.loginRegistro);

        btnLoginRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vf.showNext();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
