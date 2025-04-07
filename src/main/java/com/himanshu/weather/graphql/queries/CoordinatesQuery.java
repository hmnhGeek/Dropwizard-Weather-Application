package com.himanshu.weather.graphql.queries;

import com.himanshu.weather.graphql.DTOs.CoordinatesDTO;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.bson.Document;

public class CoordinatesQuery {
    private final MongoClient mongoClient;

    public CoordinatesQuery(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public DataFetcher<CoordinatesDTO> getCoordinatesByCityFetcher() {
        return new DataFetcher<CoordinatesDTO>() {
            @Override
            public CoordinatesDTO get(DataFetchingEnvironment environment) throws Exception {
                String city = environment.getArgument("city");

                MongoDatabase db = mongoClient.getDatabase("weatherdb");
                MongoCollection<Document> collection = db.getCollection("cities");

                Document document = collection.find(new Document("name", city)).first();
                if (document == null) return null;
                return new CoordinatesDTO(document.getString("lat"), document.getString("lon"));
            }
        };
    }
}
