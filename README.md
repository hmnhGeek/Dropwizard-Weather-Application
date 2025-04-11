# 🔗 Integrating GraphQL into a Dropwizard Project (Java)

This tutorial demonstrates how to integrate a GraphQL API layer into a [Dropwizard](https://www.dropwizard.io/) Java project, backed by a MongoDB data source. It walks through project structure, the technologies used, and explains the **"why"** behind each component — blending core Java, Dropwizard, GraphQL-Java, and MongoDB.

---

## 📆 Tech Stack & Libraries

- **Dropwizard**: Lightweight Java framework for building REST APIs.
- **GraphQL-Java**: Java implementation of Facebook's GraphQL spec.
- **MongoDB**: Document-based NoSQL database.
- **Lombok**: To reduce boilerplate (getters/setters/constructors).
- **JAX-RS** (`javax.ws.rs`): To expose HTTP endpoints.
- **GraphQL Schema Language**: Declarative syntax for GraphQL types.

---

## 🧱 Project Structure

```
src/main/java/com/yourcompany/yourapp/
│
├── WeatherAppApplication.java         # Main Dropwizard application class
├── GraphQLFactory.java                # Schema + wiring factory for GraphQL
│
├── graphql/
│   ├── DTOs/
│   │   └── CoordinatesDTO.java        # POJO model returned by GraphQL
│   ├── queries/
│   │   └── CoordinatesQuery.java      # Data fetcher for GraphQL query
│   └── resources/
│       └── GraphQLResource.java       # JAX-RS resource for handling GraphQL queries
```

Also add a `resources/graphql/schema.graphqls` file containing your GraphQL schema.

---

## ✅ Step-by-step Integration

### 1. **Define GraphQL Schema**

**Location**: `src/main/resources/graphql/schema.graphqls`

```graphql
type Query {
  coordinates(city: String!): Coordinates
}

type Coordinates {
  lat: String
  lon: String
}
```

**Why?**
- GraphQL is *schema-first*. This defines the structure of the API before implementing the logic.
- The schema drives type safety and auto-documentation.

---

### 2. **Create DTO for the Return Type**

**File**: `CoordinatesDTO.java`
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesDTO {
    private String lat;
    private String lon;
}
```

**Why?**
- A simple POJO (Plain Old Java Object) representing the GraphQL type.
- Uses **Lombok annotations** to auto-generate constructors and getters/setters.
- Ensures a clean separation between transport and internal models.

---

### 3. **Write the Query Resolver (Fetcher)**

**File**: `CoordinatesQuery.java`
```java
public class CoordinatesQuery {
    private final MongoClient mongoClient;

    public CoordinatesQuery(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public DataFetcher<CoordinatesDTO> getCoordinatesByCityFetcher() {
        return environment -> {
            String city = environment.getArgument("city");
            Document doc = mongoClient.getDatabase("weatherdb")
                                      .getCollection("cities")
                                      .find(new Document("name", city))
                                      .first();
            return doc == null ? null : new CoordinatesDTO(doc.getString("lat"), doc.getString("lon"));
        };
    }
}
```

**Why?**
- A **GraphQL DataFetcher** acts like a controller method in REST.
- This encapsulates MongoDB data retrieval.
- Uses **functional interfaces** and **lambda expressions**, an advanced Java feature introduced in Java 8.

---

### 4. **Build Schema + Resolver Wiring**

**File**: `GraphQLFactory.java`
```java
public class GraphQLFactory {
    public static GraphQL buildGraphQL(MongoClient mongoClient) {
        InputStreamReader reader = new InputStreamReader(
            GraphQLFactory.class.getResourceAsStream("/graphql/schema.graphqls")
        );

        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(reader);
        CoordinatesQuery coordinatesQuery = new CoordinatesQuery(mongoClient);

        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
            .type("Query", builder -> builder.dataFetcher("coordinates", coordinatesQuery.getCoordinatesByCityFetcher()))
            .build();

        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
        return GraphQL.newGraphQL(schema).build();
    }
}
```

**Why?**
- Responsible for **assembling the GraphQL schema and resolvers**.
- Uses **GraphQL-Java's SchemaParser/SchemaGenerator**.
- Advanced Java: Leverages builder pattern, I/O streams, and classpath resource loading.

---

### 5. **Expose GraphQL Endpoint**

**File**: `GraphQLResource.java`
```java
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
```

**Why?**
- This is the **HTTP entry point** for all GraphQL queries.
- Uses **JAX-RS annotations** to map it as a RESTful resource (`/graphql`).
- Converts GraphQL response to a `Map<String, Object>` (dynamic JSON-like structure).

---

### 6. **Register in Dropwizard App**

**File**: `WeatherAppApplication.java`
```java
@Override
public void run(WeatherAppConfiguration config, Environment environment) {
    MongoProvider mongoProvider = new MongoProvider(config.getMongoUri());
    MongoClient mongoClient = mongoProvider.getClient();

    // Existing REST resource
    environment.jersey().register(new WeatherResource(...));

    // Register GraphQL
    GraphQL graphQL = GraphQLFactory.buildGraphQL(mongoClient);
    environment.jersey().register(new GraphQLResource(graphQL));
}
```

**Why?**
- This is the central bootstrapping class of any Dropwizard app.
- Registers both **GraphQL** and **REST** endpoints with the Jersey environment.
- Advanced Java: uses **Dependency Injection** manually without any DI framework.

---

## 📢 Sample GraphQL Query (Postman or Playground)

```json
POST /graphql
Content-Type: application/json
{
  "query": "{ coordinates(city: \"Haridwar\") { lat lon } }"
}
```

**Why nested `{ lat lon }`?**
- In GraphQL, you explicitly specify which fields to fetch. This is a **feature**, not a limitation.

---

## ✨ Final Notes

- You now have a fully functional GraphQL endpoint alongside your Dropwizard REST API.
- It's modular, testable, and scalable.
- Want multiple queries? Add more `DataFetchers` and expand the schema.

---

## 🔍 References

- [GraphQL Java](https://www.graphql-java.com/)
- [Dropwizard](https://www.dropwizard.io/)
- [Lombok](https://projectlombok.org/)
- [GraphQL Spec](https://spec.graphql.org/)
