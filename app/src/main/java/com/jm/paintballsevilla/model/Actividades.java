package com.jm.paintballsevilla.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Actividades  implements Parcelable{
    private String id;
    private String nombre;
    private String zona;
    private String fecha;
    private int plazas;
    private String descripcion;

    public Actividades() {
    }

    public Actividades(String id, String nombre, String zona, String fecha, int plazas, String descripcion) {
        this.descripcion =descripcion;
        this.fecha = fecha;
        this.id = id;
        this.nombre = nombre;
        this.plazas = plazas;
        this.zona = zona;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getPlazas() {
        return plazas;
    }

    public void setPlazas(int plazas) {
        this.plazas = plazas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Actividades{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", zona='" + zona + '\'' +
                ", fecha='" + fecha + '\'' +
                ", plazas=" + plazas +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    // Para hacer parceable a ls sctividad.
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeString(zona);
        dest.writeString(descripcion);
        dest.writeString(fecha);
        dest.writeInt(plazas);
    }

    // para crear un objeto parceable
    protected Actividades(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        zona = in.readString();
        fecha = in.readString();
        descripcion = in.readString();
        plazas = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Actividades> CREATOR = new Creator<Actividades>() {
        @Override
        public Actividades createFromParcel(Parcel in) {
            return new Actividades(in);
        }

        @Override
        public Actividades[] newArray(int size) {
            return new Actividades[size];
        }
    };
}
