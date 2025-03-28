package com.himanshu.weather;

import io.dropwizard.core.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherAppConfiguration extends Configuration {
    @JsonProperty
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}
