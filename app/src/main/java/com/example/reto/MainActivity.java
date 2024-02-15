package com.example.reto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.reto.Entidades.Camara;
import com.example.reto.Entidades.Incidencia;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String API_KEY = "DAMTraficoProyecto";
    private MapView mapView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final LatLng INITIAL_POSITION = new LatLng(43.008952, -2.472580);

    private ArrayList<Camara> cameraList = new ArrayList<>();
    private ArrayList<Camara> favList = new ArrayList<>();
    private ArrayList<Incidencia> incidenceList = new ArrayList<>();
    private ArrayList<Marker> allMarkers = new ArrayList<>();

    private static final String BASE_URL_LOCAL = "http://10.10.12.200:8080/api/incidencias";
    private int currentPageApi1 = 1;
    private int currentPageApi2 = 1;
    private boolean hasMorePagesApi1 = true;
    private boolean hasMorePagesApi2 = true;
    private Set<String> cameraNamesSet = new HashSet<>();
    private String username;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);

        String username = sharedPreferences.getString("username", "default_value");

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        LinearLayout cerrarSesion = findViewById(R.id.cerrarSesion);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        View clickBlockerView = findViewById(R.id.clickBlockerView);

        manejarLottieAnimation(true);
        clickBlockerView.setVisibility(View.VISIBLE);

        View headerView = navigationView.getHeaderView(0);

        TextView headerTitleTextView = headerView.findViewById(R.id.header_title);

        headerTitleTextView.setText(username);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                manejarLottieAnimation(false);
                clickBlockerView.setVisibility(View.GONE);
            }
        }, 5000);


        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open,
                R.string.close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                boolean isGuest = "Invitado".equals(username);

                if (id == R.id.switch_Camaras) {
                    View actionView = menuItem.getActionView();
                    Switch switchView = actionView.findViewById(R.id.switchItem);
                    switchView.setChecked(!switchView.isChecked());

                    if (switchView.isChecked()) {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(MainActivity.this, menuItem.getTitle() + " mostradas", duration);
                        toast.show();
                        ocultarCamaras(true);
                    } else {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(MainActivity.this, menuItem.getTitle() + " ocultas", duration);
                        toast.show();
                        ocultarCamaras(false);
                    }
                } else if (id == R.id.switch_Incidencias) {
                    View actionView = menuItem.getActionView();
                    Switch switchView = actionView.findViewById(R.id.switchItem);
                    switchView.setChecked(!switchView.isChecked());

                    if (switchView.isChecked()) {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(MainActivity.this, menuItem.getTitle() + " mostradas", duration);
                        toast.show();
                        ocultarIncidencias(true);
                    } else {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(MainActivity.this, menuItem.getTitle() + " ocultas", duration);
                        toast.show();
                        ocultarIncidencias(false);
                    }
                } else if (id == R.id.camList && !isGuest) {
                    Intent lista = new Intent(MainActivity.this, ListaCamaras.class);
                    lista.putExtra("titulo", "Lista de Camaras:");
                    lista.putExtra("cameraList", cameraList);
                    lista.putExtra("favList", favList);
                    startActivity(lista);
                } else if (id == R.id.incList && !isGuest) {
                    Intent listaIncidencias = new Intent(MainActivity.this, ListaIncidencias.class);
                    listaIncidencias.putExtra("titulo", "Lista de Incidencias:");
                    listaIncidencias.putExtra("incidenceList", incidenceList);
                    startActivity(listaIncidencias);
                } else if (id == R.id.graficos && !isGuest) {
                    Intent graficos = new Intent(MainActivity.this, Graficos.class);
                    startActivity(graficos);
                } else if (id == R.id.favoritos && !isGuest) {
                    Intent favoritos = new Intent(MainActivity.this, Favoritos.class);
                    favoritos.putExtra("favList", favList);
                    startActivity(favoritos);
                } else {
                    Toast.makeText(MainActivity.this, "Acceso denegado", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });
        favList = new ArrayList<>();
        realizarLlamadaAPI(username);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_POSITION, 9));
        googleMap.setOnMarkerClickListener(this);
        loadMarkersFromApi(googleMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);

        String username = sharedPreferences.getString("username", "default_value");
        favList.clear();
        realizarLlamadaAPI(username);
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
        Object associatedObject = marker.getTag();

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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View view = getLayoutInflater().inflate(R.layout.camara_menu, null);
        bottomSheetDialog.setContentView(view);

        String title = camara.getTitle();
        Long id = camara.getCameraId();
        String carretera = camara.getCameraRoad();
        String provincia = camara.getAddress();
        String imageUrl = camara.getImageUrl();

        TextView titulo = view.findViewById(R.id.tituloCamara);
        titulo.setText(title);
        TextView idCam = view.findViewById(R.id.idCamaraVar);
        idCam.setText(String.valueOf(id));
        TextView carreteraCam = view.findViewById(R.id.carreteraCamaraVar);
        carreteraCam.setText(carretera);
        TextView provinciaCam = view.findViewById(R.id.localidadCamaraVar);
        provinciaCam.setText(provincia);

        WebView webView = view.findViewById(R.id.imagenCamaraVar);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String html = "<html><body><img src=\"" + imageUrl + "\" width=\"100%\" height=\"100%\"></body></html>";
        webView.loadData(html, "text/html", "UTF-8");

        bottomSheetDialog.show();
    }

    private void showIncidenciaDetails(Incidencia incidencia) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View view = getLayoutInflater().inflate(R.layout.incidencia_menu, null);
        bottomSheetDialog.setContentView(view);

        TextView titleTextView = view.findViewById(R.id.tituloIncidencia);
        TextView textViewCarretera = view.findViewById(R.id.carreteraIncidenciaVar);
        TextView textViewId = view.findViewById(R.id.idIncidenciaVar);
        TextView textViewCoche = view.findViewById(R.id.carRegistrationVar);
        TextView textViewProvincia = view.findViewById(R.id.localidadIncidenciaVar);

        String title = incidencia.getTitle();
        Long id = incidencia.getId();
        String coche = incidencia.getIncidenceLevel();
        String carretera = incidencia.getRoad();
        String provincia = incidencia.getProvince();

        titleTextView.setText(title);
        textViewCarretera.setText(carretera);
        textViewId.setText(String.valueOf(id));
        textViewCoche.setText(coche);
        textViewProvincia.setText(provincia);

        bottomSheetDialog.show();
    }

    private void loadMarkersFromApi(final GoogleMap googleMap) {
        loadIncidenciasFromApi(googleMap);
        loadIncidenciasFromLocalApi(googleMap);
        loadCamarasFromApi(googleMap);
    }

    private void loadIncidenciasFromApi(final GoogleMap googleMap) {
        if (!hasMorePagesApi1) {
            return;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localDate = LocalDate.now();
        String apiUrl1 = "https://api.euskadi.eus/traffic/v1.0/incidences/byDate/";
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl1+ dtf.format(localDate) + "?_page=" + currentPageApi1,
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

                                if (latitude != 0 && longitude != 0) {
                                    String title = incidence.getString("cause");
                                    Long id = incidence.getLong("incidenceId");
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
                                    allMarkers.add(marker);
                                    incidenceList.add(incidenceObject);
                                }
                            }

                            currentPageApi1++;
                            Log.d("Pagina", "onResponse: " + currentPageApi1);

                            int num = 8;
                            Log.d("Paginas Totales", "onResponse: " + num);
                            if (!(num - currentPageApi1 > 0)) {
                                hasMorePagesApi1 = false;
                                return;
                            }
                            loadIncidenciasFromApi(googleMap);
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
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest1);
    }

    private void loadIncidenciasFromLocalApi(final GoogleMap googleMap) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL_LOCAL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d("IncidenciaMarker", "Número de incidencias: " + response.length());
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject incidence = response.getJSONObject(i);
                                Log.d("IncidenciaMarker", "onResponse: " + incidence);
                                double latitude = incidence.getDouble("latitude");
                                double longitude = incidence.getDouble("longitude");
                                String title = incidence.getString("title");
                                Long id = incidence.getLong("incidenceId");
                                String province = incidence.getString("province");
                                String carRegistration = incidence.getString("carRegistration");
                                String incidenceLevel = incidence.getString("incidenceLevel");
                                String road = incidence.getString("road");
                                String incidenceType = incidence.getString("incidenceType");
                                LatLng markerLatLng = new LatLng(latitude, longitude);
                                Log.d("IncidenciaMarker", "onResponse: " + markerLatLng);
                                Marker marker = googleMap.addMarker(new MarkerOptions()
                                        .position(markerLatLng)
                                        .title(title)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.incidenceicon)));
                                Incidencia incidenceObject = new Incidencia(latitude, longitude, title, id, province, carRegistration, incidenceLevel, road, incidenceType);
                                marker.setTag(incidenceObject);
                                allMarkers.add(marker);
                                Log.d("Incidencia", "onResponse: " + marker.getTitle());
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
                        Log.e("Volley", "Error en la solicitud: " + error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("ApiKey", API_KEY);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void loadCamarasFromApi(final GoogleMap googleMap) {
        if (!hasMorePagesApi2) {
            return;
        }

        String apiUrl2 = "https://api.euskadi.eus/traffic/v1.0/cameras";
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

                                String[] latLng = convertCoordinates(utmX, utmY);
                                double latitude = Double.parseDouble(latLng[0]);
                                double longitude = Double.parseDouble(latLng[1]);
                                if (latitude > 10) {
                                    String title = camera.getString("cameraName");
                                    String cameraId = camera.getString("cameraId");
                                    Long idCamara = Long.parseLong(cameraId);
                                    String cameraRoad = camera.optString("road", "");
                                    String kilometer = camera.optString("kilometer", "");
                                    String address = camera.optString("address", "");
                                    String imageUrl = camera.optString("urlImage", "");
                                    imageUrl = imageUrl.replace("http://", "https://");
                                    LatLng markerLatLng = new LatLng(latitude, longitude);
                                    Camara cameraObject = new Camara(latitude, longitude, title, idCamara, cameraRoad, kilometer, address, imageUrl);
                                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                            .position(markerLatLng)
                                            .title(title)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.camara_marker)));
                                    marker.setTag(cameraObject);
                                    if (!cameraNamesSet.contains(title)) {
                                        cameraNamesSet.add(title);
                                        allMarkers.add(marker);
                                        Log.d("Marcador", "ID camara: " + cameraId);
                                        cameraList.add(cameraObject);
                                    }
                                }
                            }

                            currentPageApi2++;
                            Log.d("Pagina API 2", "onResponse: " + currentPageApi2);

                            int num = 25;

                            if (!(num - currentPageApi2 >= 0)) {
                                hasMorePagesApi2 = false;
                                return;
                            }
                            Log.d("Mas paginas", "onResponse: " + hasMorePagesApi2);
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

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest2);
    }

    public String[] convertCoordinates(double longitude, double latitude) {
        double easting = longitude;
        double northing = latitude;
        int zone = 30;

        String utmDefinition = "+proj=utm +zone=" + zone + " +ellps=WGS84";
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem utmCrs = crsFactory.createFromParameters("UTM", utmDefinition);

        String wgs84Definition = "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs";
        CoordinateReferenceSystem wgs84Crs = crsFactory.createFromParameters("WGS84", wgs84Definition);

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform utmToLatLon = ctFactory.createTransform(utmCrs, wgs84Crs);

        ProjCoordinate utmCoord = new ProjCoordinate(easting, northing);
        ProjCoordinate latLonCoord = new ProjCoordinate();
        utmToLatLon.transform(utmCoord, latLonCoord);

        String[] coordCamaras = new String[2];
        coordCamaras[0] = String.valueOf(latLonCoord.y);
        coordCamaras[1] = String.valueOf(latLonCoord.x);

        return coordCamaras;
    }

    private void ocultarCamaras(boolean mostrar) {
        for (Marker marker : allMarkers) {
            Object tag = marker.getTag();
            if (tag instanceof Camara) {
                marker.setVisible(mostrar);
            }
        }
    }

    private void ocultarIncidencias(boolean mostrar) {
        for (Marker marker : allMarkers) {
            Object tag = marker.getTag();
            if (tag instanceof Incidencia) {
                marker.setVisible(mostrar);
            }
        }
    }
    private void manejarLottieAnimation(boolean mostrar) {
        if (mostrar) {
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
        } else {
            lottieAnimationView.setVisibility(View.GONE);
            lottieAnimationView.cancelAnimation();
        }
    }

    private void realizarLlamadaAPI(String username) {
        String apiUrl = "http://10.10.12.200:8080/api/camaras/" + username;
        Log.d("Favoritos", "Response JSONArray: " + apiUrl);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response != null && response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject camera = response.getJSONObject(i);
                                    double longitude = camera.getDouble("longitude");
                                    double latitude = camera.getDouble("latitude");
                                    String title = camera.getString("title");
                                    String cameraId = camera.getString("cameraId");
                                    Long idCamara = Long.parseLong(cameraId);
                                    String cameraRoad = camera.optString("road", "");
                                    String kilometer = camera.optString("kilometer", "");
                                    String address = camera.optString("address", "");
                                    String imageUrl = camera.optString("urlImage", "");
                                    Camara cameraObject = new Camara(latitude, longitude, title, idCamara, cameraRoad, kilometer, address, imageUrl);
                                    favList.add(cameraObject);
                                }
                            } else {
                                Log.d("Favoritos", "La matriz de cámaras está vacía");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Favoritos", "Error al procesar la respuesta JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error en la solicitud: " + error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("ApiKey", API_KEY);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

}
