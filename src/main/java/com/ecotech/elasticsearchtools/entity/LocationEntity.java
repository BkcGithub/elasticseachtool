package com.ecotech.elasticsearchtools.entity;

import java.math.BigDecimal;

public class LocationEntity {
    private BigDecimal lat;
    private BigDecimal lon;

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

}
