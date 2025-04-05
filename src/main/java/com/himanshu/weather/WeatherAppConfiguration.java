package com.himanshu.weather;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import lombok.Getter;
import lombok.Setter;

public class WeatherAppConfiguration extends Configuration {

    @Getter
    @JsonProperty
    private String apiKey;

    @Getter
    @JsonProperty
    private String database;

    @Getter
    @JsonProperty
    private String citiesCollection;

    @Setter
    @Getter
    @JsonProperty
    private String mongoUri;

    @Getter
    @JsonProperty
    private String apiBase;

    @Getter
    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swagger = new SwaggerBundleConfiguration();
}
