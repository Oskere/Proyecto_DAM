package com.example.reto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reto.Entidades.Camara;

import java.util.List;

public class cameraAdapter extends RecyclerView.Adapter<cameraAdapter.ViewHolder> {

    private List<Camara> cameraList;
    private LayoutInflater inflater;

    public cameraAdapter(Context context, List<Camara> cameraList) {
        this.inflater = LayoutInflater.from(context);
        this.cameraList = cameraList;
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nombreTextView;
        private final TextView kilometerTextView;
        private final ImageView imagenImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.TextViewFiltro1);
            kilometerTextView = itemView.findViewById(R.id.TextViewFiltro2);
            imagenImageView = itemView.findViewById(R.id.imagenImageView);
        }

        public void bind(Camara camera) {
            nombreTextView.setText(camera.getTitle());
            kilometerTextView.setText(camera.getKilometer());
            imagenImageView.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.exit));
        }
    }
}
