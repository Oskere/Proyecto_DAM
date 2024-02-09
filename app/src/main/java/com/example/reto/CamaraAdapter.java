package com.example.reto;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.reto.Entidades.Camara;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CamaraAdapter extends RecyclerView.Adapter<CamaraAdapter.ViewHolder> {
    private static final String API_KEY = "DAMTraficoProyecto";
    private List<Camara> cameraList;
    private List<Camara> favList;
    private LayoutInflater inflater;
    private Context context;
    private String username;
    private final RequestQueue requestQueue;

    public CamaraAdapter(Context context, List<Camara> cameraList, List<Camara> favList, String username) {
        this.inflater = LayoutInflater.from(context);
        this.cameraList = cameraList;
        this.favList = favList;
        this.context = context;
        this.username = username;
        requestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_filtro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Camara camera = cameraList.get(position);
        holder.bind(camera);
    }

    @Override
    public int getItemCount() {
        return cameraList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nombreTextView;
        private final TextView kilometerTextView;
        private final LottieAnimationView lottieAnimationView;
        private boolean isAnimationPlayingForward = false;


        public ViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.TextViewFiltro1);
            kilometerTextView = itemView.findViewById(R.id.TextViewFiltro2);
            lottieAnimationView = itemView.findViewById(R.id.lottieAnimationView);
        }

        public void bind(Camara camera) {
            nombreTextView.setText(camera.getTitle());
            kilometerTextView.setText(camera.getKilometer());
            lottieAnimationView.cancelAnimation(); // Cancelar cualquier animación previa
            // Asignar el evento onClick al nombre de la cámara
            nombreTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acciones que deseas realizar cuando se hace clic en el nombre de la cámara
                    mostrarPopupDesdeAbajo(camera);
                }
            });

            // Asignar el evento onClick a la imagen de la cámara
            lottieAnimationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Verificar el estado actual de la animación
                    if (lottieAnimationView.getProgress() == 1f) {
                        // Si la animación está completa (estado 1f), realizar la operación de quitar
                        eliminarCamaraDeBaseDeDatos(camera.getCameraId());
                        lottieAnimationView.setSpeed(-2);
                        lottieAnimationView.playAnimation();
                        // No es necesario cambiar la dirección o actualizar la velocidad
                    } else {
                        // Si la animación no está completa, cambiar la dirección y velocidad para agregar
                        isAnimationPlayingForward = !isAnimationPlayingForward;
                        if (isAnimationPlayingForward) {
                            // Reproducir la animación hacia adelante
                            lottieAnimationView.setSpeed(1);
                            guardarCamaraEnBaseDeDatos(camera);
                        } else {
                            // Reproducir la animación hacia atrás
                            lottieAnimationView.setSpeed(-2);
                        }
                        // Reproducir la animación
                        lottieAnimationView.playAnimation();
                    }
                }
            });
        }
        public void configurarAnimacionFavoritos(boolean esFavorito) {
            if (esFavorito) {
                lottieAnimationView.setProgress(1f);
                lottieAnimationView.setSpeed(1);
                lottieAnimationView.playAnimation();
            } else {
                lottieAnimationView.setProgress(0f);
                lottieAnimationView.setSpeed(-1);
                lottieAnimationView.playAnimation();
            }
        }
    }

    public boolean isCamaraFavorita(Camara camara) {
        // Supongamos que la clase Camara tiene un método getCameraId() para obtener el ID de la cámara

        for (Camara camaraFavorita : favList) {
            if (camaraFavorita.getTitle().equals(camara.getTitle())) {
                return true; // La cámara está en la lista de favoritos
            }
        }

        return false; // La cámara no está en la lista de favoritos
    }
    private void mostrarPopupDesdeAbajo(Camara camera) {
        // Crear un BottomSheetDialog personalizado con el diseño del popup
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View popupView = inflater.inflate(R.layout.camara_menu, null);

        // Configurar los elementos del popup con la información de la cámara
        TextView popupNombre = popupView.findViewById(R.id.tituloCamara);
        TextView popupIdCamaraVar = popupView.findViewById(R.id.idCamaraVar);
        TextView popupLocalidadCamaraVar = popupView.findViewById(R.id.localidadCamaraVar);
        TextView popupCarreteraCamaraVar = popupView.findViewById(R.id.carreteraCamaraVar);

        popupNombre.setText(camera.getTitle());
        popupIdCamaraVar.setText(String.valueOf(camera.getCameraId()));
        popupLocalidadCamaraVar.setText(camera.getAddress());
        popupCarreteraCamaraVar.setText(camera.getCameraRoad());

        // Puedes agregar más configuraciones según los elementos en el popup_camara.xml

        // Agregar el popupView al BottomSheetDialog y mostrarlo
        bottomSheetDialog.setContentView(popupView);
        bottomSheetDialog.show();
    }
    private void guardarCamaraEnBaseDeDatos(Camara camara) {
        String url = "http://10.10.12.200:8080/api/camaras";
        Camara cam = camara;
        Log.d("IDD", ""+cam.getCameraId());
        // Convertir la instancia de Camara a un objeto JSON
        JSONObject jsonCamara = new JSONObject();
        try {
            jsonCamara.put("title", cam.getTitle());
            jsonCamara.put("kilometer", cam.getKilometer());
            jsonCamara.put("cameraId",cam.getCameraId());
            jsonCamara.put("address",cam.getAddress());
            jsonCamara.put("cameraRoad", cam.getCameraRoad());
            jsonCamara.put("latitude", cam.getLatitude());
            jsonCamara.put("imageUrl", cam.getImageUrl());
            jsonCamara.put("longitude", cam.getLongitude());
            jsonCamara.put("username", username);

            // Agrega otros campos según la estructura de tu entidad Camara en el backend
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Crear una solicitud POST con Volley
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonCamara,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Lógica para manejar la respuesta exitosa
                        // Puedes implementar un callback si lo necesitas
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("FAVS", "Error en la solicitud. Código de estado: " + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Agregar el encabezado "ApiKey" al encabezado de la solicitud
                Map<String, String> headers = new HashMap<>();
                headers.put("ApiKey", API_KEY);
                return headers;
            }
        };

        // Añadir la solicitud a la cola
        requestQueue.add(request);
    }

    private void eliminarCamaraDeBaseDeDatos(Long camaraId) {
        String url = "http://10.10.12.200:8080/api/camaras/" + camaraId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Lógica para manejar la respuesta exitosa
                        // Puedes implementar un callback si lo necesitas
                        Log.d("DELETE_SUCCESS", "Cámara eliminada exitosamente");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Lógica para manejar errores
                        // Puedes implementar un callback para manejar los errores
                        Log.e("DELETE_ERROR", "Error al eliminar cámara: " + error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Agregar el encabezado "ApiKey" al encabezado de la solicitud
                Map<String, String> headers = new HashMap<>();
                headers.put("ApiKey", API_KEY);
                return headers;
            }
        };

        // Añadir la solicitud a la cola
        requestQueue.add(request);
    }
}
