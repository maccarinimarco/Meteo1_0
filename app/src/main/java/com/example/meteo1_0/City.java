package com.example.meteo1_0;

public class City {
    private String name="";
    private String capol="";
    private double lat;
    private double lon;
    private String icon;
    private String descrizione;
    private double temp;
    private  double min;
    private double max;

    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getIcon() {
        return icon;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getLat() {
        return lat;
    }
    public double getLon() {
        return lon;
    }
    public String getCapol() {
        return capol;
    }
    public String getName() {
        return name;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public double getTemp() {
        return temp;
    }
    public double getMin() {
        return min;
    }
    public double getMax() {
        return max;
    }


    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", capol='" + capol + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", icon='" + icon + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", temp=" + temp +
                ", min=" + min +
                ", max=" + max +
                '}';
    }

    public City(String name, String capol, String descrizione, double temp, double min, double max, String icon) {

        this.name = (name.equals("")) ? "NO" : name;
        this.capol = (capol.equals("")) ? "Unknow name" : capol;
        this.descrizione = descrizione;
        this.temp = temp;
        this.min = min;
        this.max = max;
        this.icon = icon;
    }

}
