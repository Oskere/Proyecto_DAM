package com.example.reto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final LatLng INITIAL_POSITION = new LatLng(43.008952, -2.472580);

    private ArrayList<Camara> cameraList = new ArrayList<>();
    private ArrayList<Incidencia> incidenceList = new ArrayList<>();

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
                int id = menuItem.getItemId();
                if (id == R.id.switch_Camaras || id == R.id.switch_Incidencias) {
                    View actionView = menuItem.getActionView();
                    Switch switchView = actionView.findViewById(R.id.switchItem);
                    switchView.setChecked(!switchView.isChecked());

                    if (switchView.isChecked()) {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(MainActivity.this, menuItem.getTitle() + " mostradas", duration);
                        toast.show();
                    } else {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(MainActivity.this, menuItem.getTitle() + " ocultas", duration);
                        toast.show();
                    }
                } else if (id == R.id.camList ) {
                    Intent lista = new Intent(MainActivity.this, listaCamaras.class);
                    lista.putExtra("titulo","Lista de Camaras:");
                    startActivity(lista);
                } else if (id == R.id.esp || id == R.id.ing) {
                    handleLanguageOptionClick(id);
                } else if (id == R.id.incList){

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
                                String id = incidence.getString("incidenceId");
                                String province = incidence.getString("province");
                                String carRegistration = incidence.getString("carRegistration");
                                String incidenceLevel = incidence.getString("incidenceLevel");
                                String road = incidence.getString("road");
                                String incidenceType = incidence.getString("incidenceType");
                                LatLng markerLatLng = new LatLng(latitude, longitude);
                                googleMap.addMarker(new MarkerOptions()
                                        .position(markerLatLng)
                                        .title(title)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.incidenceicon)));
                                Incidencia incidenceObject = new Incidencia(latitude, longitude, title, id, province, carRegistration, incidenceLevel, road, incidenceType);
                                incidenceList.add(incidenceObject);
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
                                String cameraId = camera.getString("cameraId");
                                String cameraRoad = camera.getString("road");
                                String kilometer = camera.getString("kilometer");
                                String address = camera.getString("address");
                                LatLng markerLatLng = new LatLng(latitude, longitude);
                                googleMap.addMarker(new MarkerOptions()
                                        .position(markerLatLng)
                                        .title(title));
                                Camara cameraObject = new Camara(latitude, longitude, title, cameraId, cameraRoad, kilometer, address);
                                cameraList.add(cameraObject);
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
