package com.example.reto;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import org.mindrot.jbcrypt.BCrypt;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginManager {
    private static final String API_KEY = "DAMTraficoProyecto";
    private static final String BASE_URL = "http://10.10.12.200:8080/api/usuarios";

    private final RequestQueue requestQueue;

    private final String fixedSalt = "$2a$10$abcdefghijklmnopqrstuu";
    public LoginManager(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }
    public void registrarUsuario(String username, String email, String password, final ApiCallback callback) throws JSONException {
        if (!isValidEmail(email)) {
            callback.onError("Formato de correo electrónico no válido");
            return;
        }

        String url = BASE_URL + "/registro";

        String hashedPassword = BCrypt.hashpw(password, fixedSalt);
        Log.d("Respuesta del servidor", hashedPassword);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);
        jsonBody.put("email", email);
        jsonBody.put("password", hashedPassword);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    callback.onSuccess(response.toString(), email);
                },
                error -> callback.onError(error.getMessage())) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("ApiKey", API_KEY);
                return headers;
            }
        };

        requestQueue.add(request);
    }


    public void iniciarSesion(String username, String password, final ApiCallback callback) throws UnsupportedEncodingException, JSONException {
        String url = BASE_URL + "/login";

        String hashedPassword = BCrypt.hashpw(password, fixedSalt);
        Log.d("Respuesta del servidor", hashedPassword);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);
        jsonBody.put("password", hashedPassword);
        Log.d("Login", "Hola");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        String email = response.getString("email");
                        callback.onSuccess(response.toString(), email);
                    } catch (JSONException e) {
                        callback.onError("Error al obtener el email desde la respuesta");
                    }
                },
                error -> callback.onError(error.getMessage())) {
            @Override
            public byte[] getBody() {
                try {
                    String body = jsonBody.toString();
                    Log.d("Login", "Request Body: " + body);
                    return body.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e("Login", "Error al obtener el cuerpo de la solicitud: " + e.getMessage());
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("ApiKey", API_KEY);
                return headers;
            }
        };

        Log.d("LoginRequest", "" + request);
        requestQueue.add(request);
    }



    public interface ApiCallback {
        void onSuccess(String response, String email);
        void onError(String errorMessage);
    }
}

