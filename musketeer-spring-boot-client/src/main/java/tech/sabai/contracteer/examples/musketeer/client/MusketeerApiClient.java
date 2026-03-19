package tech.sabai.contracteer.examples.musketeer.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
public class MusketeerApiClient {

  private final RestClient restClient;

  public MusketeerApiClient(@Value("${musketeer.api.base-url}") String baseUrl) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();
  }

  public List<Musketeer> listMusketeers() {
    return restClient.get()
            .uri("/musketeers")
            .retrieve()
            .body(new ParameterizedTypeReference<>() {
            });
  }

  public Optional<Musketeer> getMusketeer(int id) {
    return restClient.get()
            .uri("/musketeers/{id}", id)
            .exchange((request, response) -> {
              if (response.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(response.bodyTo(Musketeer.class));
              }
              if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                return Optional.empty();
              }
              throw new MusketeerApiException(response.getStatusCode().value(),
                      "GET /musketeers/" + id + " failed: " + response.getStatusCode());
            });
  }

  public Musketeer enlistMusketeer(CreateMusketeer createMusketeer) {
    var location = restClient.post()
            .uri("/musketeers")
            .contentType(MediaType.APPLICATION_JSON)
            .body(createMusketeer)
            .exchange((request, response) -> {
              if (response.getStatusCode().is2xxSuccessful()) {
                return response.getHeaders().getFirst("Location");
              }
              throw new MusketeerApiException(response.getStatusCode().value(),
                      "POST /musketeers failed: " + response.getStatusCode());
            });
    return restClient.get()
            .uri(location)
            .retrieve()
            .body(Musketeer.class);
  }

  public List<Mission> listMissions() {
    return restClient.get()
            .uri("/missions")
            .retrieve()
            .body(new ParameterizedTypeReference<>() {
            });
  }

  public Optional<Mission> getMission(int id) {
    return restClient.get()
            .uri("/missions/{id}", id)
            .exchange((request, response) -> {
              if (response.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(response.bodyTo(Mission.class));
              }
              if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                return Optional.empty();
              }
              throw new MusketeerApiException(response.getStatusCode().value(),
                      "GET /missions/" + id + " failed: " + response.getStatusCode());
            });
  }

  public List<Mission> getMusketeerMissions(int musketeerId, String status) {
    return restClient.get()
            .uri(uriBuilder -> {
              uriBuilder.path("/musketeers/{id}/missions");
              if (status != null) {
                uriBuilder.queryParam("status", status);
              }
              return uriBuilder.build(musketeerId);
            })
            .exchange((request, response) -> {
              if (response.getStatusCode().is2xxSuccessful()) {
                return response.bodyTo(new ParameterizedTypeReference<>() {
                });
              }
              throw new MusketeerApiException(response.getStatusCode().value(),
                      "GET /musketeers/" + musketeerId + "/missions failed: " + response.getStatusCode());
            });
  }

  public Mission createMission(CreateMission createMission) {
    var location = restClient.post()
            .uri("/missions")
            .contentType(MediaType.APPLICATION_JSON)
            .body(createMission)
            .exchange((request, response) -> {
              if (response.getStatusCode().is2xxSuccessful()) {
                return response.getHeaders().getFirst("Location");
              }
              throw new MusketeerApiException(response.getStatusCode().value(),
                      "POST /missions failed: " + response.getStatusCode());
            });
    return restClient.get()
            .uri(location)
            .retrieve()
            .body(Mission.class);
  }
}
