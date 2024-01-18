package com.example.reto;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

public class Login extends AppCompatActivity {

    private ViewFlipper vf;
    private ImageView perfilImageView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        vf = findViewById(R.id.viewFlipper);

        Button btnLogin  = findViewById(R.id.btnLogin);
        Button btnRegistro = findViewById(R.id.registro);
        perfilImageView = findViewById(R.id.perfilRegistro);

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vf.showNext();
            }
        });

        perfilImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                } else {
                    Intent foto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (foto.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(foto, REQUEST_IMAGE_CAPTURE);
                    }
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Crear un drawable redondeado
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
            roundedBitmapDrawable.setCircular(true);

            // Establecer la imagen en el ImageView
            perfilImageView.setImageDrawable(roundedBitmapDrawable);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de cámara concedido", Toast.LENGTH_LONG).show();
                Intent foto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (foto.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(foto, REQUEST_IMAGE_CAPTURE);
                }
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_LONG).show();
            }
        }
    }
}
