package com.example.reto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        LinearLayout cerrarSesion = findViewById(R.id.cerrarSesion);

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
                } else if (id == R.id.camList ) {
                    // Handle filter options
                    Intent lista = new Intent(MainActivity.this, listaCamaras.class);
                    lista.putExtra("titulo","Lista de Camaras:");
                    startActivity(lista);
                    //handleFilterOptionClick(id);
                } else if (id == R.id.esp || id == R.id.ing) {
                    handleLanguageOptionClick(id);
                } else if (id == R.id.incList){

                } else if (id == R.id.graficos) {

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
        // Realizar la solicitud a la API y añadir marcadores
        loadMarkersFromApi(googleMap);
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

    private void loadMarkersFromApi(final GoogleMap googleMap) {
        // Primer endpoint
        String apiUrl1 = "https://api.euskadi.eus/traffic/v1.0/incidences";
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl1,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray incidences = response.getJSONArray("incidences");
                            for (int i = 0; i < incidences.length(); i++) {
                                JSONObject incidence = incidences.getJSONObject(i);
                                double latitude = incidence.getDouble("latitude");
                                double longitude = incidence.getDouble("longitude");
                                String title = incidence.getString("cause");
                                LatLng markerLatLng = new LatLng(latitude, longitude);
                                googleMap.addMarker(new MarkerOptions()
                                        .position(markerLatLng)
                                        .title(title)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.incidenceicon)));
                                Log.d("Marcador", "Latitud: " + latitude + ", Longitud: " + longitude + ", cause: " + title);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error al cargar los datos del primer endpoint", Toast.LENGTH_SHORT).show();
                    }
                });

        // Segundo endpoint
        String apiUrl2 = "https://api.euskadi.eus/traffic/v1.0/cameras";
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl2,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray cameras = response.getJSONArray("cameras");
                            for (int i = 0; i < cameras.length(); i++) {
                                JSONObject camera = cameras.getJSONObject(i);
                                double latitude = camera.getDouble("latitude");
                                double longitude = camera.getDouble("longitude");
                                String title = camera.getString("cameraName");
                                LatLng markerLatLng = new LatLng(latitude, longitude);
                                googleMap.addMarker(new MarkerOptions()
                                        .position(markerLatLng)
                                        .title(title));
                                Log.d("Marcador", "Latitud: " + latitude + ", Longitud: " + longitude + ", Título: " + title);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error al cargar los datos del segundo endpoint", Toast.LENGTH_SHORT).show();
                    }
                });

        // Añadir las solicitudes a la cola de Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest1);
        requestQueue.add(jsonObjectRequest2);
    }

}

