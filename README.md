# Swagger Integration with Dropwizard

This README provides a detailed guide on integrating Swagger with Dropwizard, ensuring compatibility with different versions, and resolving configuration issues.

## Prerequisites

- Java 8 or higher
- Maven 3+
- Dropwizard 2.1.5
- `dropwizard-swagger` version 2.0.0-1

## Dependencies

Add the following dependencies to your `pom.xml` file:

```xml
<dependencies>
    <dependency>
        <groupId>io.dropwizard</groupId>
        <artifactId>dropwizard-core</artifactId>
        <version>2.1.5</version>
    </dependency>
    
    <dependency>
        <groupId>com.smoketurner</groupId>
        <artifactId>dropwizard-swagger</artifactId>
        <version>2.0.0-1</version>
    </dependency>
</dependencies>
```

## Configuration

Create a `config.yml` file with the following content:

```yaml
apiKey: ""
swagger:
  resourcePackage: "com.himanshu.weather.resources"
  title: "Weather API"
  description: "Weather API Documentation"
  version: "1.0.0"
  schemes:
    - "http"
  host: "localhost:8080"
  contact: "Himanshu (himanshu@example.com)"  # Ensure contact is a string
```

### Important Fix: `contact` Must Be a String

Initially, the `contact` field was written as an object with `name` and `email` fields:

```yaml
contact:
  name: "Himanshu"
  email: "himanshu@example.com"
```

This caused an error:

```
io.dropwizard.configuration.ConfigurationParsingException: Failed to parse configuration at: swagger.contact; Cannot deserialize value of type `java.lang.String` from Object value
```

To fix this, we changed `contact` to a single string:

```yaml
contact: "Himanshu (himanshu@example.com)"
```

## Implementation

### 1. Create the Configuration Class

```java
package com.himanshu.weather;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class WeatherAppConfiguration extends Configuration {

    @JsonProperty
    private String apiKey;

    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swagger = new SwaggerBundleConfiguration();

    public String getApiKey() {
        return apiKey;
    }

    public SwaggerBundleConfiguration getSwagger() {
        return swagger;
    }
}
```

### 2. Create the Application Class

```java
package com.himanshu.weather;

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
        environment.jersey().register(new WeatherResource(config.getApiKey()));
    }
}
```

### 3. Create the Weather Resource Class

```java
package com.himanshu.weather.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/weather")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "WeatherStack APIs", description = "API to fetch data from weatherstack.")
public class WeatherResource {
    private final String apiKey;
    private final Client client;

    public WeatherResource(String apiKey) {
        this.apiKey = apiKey;
        this.client = ClientBuilder.newClient(); // Initialize Client once
    }

    @GET
    @Path("/{city}")
    @ApiOperation(value = "Get the weather of a city.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "City not found")
    })
    public Response getWeather(@PathParam("city") String city) {
        try {
            WebTarget target = client.target("https://api.weatherstack.com/current")
                    .queryParam("query", city)
                    .queryParam("access_key", apiKey);

            String result = target.request().get(String.class);
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Unable to fetch weather data\"}")
                    .build();
        }
    }
}
```

## Running the Application

1. Build the project:

   ```sh
   mvn clean package
   ```

2. Run the application with the configuration file:

   ```sh
   java -jar target/WeatherApp-1.0-SNAPSHOT.jar server config.yml
   ```

3. Open the Swagger UI:

    - Navigate to: [http://localhost:8080/swagger](http://localhost:8080/swagger)

## Conclusion

- Dropwizard 2.1.5 is compatible with `dropwizard-swagger` 2.0.0-1.
- The `contact` field in `config.yml` should be a string to avoid deserialization errors.
- The API fetches weather data from WeatherStack and exposes it via a RESTful endpoint.

This setup provides a fully functional Dropwizard application with Swagger documentation integrated successfully. 🚀

