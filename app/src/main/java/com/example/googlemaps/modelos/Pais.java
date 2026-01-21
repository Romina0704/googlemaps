package com.example.googlemaps.modelos;

public class Pais {
    private String nombre;
    private String url;
    private String cca2;

    public Pais(String nombre, String url, String cca2) {
        this.nombre = nombre;
        this.url = url;
        this.cca2 = cca2;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUrl() {
        return url;
    }

    public String getCca2() {
        return cca2;
    }
}
