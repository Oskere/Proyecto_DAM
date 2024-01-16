package com.example.reto;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class Graficos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layoutgraficos);

        // Configuración del gráfico de queso (PieChart)
        PieChart pieChart = findViewById(R.id.pieChart);
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(30f, "A"));
        pieEntries.add(new PieEntry(20f, "B"));
        pieEntries.add(new PieEntry(50f, "C"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Ejemplo de Gráfico de Queso");
        pieDataSet.setColors(new int[]{Color.RED, Color.GREEN, Color.BLUE}); // Colores específicos
        pieDataSet.setValueTextColor(getResources().getColor(R.color.texto)); // Color del texto
        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.invalidate();

        // Configuración del gráfico de barras (BarChart)
        BarChart barChart = findViewById(R.id.barChart);
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 20));
        barEntries.add(new BarEntry(1, 35));
        barEntries.add(new BarEntry(2, 15));
        barEntries.add(new BarEntry(3, 5));
        barEntries.add(new BarEntry(4, 45));
        barEntries.add(new BarEntry(5, 86));
        barEntries.add(new BarEntry(6, 260));
        // ... agregar más datos según sea necesario

        BarDataSet barDataSet = new BarDataSet(barEntries, "Tráfico Diario");
        barDataSet.setColor(getResources().getColor(R.color.rojo)); // Color específico para todas las barras
        barDataSet.setValueTextColor(getResources().getColor(R.color.texto)); // Color del texto
        BarData barData = new BarData(barDataSet);

        // Configurar etiquetas para el eje X (días de la semana)
        String[] daysOfWeek = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        barChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(daysOfWeek));

        barChart.setData(barData);
        barChart.invalidate();

        // Agregar un Listener para manejar clics en las barras
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // Muestra información adicional al hacer clic en una barra
                float value = e.getY();
                int dayIndex = (int) e.getX();
                String dayOfWeek = daysOfWeek[dayIndex];
                String message = "Tráfico el " + dayOfWeek + ": " + value;
                Toast.makeText(Graficos.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
                // Se llama cuando no se ha seleccionado ninguna barra
            }
        });
    }
}