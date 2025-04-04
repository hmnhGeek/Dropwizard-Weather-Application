package com.himanshu.weather;

import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class SwaggerBundleConfig extends SwaggerBundle<WeatherAppConfiguration> {
    @Override
    protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(WeatherAppConfiguration configuration) {
        return configuration.getSwagger(); // Load Swagger config from YAML
    }
}