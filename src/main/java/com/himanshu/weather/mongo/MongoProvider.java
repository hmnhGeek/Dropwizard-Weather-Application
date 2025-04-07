package com.himanshu.weather.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoProvider {
    private final MongoClient mongoClient;

    public MongoProvider(String uri) {
        this.mongoClient = MongoClients.create(uri);
    }

    public MongoClient getClient() {
        return mongoClient;
    }
}
