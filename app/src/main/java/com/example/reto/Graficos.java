package com.example.reto;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
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

        pieChart = findViewById(R.id.pieChart);

        tituloGrafico = findViewById(R.id.tituloGrafico);
        TextInputLayout textInputLayout = findViewById(R.id.spinnerLocation);
        autoCompleteTextView = textInputLayout.findViewById(R.id.autoCompleteText);

        requestQueue = Volley.newRequestQueue(this);

        String[] yearArray = getResources().getStringArray(R.array.year_array);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedYearString = autoCompleteTextView.getText().toString();
            pieChart.clear();
            for (int month = 1; month <= 12; month++) {
                makeApiRequest(selectedYearString, month);
            }
        });

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void makeApiRequest(final String year, final int month) {
        String url = baseUrl + year + "/" + month;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int totalItems = response.getInt("totalItems");
                            updatePieChart(totalItems, month);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Graficos.this, "Error al procesar la respuesta JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("GRAFICOSSS", error.toString());
                        Toast.makeText(Graficos.this, "Error de solicitud", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void updatePieChart(int totalItems, int month) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        PieData pieData;

        if (pieChart.getData() != null && pieChart.getData().getDataSetCount() > 0) {
            pieData = pieChart.getData();
            PieDataSet dataSet = (PieDataSet) pieData.getDataSetByIndex(0);
            pieEntries = new ArrayList<>(dataSet.getValues());
        }

        pieEntries.add(new PieEntry(totalItems, getMonthName(month)));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(255, 102, 0));
        colors.add(Color.rgb(255, 204, 51));
        colors.add(Color.rgb(125, 255, 51));
        colors.add(Color.rgb(51, 255, 51));
        colors.add(Color.rgb(0, 102, 204));
        colors.add(Color.rgb(51, 51, 255));
        colors.add(Color.rgb(153, 51, 255));
        colors.add(Color.rgb(255, 51, 204));
        colors.add(Color.rgb(102, 0, 102));
        colors.add(Color.rgb(255, 0, 0));
        colors.add(Color.rgb(0, 204, 204));
        colors.add(Color.rgb(255, 204, 0));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Grafico de Incidencias del año " + autoCompleteTextView.getText().toString());
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setColors(colors);
        pieChart.setDrawSliceText(false);
        pieChart.setDescription(null);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.texto));
        pieData = new PieData(pieDataSet);
        pieChart.setDrawEntryLabels(false);
        pieChart.setData(pieData);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pieEntry = (PieEntry) e;

                Toast.makeText(Graficos.this, pieEntry.getLabel(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });
    }

    private String getMonthName(int month) {
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
