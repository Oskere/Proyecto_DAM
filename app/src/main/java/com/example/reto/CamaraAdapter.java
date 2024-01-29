package com.example.reto;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import org.mindrot.jbcrypt.BCrypt;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class CamaraAdapter extends RecyclerView.Adapter<CamaraAdapter.ViewHolder> {

    private List<Camara> cameraList;
    private LayoutInflater inflater;
    private Context context;
    private final RequestQueue requestQueue;

    public CamaraAdapter(Context context, List<Camara> cameraList) {
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
                    // Cambiar la dirección de la animación
                    isAnimationPlayingForward = !isAnimationPlayingForward;

                    // Actualizar la velocidad y reproducir la animación
                    if (isAnimationPlayingForward) {
                        // Reproducir la animación hacia adelante
                        lottieAnimationView.setSpeed(1);
                        guardarCamaraEnBaseDeDatos(camera);
                    } else {
                        // Reproducir la animación hacia atrás
                        lottieAnimationView.setSpeed(-2);
                        eliminarCamaraDeBaseDeDatos(Long.parseLong(camera.getCameraId()));
                    }
                    // Reproducir la animación
                    lottieAnimationView.playAnimation();

                }
            });
        }
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
        popupIdCamaraVar.setText(camera.getCameraId());
        popupLocalidadCamaraVar.setText(camera.getAddress());
        popupCarreteraCamaraVar.setText(camera.getCameraRoad());

        // Puedes agregar más configuraciones según los elementos en el popup_camara.xml

        // Agregar el popupView al BottomSheetDialog y mostrarlo
        bottomSheetDialog.setContentView(popupView);
        bottomSheetDialog.show();
    }
    private void guardarCamaraEnBaseDeDatos(Camara camara) {
        String url = "http://10.10.12.200/api/camaras";

        // Convertir la instancia de Camara a un objeto JSON
        JSONObject jsonCamara = new JSONObject();
        try {
            jsonCamara.put("title", camara.getTitle());
            jsonCamara.put("kilometer", camara.getKilometer());
            jsonCamara.put("id",camara.getCameraId());
            jsonCamara.put("address",camara.getAddress();

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
                        Log.e("FAVS", "Error en la solicitud: " + error.getMessage());
                    }
                });

        // Añadir la solicitud a la cola
        requestQueue.add(request);
    }

    private void eliminarCamaraDeBaseDeDatos(Long camaraId) {
        String url = "http://10.10.12.200/api/camaras/" + camaraId;  // Reemplaza con la URL correcta

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
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
                        // Lógica para manejar errores
                        // Puedes implementar un callback para manejar los errores
                    }
                });

        // Añadir la solicitud a la cola
        requestQueue.add(request);
    }
}
