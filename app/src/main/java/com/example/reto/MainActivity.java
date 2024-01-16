package com.example.reto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final LatLng INITIAL_POSITION = new LatLng(43.008952, -2.472580);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        TextView cerrarSesion = navigationView.findViewById(R.id.nav_footer_textview);

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "Cerrar Sesión" click
                // Perform any necessary actions before finishing the activity if needed
                finish(); // Finish the activity
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Configurar el ActionBarDrawerToggle para sincronizar el ícono de hamburguesa con el DrawerLayout
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open,
                R.string.close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Manejar eventos de clic en elementos del menú
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // Manejar clic en elementos del menú aquí
                // Por ejemplo, cambiar fragmento o iniciar una nueva actividad
                // Get the ID of the clicked menu item
                int id = menuItem.getItemId();
                // Handle the click event based on the ID
                if (id == R.id.switch_Camaras || id == R.id.switch_Incidencias) {
                    // Find the switch in the action view of the menu item
                    View actionView = menuItem.getActionView();
                    Switch switchView = actionView.findViewById(R.id.switchItem);

                    // Toggle the state of the switch
                    switchView.setChecked(!switchView.isChecked());

                    // Handle the switch state change as needed
                    if (switchView.isChecked()) {
                        // Switch is ON
                        // Handle the action when the switch is ON
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(MainActivity.this, menuItem.getTitle() + " mostradas", duration);
                        toast.show();
                    } else {
                        // Switch is OFF
                        // Handle the action when the switch is OFF
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(MainActivity.this, menuItem.getTitle() + " ocultas", duration);
                        toast.show();
                    }
                } else if (id == R.id.camList || id == R.id.incList) {
                    // Handle filter options
                    Intent lista = new Intent(MainActivity.this, listaCamaras.class);
                    startActivity(lista);
                    handleFilterOptionClick(id);
                } else if (id == R.id.esp || id == R.id.ing) {
                    // Handle language options
                    handleLanguageOptionClick(id);
                } else if (id == R.id.graficos) {
                    Intent graficos = new Intent(MainActivity.this, Graficos.class);
                    startActivity(graficos);
                }
                // Add more conditions for other menu items

                // Close the DrawerLayout after handling the click


                return true;
            }
        });

    }

    private void handleLanguageOptionClick(int id) {

    }

    private void handleFilterOptionClick(int id) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar clic en el ícono de hamburguesa en la ActionBar
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Mover la cámara a la posición inicial
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_POSITION, 9));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}

