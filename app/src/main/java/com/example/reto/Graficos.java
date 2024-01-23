package com.example.reto;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Graficos extends AppCompatActivity {

    private PieChart pieChart;
    private RequestQueue requestQueue;
    private TextView tituloGrafico;
    private AutoCompleteTextView autoCompleteTextView;
    private String baseUrl = "https://api.euskadi.eus/traffic/v1.0/incidences/byMonth/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layoutgraficos);

        // Configuración del gráfico de queso (PieChart)
        pieChart = findViewById(R.id.pieChart);

        tituloGrafico = findViewById(R.id.tituloGrafico);
        TextInputLayout textInputLayout = findViewById(R.id.spinnerLocation);
        autoCompleteTextView = textInputLayout.findViewById(R.id.autoCompleteText);

        // Inicializar la cola de solicitudes de Volley
        requestQueue = Volley.newRequestQueue(this);

        // Obtener el array de años desde los recursos
        String[] yearArray = getResources().getStringArray(R.array.year_array);

        // Crear un ArrayAdapter usando el array de años y un diseño predeterminado del sistema
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearArray);

        // Establecer el diseño para las opciones del AutoCompleteTextView
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Establecer el adaptador en el AutoCompleteTextView
        autoCompleteTextView.setAdapter(adapter);

        // Agregar un listener al AutoCompleteTextView para detectar cambios en la selección del año
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            // Obtener el año seleccionado
            String selectedYearString = autoCompleteTextView.getText().toString();

            // Realizar llamada a la API para el año seleccionado
            makeApiRequest(selectedYearString);
        });

        // Realizar llamadas a la API para cada mes (meses del 1 al 12) con el año predeterminado
        for (int month = 1; month <= 12; month++) {
            makeApiRequest(yearArray[0], month);
        }
    }

    private void makeApiRequest(final String year) {
        // Construir la URL de la API para el año actual
        String url = baseUrl + year;

        // Realizar la solicitud JSON a la API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Obtener el valor de "totalItems" directamente
                            int totalItems = response.getInt("totalItems");

                            // Actualizar el gráfico de queso con el número total de items y el nombre del mes
                            updatePieChart(totalItems);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Manejar error al procesar la respuesta JSON
                            Toast.makeText(Graficos.this, "Error al procesar la respuesta JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar error de la solicitud
                        Log.d("GRAFICOSSS", error.toString());
                        Toast.makeText(Graficos.this, "Error de solicitud", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Agregar la solicitud a la cola
        requestQueue.add(jsonObjectRequest);
    }

    private void makeApiRequest(final String year, final int month) {
        // Construir la URL de la API para el año y mes actual
        String url = baseUrl + year + "/" + month;

        // Realizar la solicitud JSON a la API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Obtener el valor de "totalItems" directamente
                            int totalItems = response.getInt("totalItems");

                            // Actualizar el gráfico de queso con el número total de items y el nombre del mes
                            updatePieChart(totalItems);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Manejar error al procesar la respuesta JSON
                            Toast.makeText(Graficos.this, "Error al procesar la respuesta JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar error de la solicitud
                        Log.d("GRAFICOSSS", error.toString());
                        Toast.makeText(Graficos.this, "Error de solicitud", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Agregar la solicitud a la cola
        requestQueue.add(jsonObjectRequest);
    }

    private void updatePieChart(int totalItems) {
        // Crear o actualizar la lista de entradas para el gráfico de queso
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        PieData pieData;

        // Obtener datos existentes del gráfico
        if (pieChart.getData() != null && pieChart.getData().getDataSetCount() > 0) {
            pieData = pieChart.getData();
            PieDataSet dataSet = (PieDataSet) pieData.getDataSetByIndex(0);
            pieEntries = new ArrayList<>(dataSet.getValues());
        }

        // Agregar nueva entrada para el año actual
        pieEntries.add(new PieEntry(totalItems, "Total"));

        ArrayList<Integer> colors = new ArrayList<>();
        // ... (colores según tus preferencias)

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Grafico de Incidencias del año " + autoCompleteTextView.getText().toString());
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setColors(colors);  // Asignar colores a las entradas
        pieChart.setDrawSliceText(false);
        pieChart.setDescription(null);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.texto)); // Color del texto
        pieData = new PieData(pieDataSet);
        pieChart.setDrawEntryLabels(false);
        pieChart.setData(pieData);
        pieChart.getLegend().setEnabled(false);  // Desactivar la leyenda
        pieChart.invalidate();
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // Aquí puedes obtener información sobre la Slice seleccionada
                PieEntry pieEntry = (PieEntry) e;

                // Muestra el texto de la Slice seleccionada (puedes usar un Toast u otra interfaz de usuario)
                Toast.makeText(Graficos.this, pieEntry.getLabel(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
                // Acciones cuando no se selecciona ninguna Slice
            }
        });
    }
    private String getMonthName(int month) {
        // Puedes personalizar la obtención del nombre del mes según tus necesidades
        switch (month) {
            case 1: return "Enero";
            case 2: return "Febrero";
            case 3: return "Marzo";
            case 4: return "Abril";
            case 5: return "Mayo";
            case 6: return "Junio";
            case 7: return "Julio";
            case 8: return "Agosto";
            case 9: return "Septiembre";
            case 10: return "Octubre";
            case 11: return "Noviembre";
            case 12: return "Diciembre";
            default: return "";
        }
    }
}
