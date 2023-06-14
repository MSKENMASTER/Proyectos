package com.telemedicina.Entidades.Firebase;

public class Doctor {

    private String FotoPerfil;
    private String Nombre;
    private String Correo;
    private String Telefono;
    private  String DNI;

    private String Especialidad;

    public Doctor() {
    }

    public Doctor(String fotoperfil, String nombre, String email, String telefono, String especialidad, String DNI) {
        this.FotoPerfil = fotoperfil;
        this.Nombre = nombre;
        this.Correo = email;
        this.Telefono = telefono;
        this.Especialidad =especialidad;
        this.DNI = DNI;
    }

    public String getFotoPerfil() {
        return FotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.FotoPerfil = fotoPerfil;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        this.Correo = correo;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        this.Telefono = telefono;
    }

    public String getEspecialidad() {
        return Especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.Especialidad = especialidad;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }
}
