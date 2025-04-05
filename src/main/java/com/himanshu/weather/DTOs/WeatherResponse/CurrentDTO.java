package com.himanshu.weather.DTOs.WeatherResponse;

import lombok.Data;

import java.util.List;

@Data
public class CurrentDTO {
    private String observation_time;
    private int temperature;
    private int weather_code;
    private List<String> weather_icons;
    private List<String> weather_descriptions;
    private AstroDTO astro;
    private AirQualityDTO air_quality;
    private int wind_speed;
    private int wind_degree;
    private String wind_dir;
    private int pressure;
    private int precip;
    private int humidity;
    private int cloudcover;
    private int feelslike;
    private int uv_index;
    private int visibility;
}