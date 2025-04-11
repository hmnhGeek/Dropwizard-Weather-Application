package com.himanshu.weather.graphql.resources;


import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/graphql")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphQLResource {
    private final GraphQL graphQL;

    public GraphQLResource(GraphQL graphQL) {
        this.graphQL = graphQL;
    }

    @POST
    public Map<String, Object> query(Map<String, Object> request) {
        String query = (String) request.get("query");
        ExecutionInput input = ExecutionInput.newExecutionInput().query(query).build();
        ExecutionResult result = graphQL.execute(input);
        return result.toSpecification();
    }
}
