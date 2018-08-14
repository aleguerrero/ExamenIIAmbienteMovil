package com.example.alejandroguerreroa.exameniiambientemovil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class Factura {
     //Numero, Cliente, Producto, Cantidad, Fecha
    public String cliente, producto;
    public int cantidad, numero;
    public String fecha;
    public ArrayList<Factura> productos;

    public Factura() {
    }

    public Factura(String cliente, String fecha, ArrayList<Factura> productos) {
        this.cliente = cliente;
        this.fecha = fecha;
        this.productos = productos;
    }

    public Factura(String producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }
}
