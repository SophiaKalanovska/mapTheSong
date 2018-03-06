package com.example.sophia.mapthesong;

/**
 * Created by sophia on 6.03.18.
 */

public class City {


    private Double longitude;
    private Double latitude;
    private int numberOfPeopleListening;

    public  City(Double longitude, Double latitude, int numberOfPeopleListening) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.numberOfPeopleListening = numberOfPeopleListening;
    }

    public Double getLng() {
        return longitude;
    }
    public int getPeopleListnening() {return numberOfPeopleListening;}



    public Double getLat() {
        return latitude;
    }

}
