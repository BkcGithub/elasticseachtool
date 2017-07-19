package com.ecotech.elasticsearchtools.document;

import java.math.BigDecimal;

public class POIDocument {
    private Point location;
    private String name;
    private String pinyin;
    private String pinyinshort;

    public class Point {
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

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyinshort() {
        return pinyinshort;
    }

    public void setPinyinshort(String pinyinshort) {
        this.pinyinshort = pinyinshort;
    }
}
