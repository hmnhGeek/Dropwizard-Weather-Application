
# 🌦️ WeatherApp – Dropwizard Microservice with MongoDB Integration

**WeatherApp** is a lightweight Java microservice built using [Dropwizard](https://www.dropwizard.io/en/latest/). It fetches real-time weather information from the [Weatherstack API](https://weatherstack.com/) and persists location data into a MongoDB collection for future use.

---

## 📁 Project Structure

```
com.himanshu.weather
├── WeatherAppApplication.java       // Main application class
├── WeatherAppConfiguration.java     // YAML-backed configuration class
├── resources/
│   └── WeatherResource.java         // REST endpoint for /weather/{city}
├── DTOs/
│   └── WeatherResponse/             // DTOs for deserializing API responses
└── config.yml                       // Configuration file (API key, Mongo URI, etc.)
```

---

## 🔄 JSON Mapping using Jackson's `ObjectMapper`

### 🧠 What is ObjectMapper?

Dropwizard uses Jackson under the hood to serialize/deserialize JSON. `ObjectMapper` is the central tool used to:

- Convert external JSON responses into Java objects
- Convert Java objects into JSON for API responses
- Transform objects into `Map<String, Object>` for storage or manipulation

### 📥 Example Use Case

After calling the Weatherstack API, the raw JSON response is converted into a strongly-typed Java object:

```java
WeatherResponse weatherResponse = mapper.readValue(jsonResponse, WeatherResponse.class);
```

This avoids manual JSON parsing and results in cleaner, safer, and easier-to-maintain code.

### 🔁 Customizing the Mapper (Optional)

Dropwizard provides a shared instance of `ObjectMapper` which you can further customize in the application’s `run()` method:

```java
@Override
public void run(WeatherAppConfiguration config, Environment env) {
    ObjectMapper mapper = env.getObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
}
```

---

## 🍃 MongoDB Integration

### 🧩 Why MongoDB?

MongoDB is a document-oriented NoSQL database that's ideal for storing flexible data structures like location info fetched from weather APIs. This project uses the official MongoDB Java driver (no ODM like Morphia) for full control.

### 🔌 Dependencies

Add the MongoDB driver in your `pom.xml`:

```xml
<dependency>
  <groupId>org.mongodb</groupId>
  <artifactId>mongodb-driver-sync</artifactId>
  <version>4.11.1</version>
</dependency>
```

(Note: Do not remove or change existing dependencies. This addition works alongside Dropwizard.)

### 🛠️ Connecting to MongoDB

In your `WeatherAppApplication.java`:

```java
MongoClient mongoClient = MongoClients.create(config.getMongoUri());
```

Inject this client into your `WeatherResource`:

```java
public WeatherResource(String apiKey, ObjectMapper mapper, MongoClient mongoClient) {
    this.apiKey = apiKey;
    this.mapper = mapper;
    this.mongoClient = mongoClient;
}
```

### 🗃️ Saving Location Data to MongoDB

Once you get the `WeatherResponse`, you can extract the `LocationDTO` and store it like this:

#### Option 1: Manual Document Creation

```java
Document doc = new Document("name", location.getName())
    .append("country", location.getCountry())
    .append("region", location.getRegion())
    .append("lat", location.getLat())
    .append("lon", location.getLon())
    .append("timezoneId", location.getTimezoneId())
    .append("localtime", location.getLocaltime())
    .append("localtimeEpoch", location.getLocaltimeEpoch())
    .append("utcOffset", location.getUtcOffset());

collection.insertOne(doc);
```

#### Option 2: Object to Map Conversion using ObjectMapper

```java
Map<String, Object> locationMap = mapper.convertValue(location, Map.class);
Document doc = new Document(locationMap);
collection.insertOne(doc);
```

💡 This approach leverages the power of `ObjectMapper` to avoid repetitive code when converting DTOs to MongoDB documents.

### 📌 Collection & Database Info

- Database: `weatherdb`
- Collection: `cities`
- Data Stored: Only the `location` object from the WeatherResponse

---

## 🧪 Sample API Call

```bash
curl http://localhost:8080/weather/Delhi
```

Returns:

```json
{
  "location": {
    "name": "Delhi",
    "country": "India",
    ...
  },
  "current": {
    "temperature": 32,
    ...
  }
}
```

And simultaneously stores the `location` portion into MongoDB under the `cities` collection.

---

## 🚀 Future Enhancements

- Add ODM integration (Morphia or Spring Data-like annotations)
- Add indexing and search capabilities to MongoDB
- Expose a `/history` endpoint to fetch past locations from MongoDB

---

## 🧾 License

This project is for educational purposes only. Built with ❤️ by Himanshu.
