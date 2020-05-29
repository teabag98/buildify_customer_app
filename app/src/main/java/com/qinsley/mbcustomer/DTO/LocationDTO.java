package com.qinsley.mbcustomer.DTO;

import java.io.Serializable;

public class LocationDTO implements Serializable {

   public String latitude ="";
   public String longitude= "";

    public String getLati() {
        return latitude;
    }

    public void setLati(String latitude) {
        this.latitude = latitude;
    }

    public String getLongi() {
        return longitude;
    }

    public void setLongi(String longitude) {
        this.longitude = longitude;
    }
}
