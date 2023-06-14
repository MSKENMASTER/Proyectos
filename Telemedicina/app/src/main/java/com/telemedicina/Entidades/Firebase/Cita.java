package com.telemedicina.Entidades.Firebase;

public class Cita {

    private String doctor;
    private String fecha;
    private String hora;
    private String especialidad;

    public Cita(String doctor, String fecha, String hora, String especialidad) {
        this.doctor = doctor;
        this.fecha = fecha;
        this.hora = hora;
        this.especialidad = especialidad;
    }

    public Cita() {
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
}
