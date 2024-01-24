package com.example.reto;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LoginManager {

    private static final String BASE_URL = "http://10.10.12.200:8080/api/usuarios"; // Reemplaza con tu dirección de la API

    private final RequestQueue requestQueue;

    public LoginManager(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void registrarUsuario(String username, String email, String password, final ApiCallback callback) throws JSONException {
        String url = BASE_URL + "/registro";

        // Construir el cuerpo de la solicitud
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);
        jsonBody.put("email", email);
        jsonBody.put("password", password);

        // Crear la solicitud POST
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    // Aquí puedes imprimir la respuesta para depurar
                    Log.d("Respuesta del servidor", response.toString());
                    callback.onSuccess(response.toString());
                },
                error -> callback.onError(error.getMessage()));

        // Añadir la solicitud a la cola
        requestQueue.add(request);
    }


    public void iniciarSesion(String username, String password, final ApiCallback callback) throws UnsupportedEncodingException, JSONException {
        String url = BASE_URL + "/login";

        // Construir el cuerpo de la solicitud
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);
        jsonBody.put("password", password);

        // Crear la solicitud POST
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> callback.onSuccess(response.toString()),
                error -> callback.onError(error.getMessage()));

        // Añadir la solicitud a la cola
        requestQueue.add(request);
    }


    // Interfaz de devolución de llamada para manejar respuestas de la API
    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String errorMessage);
    }
}

