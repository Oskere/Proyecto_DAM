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

        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);

        String username = sharedPreferences.getString("username", "default_value");
        Log.d("Usuario", username);

        List<Camara> cameraList = (List<Camara>) intent.getSerializableExtra("cameraList");
        List<Camara> favList = (List<Camara>) intent.getSerializableExtra("favList");


        RecyclerView recyclerView = findViewById(R.id.recycler);
        CamaraAdapter adapter = new CamaraAdapter(this, cameraList, favList, username);

        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                for (Camara camara : cameraList) {
                    CamaraAdapter.ViewHolder viewHolder = (CamaraAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(cameraList.indexOf(camara));
                    if (viewHolder != null) {
                        if (adapter.isCamaraFavorita(camara)) {
                            viewHolder.configurarAnimacionFavoritos(true);
                        } else {
                            viewHolder.configurarAnimacionFavoritos(false);
                        }
                    }
                }

                return true;
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
