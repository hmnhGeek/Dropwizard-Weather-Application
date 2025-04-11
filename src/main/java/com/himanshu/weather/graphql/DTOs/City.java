package com.himanshu.weather.graphql.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    private String name;
    private String country;
    private String region;
    private String lat;
    private String lon;
    private String timezoneId;
    private String utcOffset;
}