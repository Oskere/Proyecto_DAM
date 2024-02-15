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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder> {
    private static final String API_KEY = "DAMTraficoProyecto";
    private List<Camara> cameraList;
    private LayoutInflater inflater;
    private Context context;
    private final RequestQueue requestQueue;

    public FavoritosAdapter(Context context, List<Camara> cameraList) {
        this.inflater = LayoutInflater.from(context);
        this.cameraList = cameraList;
        this.context = context;
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
        private boolean isAnimationPlayed = false;

        public ViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.TextViewFiltro1);
            kilometerTextView = itemView.findViewById(R.id.TextViewFiltro2);
            lottieAnimationView = itemView.findViewById(R.id.lottieAnimationView);
        }

        public void bind(Camara camera) {
            nombreTextView.setText(camera.getTitle());
            kilometerTextView.setText(camera.getKilometer());

            nombreTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarPopupDesdeAbajo(camera);
                }
            });
            lottieAnimationView.setProgress(1f);
            lottieAnimationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isAnimationPlayed) {
                        isAnimationPlayed = true;

                        lottieAnimationView.setSpeed(-8);
                        eliminarCamaraDeBaseDeDatos(camera.getCameraId());

                        lottieAnimationView.playAnimation();
                        removeCamera(camera);

                    }
                }
            });
        }

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

    public void removeCamera(Camara camera) {
        int position = cameraList.indexOf(camera);
        if (position != -1) {
            cameraList.remove(position);
            notifyItemRemoved(position);
        }
    }

}
