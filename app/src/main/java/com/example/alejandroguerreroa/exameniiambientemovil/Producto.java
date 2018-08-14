package com.example.alejandroguerreroa.exameniiambientemovil;

public class Producto {

    public int id;
    public String nombre;
    public int precioVenta;

    public Producto() {
    }

    public Producto(String nombre, int precioVenta) {
        this.nombre = nombre;
        this.precioVenta = precioVenta;
    }
}
