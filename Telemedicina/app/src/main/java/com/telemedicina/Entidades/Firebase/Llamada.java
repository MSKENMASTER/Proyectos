package com.telemedicina.Entidades.Firebase;

public class Llamada {

    private String tlf;
    private String tipo;
    private String fecha;

    public Llamada(String tlf, String tipo, String fecha) {
        this.tlf = tlf;
        this.tipo = tipo;
        this.fecha = fecha;
    }

    public Llamada() {
    }

    public String getTlf() {
        return tlf;
    }

    public void setTlf(String tlf) {
        this.tlf = tlf;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
