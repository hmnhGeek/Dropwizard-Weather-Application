package com.himanshu.weather.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/weather")
@Produces(MediaType.APPLICATION_JSON)
public class WeatherResource {
    private final String apiKey;
    private final Client client;

    public WeatherResource(String apiKey) {
        this.apiKey = apiKey;
        this.client = ClientBuilder.newClient(); // Initialize Client once
    }

    @GET
    @Path("/{city}")
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
