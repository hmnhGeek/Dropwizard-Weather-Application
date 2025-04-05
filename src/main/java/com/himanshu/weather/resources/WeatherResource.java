package com.himanshu.weather.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.himanshu.weather.DTOs.WeatherResponse.LocationDTO;
import com.himanshu.weather.DTOs.WeatherResponse.WeatherResponse;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.bson.Document;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/weather")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "WeatherStack APIs", description = "API to fetch data from weatherstack.")
public class WeatherResource {
    private final String apiKey;
    private final Client client;
    private final ObjectMapper mapper;
    private final MongoClient mongoClient;

    public WeatherResource(String apiKey, ObjectMapper mapper, MongoClient mongoClient) {
        this.apiKey = apiKey;
        this.client = ClientBuilder.newClient();
        this.mapper = mapper;
        this.mongoClient = mongoClient;
    }

    @GET
    @Path("/{city}")
    @ApiOperation(value = "Get the weather of a city.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of weather data"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "City not found")
    })
    public Response getWeather(@PathParam("city") String city) {
        try {
            WebTarget target = client.target("https://api.weatherstack.com/current")
                    .queryParam("query", city)
                    .queryParam("access_key", apiKey);

            String result = target.request().get(String.class);
            WeatherResponse weatherResponse = mapper.readValue(result, WeatherResponse.class);

            MongoDatabase database = mongoClient.getDatabase("weatherdb");
            MongoCollection<Document> collection = database.getCollection("cities");
            LocationDTO location = weatherResponse.getLocation();
            Map<String, Object> locationMap = mapper.convertValue(location, Map.class);
            Document doc = new Document(locationMap);
            collection.insertOne(doc);
            return Response.ok(weatherResponse).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Unable to fetch weather data\"}")
                    .build();
        }
    }
}
