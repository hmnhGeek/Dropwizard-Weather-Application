package com.himanshu.weather;

import com.himanshu.weather.graphql.queries.CityQuery;
import com.mongodb.client.MongoClient;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.io.InputStreamReader;

public class GraphQLFactory {
    public static GraphQL buildGraphQL(MongoClient mongoClient) {
        InputStreamReader reader = new InputStreamReader(GraphQLFactory.class.getResourceAsStream("/graphql/schema.graphqls"));
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(reader);
        CityQuery queryResolver = new CityQuery(mongoClient);
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("getCityInformation", queryResolver.getCoordinatesByCityFetcher()))
                .build();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, wiring);
        return GraphQL.newGraphQL(schema).build();
    }
}
