package com.himanshu.weather.DTOs.WeatherResponse;

import lombok.Data;

@Data
public class AstroDTO {
    private String sunrise;
    private String sunset;
    private String moonrise;
    private String moonset;
    private String moon_phase;
    private int moon_illumination;
}
