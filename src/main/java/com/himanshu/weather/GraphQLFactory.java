package com.himanshu.weather;

import com.himanshu.weather.graphql.queries.CoordinatesQuery;
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
        CoordinatesQuery queryResolver = new CoordinatesQuery(mongoClient);
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("coordinates", queryResolver.getCoordinatesByCityFetcher()))
                .build();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, wiring);
        return GraphQL.newGraphQL(schema).build();
    }
}
