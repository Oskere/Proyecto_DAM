package com.example.reto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reto.Entidades.Incidencia;

import java.util.ArrayList;
import java.util.List;

public class ListaIncidencias extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclercamaras);

        Intent intent = getIntent();
        TextView titulo = findViewById(R.id.tituloLista);
        titulo.setText(intent.getStringExtra("titulo"));

        List<Incidencia> incidenceList = (List<Incidencia>) intent.getSerializableExtra("incidenceList");

        RecyclerView recyclerView = findViewById(R.id.recycler);
        IncidenciaAdapter adapter = new IncidenciaAdapter(this, incidenceList);
        recyclerView.setAdapter(adapter);

        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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
