package com.himanshu.weather.graphql.queries;

import com.himanshu.weather.graphql.DTOs.City;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.bson.Document;

public class CityQuery {
    private final MongoClient mongoClient;

    public CityQuery(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public DataFetcher<City> getCoordinatesByCityFetcher() {
        return new DataFetcher<City>() {
            @Override
            public City get(DataFetchingEnvironment environment) throws Exception {
                String city = environment.getArgument("city");

                MongoDatabase db = mongoClient.getDatabase("weatherdb");
                MongoCollection<Document> collection = db.getCollection("cities");

                Document document = collection.find(new Document("name", city)).first();
                if (document == null) return null;
                return new City(
                        document.getString("name"),
                        document.getString("country"),
                        document.getString("region"),
                        document.getString("lat"),
                        document.getString("lon"),
                        document.getString("timezone_id"),
                        document.getString("utc_offset")
                );
            }
        };
    }
}
