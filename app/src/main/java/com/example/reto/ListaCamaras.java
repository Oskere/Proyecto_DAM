package com.example.reto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reto.Entidades.Camara;

import java.util.List;

public class ListaCamaras extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclercamaras);

        Intent intent = getIntent();

        TextView titulo = findViewById(R.id.tituloLista);
        titulo.setText(intent.getStringExtra("titulo"));

        // Obtén una referencia al SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);

        // Recupera el nombre de usuario
        String username = sharedPreferences.getString("username", "default_value");
        Log.d("Usuario", username);

        List<Camara> cameraList = (List<Camara>) intent.getSerializableExtra("cameraList");
        List<Camara> favList = (List<Camara>) intent.getSerializableExtra("favList"); // Obtén la lista de favoritos


        RecyclerView recyclerView = findViewById(R.id.recycler);
        CamaraAdapter adapter = new CamaraAdapter(this, cameraList, favList, username);

// Configurar el RecyclerView y el adaptador
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

// Agregar un observador para esperar a que el RecyclerView se dibuje completamente
        recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Remover el observador para evitar múltiples llamadas
                recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                // Iterar sobre la lista de cámaras para configurar la animación en función de si están en favoritos
                for (Camara camara : cameraList) {
                    CamaraAdapter.ViewHolder viewHolder = (CamaraAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(cameraList.indexOf(camara));
                    if (viewHolder != null) {
                        if (adapter.isCamaraFavorita(camara)) {
                            // Configura la animación como favorita
                            viewHolder.configurarAnimacionFavoritos(true);
                        } else {
                            // Configura la animación como no favorita
                            viewHolder.configurarAnimacionFavoritos(false);
                        }
                    }
                }

                return true; // Devuelve true para continuar con el proceso de dibujo
            }
        });

        recyclerView.setAdapter(adapter);


        // Configurar el RecyclerView y el adaptador
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);


        // Agregar un OnClickListener al ImageView
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acciones que deseas realizar cuando se hace clic en la imagen
                finish(); // Finalizar la actividad
            }
        });
    }
}
