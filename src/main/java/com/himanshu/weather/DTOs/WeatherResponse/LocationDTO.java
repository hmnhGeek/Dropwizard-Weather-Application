package com.himanshu.weather.DTOs.WeatherResponse;

import lombok.Data;

@Data
public class LocationDTO {
    private String name;
    private String country;
    private String region;
    private String lat;
    private String lon;
    private String timezone_id;
    private String localtime;
    private long localtime_epoch;
    private String utc_offset;
}