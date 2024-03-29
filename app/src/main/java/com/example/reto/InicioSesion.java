package com.example.reto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class InicioSesion extends AppCompatActivity {

    private ViewFlipper vf;
    private ImageView perfilImageView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private LoginManager apiManager;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiManager = new LoginManager(InicioSesion.this);

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.login_horizontal);
            vf = findViewById(R.id.viewFlipper_horizontal);
        } else {
            setContentView(R.layout.login);
            vf = findViewById(R.id.viewFlipper);
        }

        Button btnLogin  = findViewById(R.id.btnLogin);
        Button btnRegistro = findViewById(R.id.btnRegistro);
        Button registroLogin = findViewById(R.id.registro);
        perfilImageView = findViewById(R.id.perfilRegistro);
        TextView inputUsuario = findViewById(R.id.inputUsuarioText);
        TextView inputContra = findViewById(R.id.inputContraText);
        TextView inputEmailRegistro = findViewById(R.id.inputMailRegistroTexto);
        TextView inputUsuarioRegistro = findViewById(R.id.inputUsuarioRegistroTexto);
        TextView inputContraRegistro = findViewById(R.id.inputContraRegistroTexto);
        registroLogin.setOnClickListener(new View.OnClickListener() {
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

        Button btnLoginRegistro = findViewById(R.id.loginRegistro);

        btnLoginRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vf.showNext();
            }
        });
        TextView invitado = findViewById(R.id.invitado);
        invitado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioSesion.this, MainActivity.class);
                guardarUsuarioyEmailEnSharedPreferences("invitado",null);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(inputUsuario.getText());
                String password = String.valueOf(inputContra.getText());
                Log.d("Username", "onClick: "+username);
                try {
                    apiManager.iniciarSesion(username, password, new LoginManager.ApiCallback() {
                        @Override
                        public void onSuccess(String response,String email) {
                            Log.d("Inicio de sesión", response);
                            Log.d("Inicio de sesión", "Email: " + email);
                            Toast.makeText(InicioSesion.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                            guardarUsuarioyEmailEnSharedPreferences(username,email);

                            Intent intent = new Intent(InicioSesion.this, MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(InicioSesion.this, "Error en inicio de sesión", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(inputUsuarioRegistro.getText());
                String email = String.valueOf(inputEmailRegistro.getText());
                String password = String.valueOf(inputContraRegistro.getText());

                try {
                    apiManager.registrarUsuario(username, email, password, new LoginManager.ApiCallback() {
                        @Override
                        public void onSuccess(String response, String email) {
                            Toast.makeText(InicioSesion.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            vf.showNext();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.d("ERRORREGISTRO", "onError: "+errorMessage);
                            if (errorMessage == null) {
                                Toast.makeText(InicioSesion.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                vf.showNext();
                            } else {
                                Toast.makeText(InicioSesion.this, "Error en registro", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
            roundedBitmapDrawable.setCircular(true);

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

    private void guardarUsuarioyEmailEnSharedPreferences(String username,String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("username", username);
        editor.putString("email",email);
        Log.d("USEREMAIL", "guardarUsuarioyEmailEnSharedPreferences: "+email);

        editor.apply();
    }
}
