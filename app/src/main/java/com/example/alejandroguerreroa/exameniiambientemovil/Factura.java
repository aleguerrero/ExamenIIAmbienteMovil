package com.example.alejandroguerreroa.exameniiambientemovil;

import java.util.Date;

public class Factura {
     //Numero, Cliente, Producto, Cantidad, Fecha
    String cliente, producto;
    int cantidad, numero;
    Date fecha;

    public Factura() {
    }

    public Factura(int numero, String cliente, String producto, int cantidad, Date fecha) {
        this.numero = numero;
        this.cliente = cliente;
        this.producto = producto;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
