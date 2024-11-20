package manusha.mas.model;

import java.sql.Date;

public class Bus {

    private int busId;
    private String busStatus;
    private String busLocation;
    private Date busDate;
    private double busPrice;

    private int busSeats;

    public Bus(int busId, String busStatus, String busLocation, Date busDate, double busPrice , int busSeats) {
        this.busId = busId;
        this.busStatus = busStatus;
        this.busLocation = busLocation;
        this.busDate = busDate;
        this.busPrice = busPrice;
        this.busSeats = busSeats;

    }
    public int getBusId() {
        return busId;
    }
    public void setBusId(int busId) {
        this.busId = busId;
    }
    public String getBusStatus() {
        return busStatus;
    }
    public void setBusType(String busType) {
        this.busStatus = busType;
    }
    public String getBusLocation() {
        return busLocation;
    }
    public void setBusLocation(String busLocation) {
        this.busLocation = busLocation;
    }
    public Date getBusDate() {
        return busDate;
    }

    public int getBusSeats() {
        return busSeats;
    }
    public void setBusSeats(int totalSeats) {
        this.busSeats = totalSeats;
    }



    public void setBusDate(Date busDate) {
        this.busDate = busDate;
    }
    public double getBusPrice() {
        return busPrice;
    }

    public void setBusPrice(double busPrice) {
        this.busPrice = busPrice;
    }

}
