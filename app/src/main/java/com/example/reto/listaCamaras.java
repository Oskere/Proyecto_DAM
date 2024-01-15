package com.example.reto;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class listaCamaras extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler);

        // Crear datos de ejemplo
        List<camaras> cameraList = new ArrayList<camaras>();
        cameraList.add(new camaras("75", "2", "CCTV 232 - Cámara DOMO 232", "4792953.64", "507650.15", "N - 637", "016+500", "Cruces", "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.istockphoto.com%2Fes%2Ffotos%2Ftr%25C3%25A1fico&psig=AOvVaw3vofCuo3T7LUvBrCE4fuvF&ust=1705396906934000&source=images&cd=vfe&ved=0CBIQjRxqFwoTCLCEpdiI34MDFQAAAAAdAAAAABAE"));
        // Agregar más cámaras según sea necesario

        // Configurar el RecyclerView y el adaptador
        RecyclerView recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cameraAdapter adapter = new cameraAdapter(this,cameraList);
        recyclerView.setAdapter(adapter);
    }
}