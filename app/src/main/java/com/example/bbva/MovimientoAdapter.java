package com.example.bbva;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MovimientoAdapter extends RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder> {

    private List<Movimiento> movimientoList;

    public MovimientoAdapter(List<Movimiento> movimientoList) {
        this.movimientoList = movimientoList;
    }

    @NonNull
    @Override
    public MovimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movimiento, parent, false);
        return new MovimientoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovimientoViewHolder holder, int position) {
        Movimiento movimiento = movimientoList.get(position);
        holder.fechaTextView.setText(movimiento.getFecha());
        holder.descripcionTextView.setText(movimiento.getDescripcion());
        holder.montoTextView.setText(movimiento.getMonto());
    }

    @Override
    public int getItemCount() {
        return movimientoList.size();
    }

    public static class MovimientoViewHolder extends RecyclerView.ViewHolder {

        TextView fechaTextView, descripcionTextView, montoTextView;

        public MovimientoViewHolder(@NonNull View itemView) {
            super(itemView);
            fechaTextView = itemView.findViewById(R.id.fechaTextView);
            descripcionTextView = itemView.findViewById(R.id.descripcionTextView);
            montoTextView = itemView.findViewById(R.id.montoTextView);
        }
    }
}
