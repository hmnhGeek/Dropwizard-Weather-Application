package com.himanshu.weather.DTOs.WeatherResponse;

import lombok.Data;

@Data
public class AirQualityDTO {
    private String co;
    private String no2;
    private String o3;
    private String so2;
    private String pm2_5;
    private String pm10;
    private String us_epa_index;
    private String gb_defra_index;
}