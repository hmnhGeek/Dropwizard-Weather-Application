package com.himanshu.weather;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class WeatherAppConfiguration extends Configuration {

    @JsonProperty
    private String apiKey;

    @JsonProperty
    private String mongoUri;

    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swagger = new SwaggerBundleConfiguration();

    public String getApiKey() {
        return apiKey;
    }

    public SwaggerBundleConfiguration getSwagger() {
        return swagger;
    }

    public String getMongoUri() {
        return mongoUri;
    }

    public void setMongoUri(String mongoUri) {
        this.mongoUri = mongoUri;
    }

}
