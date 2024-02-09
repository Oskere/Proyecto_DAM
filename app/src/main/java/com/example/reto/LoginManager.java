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
    private static final String BASE_URL = "http://10.10.12.200:8080/api/usuarios"; // Reemplaza con tu dirección de la API

    private final RequestQueue requestQueue;

    private final String fixedSalt = "$2a$10$abcdefghijklmnopqrstuu";
    public LoginManager(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static boolean isValidEmail(String email) {
        // Definir el patrón de la expresión regular para validar el formato del correo electrónico
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }
    public void registrarUsuario(String username, String email, String password, final ApiCallback callback) throws JSONException {
        if (!isValidEmail(email)) {
            callback.onError("Formato de correo electrónico no válido");
            return;
        }

        String url = BASE_URL + "/registro";

        // Encriptar la contraseña antes de enviarla
        String hashedPassword = BCrypt.hashpw(password, fixedSalt);
        Log.d("Respuesta del servidor", hashedPassword);

        // Construir el cuerpo de la solicitud
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);
        jsonBody.put("email", email);
        jsonBody.put("password", hashedPassword);

        // Crear la solicitud POST
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    // Aquí puedes imprimir la respuesta para depurar
                    callback.onSuccess(response.toString(), email);
                },
                error -> callback.onError(error.getMessage())) {
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


    public void iniciarSesion(String username, String password, final ApiCallback callback) throws UnsupportedEncodingException, JSONException {
        String url = BASE_URL + "/login";

        String hashedPassword = BCrypt.hashpw(password, fixedSalt);
        Log.d("Respuesta del servidor", hashedPassword);

        // Construir el cuerpo de la solicitud
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);
        jsonBody.put("password", hashedPassword);
        Log.d("Login", "Hola");

        // Crear la solicitud POST con el encabezado "ApiKey"
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    // Aquí puedes imprimir la respuesta para depurar
                    try {
                        // Obtener el email desde la respuesta del servidor
                        String email = response.getString("email");
                        callback.onSuccess(response.toString(), email);
                    } catch (JSONException e) {
                        // Manejar el error si no se puede obtener el email
                        callback.onError("Error al obtener el email desde la respuesta");
                    }
                },
                error -> callback.onError(error.getMessage())) {
            @Override
            public byte[] getBody() {
                try {
                    // Imprimir el cuerpo de la solicitud antes de enviarla
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
                // Asegúrate de establecer correctamente el tipo de contenido del cuerpo de la solicitud
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                // Agregar el encabezado "ApiKey" al encabezado de la solicitud
                Map<String, String> headers = new HashMap<>();
                headers.put("ApiKey", API_KEY);
                return headers;
            }
        };

        // Añadir la solicitud a la cola
        Log.d("LoginRequest", "" + request);
        requestQueue.add(request);
    }



    // Interfaz de devolución de llamada para manejar respuestas de la API
    public interface ApiCallback {
        void onSuccess(String response, String email);
        void onError(String errorMessage);
    }
}

