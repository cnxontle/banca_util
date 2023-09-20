package com.example.bbva;

public class Movimiento {
    private String fecha;
    private String descripcion;
    private String monto;

    public Movimiento(String fecha, String descripcion, String monto) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.monto = monto;
    }

    public String getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getMonto() {
        return monto;
    }
}
