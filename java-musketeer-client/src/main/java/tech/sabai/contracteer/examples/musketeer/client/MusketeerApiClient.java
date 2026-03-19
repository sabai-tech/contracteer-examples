package tech.sabai.contracteer.examples.musketeer.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

public class MusketeerApiClient {

  private final String baseUrl;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public MusketeerApiClient(String baseUrl) {
    this.baseUrl = baseUrl;
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = new ObjectMapper();
  }

  public List<Musketeer> listMusketeers() {
    var request = HttpRequest.newBuilder()
                             .uri(URI.create(baseUrl + "/musketeers"))
                             .GET()
                             .build();

    var response = send(request);

    if (isSuccessful(response)) return readValue(response.body(), new TypeReference<>() {
    });

    throw new MusketeerApiException(response.statusCode(), "GET /musketeers failed: " + response.statusCode());
  }

  public Optional<Musketeer> getMusketeer(int id) {
    var request = HttpRequest.newBuilder()
                             .uri(URI.create(baseUrl + "/musketeers/" + id))
                             .GET()
                             .build();

    var response = send(request);

    if (isSuccessful(response)) return Optional.of(readValue(response.body(), Musketeer.class));
    if (response.statusCode() == 404) return Optional.empty();

    throw new MusketeerApiException(response.statusCode(), "GET /musketeers/" + id + " failed: " + response.statusCode());
  }

  public Musketeer enlistMusketeer(CreateMusketeer createMusketeer) {
    var request = HttpRequest.newBuilder()
                             .uri(URI.create(baseUrl + "/musketeers"))
                             .header("Content-Type", "application/json")
                             .POST(HttpRequest.BodyPublishers.ofString(writeValue(createMusketeer)))
                             .build();

    var response = send(request);

    if (isSuccessful(response)) {
      var location = response.headers()
                             .firstValue("Location")
                             .orElseThrow(() -> new MusketeerApiException(response.statusCode(),
                                     "POST /musketeers: missing Location header"));

      var getRequest = HttpRequest.newBuilder()
                                  .uri(URI.create(baseUrl + location))
                                  .GET()
                                  .build();

      var getResponse = send(getRequest);
      return readValue(getResponse.body(), Musketeer.class);
    }

    throw new MusketeerApiException(response.statusCode(), "POST /musketeers failed: " + response.statusCode());
  }

  public List<Mission> listMissions() {
    var request = HttpRequest.newBuilder()
                             .uri(URI.create(baseUrl + "/missions"))
                             .GET()
                             .build();

    var response = send(request);

    if (isSuccessful(response)) return readValue(response.body(), new TypeReference<>() {
    });

    throw new MusketeerApiException(response.statusCode(), "GET /missions failed: " + response.statusCode());
  }

  public Optional<Mission> getMission(int id) {
    var request = HttpRequest.newBuilder()
                             .uri(URI.create(baseUrl + "/missions/" + id))
                             .GET()
                             .build();

    var response = send(request);

    if (isSuccessful(response)) return Optional.of(readValue(response.body(), Mission.class));
    if (response.statusCode() == 404) return Optional.empty();

    throw new MusketeerApiException(response.statusCode(), "GET /missions/" + id + " failed: " + response.statusCode());
  }

  public Mission createMission(CreateMission createMission) {
    var request = HttpRequest.newBuilder()
                             .uri(URI.create(baseUrl + "/missions"))
                             .header("Content-Type", "application/json")
                             .POST(HttpRequest.BodyPublishers.ofString(writeValue(createMission)))
                             .build();

    var response = send(request);

    if (isSuccessful(response)) {
      var location = response.headers()
                             .firstValue("Location")
                             .orElseThrow(() -> new MusketeerApiException(response.statusCode(),
                                     "POST /missions: missing Location header"));

      var getRequest = HttpRequest.newBuilder()
                                  .uri(URI.create(baseUrl + location))
                                  .GET()
                                  .build();

      var getResponse = send(getRequest);
      return readValue(getResponse.body(), Mission.class);
    }

    throw new MusketeerApiException(response.statusCode(), "POST /missions failed: " + response.statusCode());
  }

  public List<Mission> getMusketeerMissions(int musketeerId, String status) {
    var uri = status != null
            ? baseUrl + "/musketeers/" + musketeerId + "/missions?status=" + status
            : baseUrl + "/musketeers/" + musketeerId + "/missions";

    var request = HttpRequest.newBuilder()
                             .uri(URI.create(uri))
                             .GET()
                             .build();

    var response = send(request);

    if (isSuccessful(response)) return readValue(response.body(), new TypeReference<>() {
    });

    throw new MusketeerApiException(response.statusCode(),
            "GET /musketeers/" + musketeerId + "/missions failed: " + response.statusCode());
  }

  private HttpResponse<String> send(HttpRequest request) {
    try {
      return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  private <T> T readValue(String json, Class<T> type) {
    try {
      return objectMapper.readValue(json, type);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private <T> T readValue(String json, TypeReference<T> type) {
    try {
      return objectMapper.readValue(json, type);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private String writeValue(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isSuccessful(HttpResponse<String> response) {
    return response.statusCode() >= 200 && response.statusCode() < 300;
  }
}
