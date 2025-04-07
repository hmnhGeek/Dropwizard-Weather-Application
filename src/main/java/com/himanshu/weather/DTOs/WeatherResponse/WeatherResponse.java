package com.himanshu.weather.DTOs.WeatherResponse;

import lombok.Data;

@Data
public class WeatherResponse {
    private RequestDTO request;
    private LocationDTO location;
    private CurrentDTO current;
}
