package com.jm.paintballsevilla.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Users implements Parcelable {
    private String id;
    private String email;
    private String name;
    private String last_name;
    private int phone;
    private boolean master;
    private Map<String, Boolean> fav;

    public Users() {
    }

    /**
     * Constructor sin el master
     *
     * @param email
     * @param name
     * @param last_name
     * @param phone
     */
    public Users(String email, String name, String last_name, int phone)
    {
        this.email = email;
        this.name = name;
        this.last_name = last_name;
        this.phone = phone;
        this.master = false;
        this.fav = new HashMap<>();
    }

    /**
     * Constructor con el master
     *
     * @param email
     * @param name
     * @param last_name
     * @param phone
     */
    public Users(String email, String name, String last_name, int phone, Boolean master)
    {
        this.email = email;
        this.name = name;
        this.last_name = last_name;
        this.phone = phone;
        this.master = master;
        this.fav = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public Map<String, Boolean> getFav() {
        return fav;
    }

    public void setFav(Map<String, Boolean> fav) {
        this.fav = fav;
    }

    protected Users(Parcel in) {
        id = in.readString();
        email = in.readString();
        name = in.readString();
        last_name = in.readString();
        phone = in.readInt();
        master = in.readByte() != 0;
        fav = new HashMap<>();
        // Lee el tamaño del mapa
        int favSize = in.readInt();
        // Aquí se usa un bucle para leer cada objeto del mapa y almacenarlo dentro de fav
        for (int i = 0; i < favSize; i++) {
            String key = in.readString();
            Boolean value = in.readByte() != 0;
            fav.put(key, value);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(last_name);
        dest.writeInt(phone);
        // Esta línea define el tamaño del mapa
        dest.writeByte((byte) (master ? 1 : 0));
        if (fav != null) {
            dest.writeInt(fav.size());
            // Este bucle mete cada Actividad(en este caso) dentro de fav
            for (Map.Entry<String, Boolean> entry : fav.entrySet()) {
                dest.writeString(entry.getKey());
                dest.writeByte((byte) (entry.getValue() ? 1 : 0));
            }
        } else {
            dest.writeInt(0);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    @Override
    public String toString() {
        return "Users{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", phone=" + phone +
                ", master=" + master +
                ", fav=" + fav +
                '}';
    }
}
