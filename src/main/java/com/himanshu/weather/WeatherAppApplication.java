package com.himanshu.weather;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import com.himanshu.weather.resources.WeatherResource;

public class WeatherAppApplication extends Application<WeatherAppConfiguration> {
    public static void main(String[] args) throws Exception {
        new WeatherAppApplication().run(args);
    }

    @Override
    public void run(WeatherAppConfiguration config, Environment environment) {
        environment.jersey().register(new WeatherResource(config.getApiKey()));
    }
}
