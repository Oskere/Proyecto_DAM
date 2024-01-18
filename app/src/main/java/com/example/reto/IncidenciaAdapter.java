package com.example.reto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reto.Entidades.Incidencia;

import java.util.List;

public class IncidenciaAdapter extends RecyclerView.Adapter<IncidenciaAdapter.ViewHolder> {

    private List<Incidencia> incidenceList;
    private LayoutInflater inflater;

    public IncidenciaAdapter(Context context, List<Incidencia> incidenceList) {
        this.inflater = LayoutInflater.from(context);
        this.incidenceList = incidenceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_filtro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Incidencia incidence = incidenceList.get(position);
        holder.bind(incidence);
    }

    @Override
    public int getItemCount() {
        return incidenceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nombreTextView;
        private final TextView kilometerTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.TextViewFiltro1);
            kilometerTextView = itemView.findViewById(R.id.TextViewFiltro2);
        }

        public void bind(Incidencia incidence) {
            nombreTextView.setText(incidence.getTitle());
            kilometerTextView.setText(incidence.getRoad());

        }
    }
}
