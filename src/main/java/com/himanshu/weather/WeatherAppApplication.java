package com.himanshu.weather;

import com.himanshu.weather.mongo.MongoProvider;
import com.mongodb.client.MongoClient;
import io.dropwizard.Application;
import com.himanshu.weather.resources.WeatherResource;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class WeatherAppApplication extends Application<WeatherAppConfiguration> {
    public static void main(String[] args) throws Exception {
        new WeatherAppApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<WeatherAppConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<WeatherAppConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(WeatherAppConfiguration configuration) {
                return configuration.getSwagger();
            }
        });
    }

    @Override
    public void run(WeatherAppConfiguration config, Environment environment) {
        MongoProvider mongoProvider = new MongoProvider(config.getMongoUri());
        MongoClient mongoClient = mongoProvider.getClient();
        environment.jersey().register(new WeatherResource(config.getApiKey(), environment.getObjectMapper(), mongoClient, config.getDatabase(), config.getCitiesCollection(), config.getApiBase()));
    }
}
