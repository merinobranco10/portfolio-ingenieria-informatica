package com.ubo.apirest.modelo;

/**
 *
 * @author branc
 */

public class Producto {
    private int idProducto;
    private String nombre;
    private int idCategoria;
    private String tipoVenta;
    private double precioBase;
    private double stock;
    private String extra;
    
    // Constructor vacio, para crear objeto sin datos inicialmente
    public Producto(){
    }
    
    // Constructor
    public Producto(int idProducto, String nombreProducto, int idCategoria, String tipoVenta,
                    double precioBase, double stock, String extra) {
        this.idProducto = idProducto;
        this.nombre = nombreProducto;
        this.idCategoria = idCategoria;
        this.tipoVenta = tipoVenta;
        this.precioBase = precioBase;
        this.stock = stock;
        this.extra = extra;
    }

    // Getters & Setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int id) { this.idProducto = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    
    public String getTipoVenta() { return tipoVenta; }
    public void setTipoVenta(String tipoVenta) { this.tipoVenta = tipoVenta; }
    
    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }
    
    public double getStock() { return stock; }
    public void setStock(double stock) { this.stock = stock; }
    
    public String getExtra() { return extra; }
    public void setExtra(String extra) { this.extra = extra; }

}

