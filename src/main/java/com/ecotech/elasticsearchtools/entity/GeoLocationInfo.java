package com.ecotech.elasticsearchtools.entity;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeoLocationInfo {
    // {"location":{"type":"point","coordinates":[115.903288,39.579271]}},"name":"房山区西长沟村"}
    private Location location;
    private String name;
    private String pinyin;
    private String pinyinShort;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    // public static void main(String[] args) throws JsonParseException, JsonMappingException,
    // IOException {
    // String
    // jsonString="{\"location\":{\"type\":\"point\",\"coordinates\":[115.903288,39.579271]},\"name\":\"房山区西长沟村\"}";
    // ObjectMapper objectMapper=new ObjectMapper();
    // GeoLocationInfo geoLocationInfo=objectMapper.readValue(jsonString, GeoLocationInfo.class);
    // System.out.println();
    // }
}
