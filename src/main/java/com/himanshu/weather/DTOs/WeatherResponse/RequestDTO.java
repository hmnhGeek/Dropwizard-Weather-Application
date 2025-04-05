package com.himanshu.weather.DTOs.WeatherResponse;

import lombok.Data;

@Data
public class RequestDTO {
    private String type;
    private String query;
    private String language;
    private String unit;
}