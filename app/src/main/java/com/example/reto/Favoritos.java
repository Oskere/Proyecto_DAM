package com.example.reto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.reto.Entidades.Camara;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Favoritos extends AppCompatActivity {

    private List<Camara> cameraList;
    private FavoritosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclercamaras);

        Intent intent = getIntent();
        TextView titulo = findViewById(R.id.tituloLista);
        titulo.setText("Favoritos:");

        cameraList = (List<Camara>) intent.getSerializableExtra("favList");

        Log.d("LISTAFAV", ""+cameraList);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        adapter = new FavoritosAdapter(this,cameraList);
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
