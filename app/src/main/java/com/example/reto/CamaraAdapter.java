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
            lottieAnimationView.cancelAnimation();
            nombreTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarPopupDesdeAbajo(camera);
                }
            });

            lottieAnimationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lottieAnimationView.getProgress() == 1f) {
                        eliminarCamaraDeBaseDeDatos(camera.getCameraId());
                        lottieAnimationView.setSpeed(-2);
                        lottieAnimationView.playAnimation();
                    } else {
                        isAnimationPlayingForward = !isAnimationPlayingForward;
                        if (isAnimationPlayingForward) {
                            lottieAnimationView.setSpeed(1);
                            guardarCamaraEnBaseDeDatos(camera);
                        } else {
                            lottieAnimationView.setSpeed(-2);
                        }
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

        for (Camara camaraFavorita : favList) {
            if (camaraFavorita.getTitle().equals(camara.getTitle())) {
                return true;
            }
        }

        return false;
    }
    private void mostrarPopupDesdeAbajo(Camara camera) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View popupView = inflater.inflate(R.layout.camara_menu, null);

        TextView popupNombre = popupView.findViewById(R.id.tituloCamara);
        TextView popupIdCamaraVar = popupView.findViewById(R.id.idCamaraVar);
        TextView popupLocalidadCamaraVar = popupView.findViewById(R.id.localidadCamaraVar);
        TextView popupCarreteraCamaraVar = popupView.findViewById(R.id.carreteraCamaraVar);

        popupNombre.setText(camera.getTitle());
        popupIdCamaraVar.setText(String.valueOf(camera.getCameraId()));
        popupLocalidadCamaraVar.setText(camera.getAddress());
        popupCarreteraCamaraVar.setText(camera.getCameraRoad());

        bottomSheetDialog.setContentView(popupView);
        bottomSheetDialog.show();
    }
    private void guardarCamaraEnBaseDeDatos(Camara camara) {
        String url = "http://10.10.12.200:8080/api/camaras";
        Camara cam = camara;
        Log.d("IDD", ""+cam.getCameraId());
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonCamara,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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
                Map<String, String> headers = new HashMap<>();
                headers.put("ApiKey", API_KEY);
                return headers;
            }
        };
        requestQueue.add(request);
    }

    private void eliminarCamaraDeBaseDeDatos(Long camaraId) {
        String url = "http://10.10.12.200:8080/api/camaras/" + camaraId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("DELETE_SUCCESS", "Cámara eliminada exitosamente");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DELETE_ERROR", "Error al eliminar cámara: " + error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("ApiKey", API_KEY);
                return headers;
            }
        };
        requestQueue.add(request);
    }
}
