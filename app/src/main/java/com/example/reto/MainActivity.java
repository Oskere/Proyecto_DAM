package com.example.reto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.locationtech.proj4j.*;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private MapView mapView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final LatLng INITIAL_POSITION = new LatLng(43.008952, -2.472580);

    private ArrayList<Camara> cameraList = new ArrayList<>();
    private ArrayList<Incidencia> incidenceList = new ArrayList<>();

    private int currentPageApi1 = 1;
    private int currentPageApi2 = 1;
    private boolean hasMorePagesApi1 = true;
    private boolean hasMorePagesApi2 = true;

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
        // Registrar la actividad como el listener de clics en marcadores
        googleMap.setOnMarkerClickListener(this);
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Obtener el objeto asociado al marcador
        Object associatedObject = marker.getTag();

        // Verificar el tipo del objeto y mostrar detalles según sea necesario
        if (associatedObject instanceof Incidencia) {
            Incidencia incidencia = (Incidencia) associatedObject;
            showIncidenciaDetails(incidencia);
        } else if (associatedObject instanceof Camara) {
            Camara camara = (Camara) associatedObject;
            showCamaraDetails(camara);
        }

        return true;
    }

    private void showCamaraDetails(Camara camara) {
        // Crear un BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Inflar el diseño del contenido del marcador
        View view = getLayoutInflater().inflate(R.layout.camara_menu, null);
        bottomSheetDialog.setContentView(view);

        // ...

        // Obtener los datos del marcador
        String title = camara.getTitle();
        String id = camara.getCameraId();
        String carretera = camara.getCameraRoad();
        String provincia = camara.getAddress();
        String imageUrl = camara.getImageUrl();

        TextView titulo = view.findViewById(R.id.tituloCamara);
        titulo.setText(title);
        TextView idCam = view.findViewById(R.id.idCamaraVar);
        idCam.setText(id);
        TextView carreteraCam = view.findViewById(R.id.carreteraCamaraVar);
        carreteraCam.setText(carretera);
        TextView provinciaCam = view.findViewById(R.id.localidadCamaraVar);
        provinciaCam.setText(provincia);


        // Configura las opciones del WebView
        WebView webView = view.findViewById(R.id.imagenCamaraVar);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Habilita JavaScript si es necesario

        String html = "<html><body><img src=\"" + imageUrl + "\" width=\"100%\" height=\"100%\"></body></html>";
        webView.loadData(html, "text/html", "UTF-8");

        // ...

        // Mostrar el BottomSheetDialog
        bottomSheetDialog.show();
    }



    private void showIncidenciaDetails(Incidencia incidencia) {
        // Crear un BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Inflar el diseño del contenido del marcador
        View view = getLayoutInflater().inflate(R.layout.incidencia_menu, null);
        bottomSheetDialog.setContentView(view);

        // Obtener referencias a las vistas en el diseño
        TextView titleTextView = view.findViewById(R.id.tituloIncidencia);
        TextView textViewCarretera = view.findViewById(R.id.carreteraIncidenciaVar);
        TextView textViewId = view.findViewById(R.id.idIncidenciaVar);
        TextView textViewCoche = view.findViewById(R.id.carRegistrationVar);
        TextView textViewProvincia = view.findViewById(R.id.localidadIncidenciaVar);

        // Obtener los datos del marcador
        String title = incidencia.getTitle();
        String id = incidencia.getId();
        String coche = incidencia.getCarRegistration();
        String carretera = incidencia.getRoad();
        String provincia = incidencia.getProvince();
        // Puedes obtener datos adicionales del marcador utilizando la lista de cámaras e incidencias
        // según sea necesario


        // Configurar las vistas con los datos del marcador
        titleTextView.setText(title);
        textViewCarretera.setText(carretera);
        textViewId.setText(id);
        textViewCoche.setText(coche);
        textViewProvincia.setText(provincia);


        // Configurar otras vistas con datos adicionales si es necesario

        // Mostrar el BottomSheetDialog
        bottomSheetDialog.show();
    }

    private void loadMarkersFromApi(final GoogleMap googleMap) {
            // Primera solicitud a la API para incidencias
            loadIncidenciasFromApi(googleMap);

            // Segunda solicitud a la API para cámaras
            loadCamarasFromApi(googleMap);
    }

    private void loadIncidenciasFromApi(final GoogleMap googleMap) {
        if (!hasMorePagesApi1) {
            // Si no hay más páginas, salir del método
            return;
        }

        String apiUrl1 = "https://api.euskadi.eus/traffic/v1.0/incidences";
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl1 + "?_page=" + currentPageApi1,
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

                                // Verifica si la latitud y la longitud son diferentes de 0 antes de añadir el marcador
                                if (latitude != 0 && longitude != 0) {
                                    String title = incidence.getString("cause");
                                    String id = incidence.getString("incidenceId");
                                    String province = incidence.getString("province");
                                    String carRegistration = incidence.getString("carRegistration");
                                    String incidenceLevel = incidence.getString("incidenceLevel");
                                    String road = incidence.getString("road");
                                    String incidenceType = incidence.getString("incidenceType");

                                    LatLng markerLatLng = new LatLng(latitude, longitude);
                                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                            .position(markerLatLng)
                                            .title(title)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.incidenceicon)));
                                    Incidencia incidenceObject = new Incidencia(latitude, longitude, title, id, province, carRegistration, incidenceLevel, road, incidenceType);
                                    marker.setTag(incidenceObject);
                                    incidenceList.add(incidenceObject);
                                }
                            }

                            // Asegúrate de actualizar currentPageApi1 después de procesar la respuesta
                            currentPageApi1++;
                            Log.d("Pagina", "onResponse: "+currentPageApi1);

                            int num = 8;
                            Log.d("Paginas Totales", "onResponse: "+num);
                            if (!(num - currentPageApi1 > 0)) {
                                hasMorePagesApi1 = false;
                                return;
                            }

                            // Verifica si hay más páginas según la información de la respuesta


                            // Realiza la siguiente solicitud de página si es necesario
                            loadIncidenciasFromApi(googleMap);
                            Log.d("Mas paginas", "onResponse: "+hasMorePagesApi1);
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

        // Añadir la solicitud a la cola de Volley
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest1);
    }

    private void loadCamarasFromApi(final GoogleMap googleMap) {
        if (!hasMorePagesApi2) {
            // Si no hay más páginas, salir del método
            return;
        }

        // Segunda solicitud a la API para cámaras
        String apiUrl2 = "https://api.euskadi.eus/traffic/v1.0/cameras";
        Log.d("Request OPIO", apiUrl2+"?_page="+currentPageApi2);
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl2 + "?_page=" + currentPageApi2,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray cameras = response.getJSONArray("cameras");
                            for (int i = 0; i < cameras.length(); i++) {
                                JSONObject camera = cameras.getJSONObject(i);
                                double utmX = camera.getDouble("longitude");
                                double utmY = camera.getDouble("latitude");

                                // Convierte las coordenadas UTM a latitud y longitud
                                String[] latLng = convertCoordinates(utmX, utmY);
                                double latitude = Double.parseDouble(latLng[0]);
                                double longitude = Double.parseDouble(latLng[1]);
                                String title = camera.getString("cameraName");
                                String cameraId = camera.getString("cameraId");
                                String cameraRoad = camera.optString("road","");
                                String kilometer = camera.optString("kilometer","");
                                String address = camera.optString("address","");
                                String imageUrl = camera.optString("urlImage", "");
                                imageUrl = imageUrl.replace("http://", "https://");
                                LatLng markerLatLng = new LatLng(latitude, longitude);
                                Camara cameraObject = new Camara(latitude, longitude, title, cameraId, cameraRoad, kilometer, address, imageUrl);
                                Marker marker = googleMap.addMarker(new MarkerOptions()
                                        .position(markerLatLng)
                                        .title(title)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.camara_marker)));
                                marker.setTag(cameraObject);
                                Log.d("Marcador", "ID camara: "+cameraId);
                                cameraList.add(cameraObject);
                            }

                            currentPageApi2++;
                            Log.d("Pagina API 2", "onResponse: "+currentPageApi2);

                            int num = 25;

                            if (!(num - currentPageApi2 >= 0)) {
                                hasMorePagesApi2 = false;
                                return;
                            }
                            Log.d("Paginas Totales API 2", "onResponse: "+num);

                            // Verifica si hay más páginas según la información de la respuesta


                            // Realiza la siguiente solicitud de página si es necesario
                            Log.d("Mas paginas", "onResponse: "+hasMorePagesApi2);
                            loadCamarasFromApi(googleMap);

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

        // Añadir la solicitud a la cola de Volley
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest2);
    }

    public String[] convertCoordinates(double longitude, double latitude){
        // UTM coordinates for Zone 30T
        double easting = longitude; // Replace with your UTM easting value
        double northing = latitude; // Replace with your UTM northing value
        int zone = 30;

        // Define the UTM projection for Zone 30T
        String utmDefinition = "+proj=utm +zone=" + zone + " +ellps=WGS84";
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem utmCrs = crsFactory.createFromParameters("UTM", utmDefinition);

        // Define the WGS84 geographic coordinate system
        String wgs84Definition = "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs";
        CoordinateReferenceSystem wgs84Crs = crsFactory.createFromParameters("WGS84", wgs84Definition);

        // Create a CoordinateTransformFactory and create a CoordinateTransform
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform utmToLatLon = ctFactory.createTransform(utmCrs, wgs84Crs);

        // Transform UTM coordinates to latitude and longitude
        ProjCoordinate utmCoord = new ProjCoordinate(easting, northing);
        ProjCoordinate latLonCoord = new ProjCoordinate();
        utmToLatLon.transform(utmCoord, latLonCoord);

        String[] coordCamaras = new String[2]; // Tamaño 2 para latitud y longitud
        coordCamaras[0] = String.valueOf(latLonCoord.y); // Latitud
        coordCamaras[1] = String.valueOf(latLonCoord.x); // Longitud

        return coordCamaras;
    }

}
