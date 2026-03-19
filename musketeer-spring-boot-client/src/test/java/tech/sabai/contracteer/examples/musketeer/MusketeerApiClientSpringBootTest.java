package tech.sabai.contracteer.examples.musketeer;

// ---------------------------------------------------------------------------
// Spring Boot integration test — uses @ContracteerMockServer to start the
// mock server automatically within the Spring test context.
//
// The annotation loads the OpenAPI spec, starts a mock server on a random
// port, and injects its base URL into the specified Spring property. The
// MusketeerApiClient is autowired and configured via that property.
//
// Use this approach when your client is a Spring-managed bean and you want
// the mock server lifecycle tied to the Spring test context.
//
// Assertions verify response structure (not null, present, positive id)
// rather than specific values. The mock server returns scenario-matched
// or schema-generated responses — tests should not depend on example
// data from the specification.
// ---------------------------------------------------------------------------

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tech.sabai.contracteer.examples.musketeer.client.CreateMission;
import tech.sabai.contracteer.examples.musketeer.client.CreateMusketeer;
import tech.sabai.contracteer.examples.musketeer.client.MusketeerApiClient;
import tech.sabai.contracteer.examples.musketeer.client.MusketeerApiException;
import tech.sabai.contracteer.mockserver.spring.ContracteerMockServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@ContracteerMockServer(
        openApiDoc = "classpath:musketeer-api.yaml",
        baseUrlProperty = "musketeer.api.base-url"
)
class MusketeerApiClientSpringBootTest {

  @Autowired
  MusketeerApiClient client;

  // --- Musketeers ---
  @Test
  @DisplayName("retrieve all musketeers")
  void retrieve_all_musketeers() {
    // When
    var musketeers = client.listMusketeers();

    // Then
    assertThat(musketeers).isNotNull();
  }

  @Test
  @DisplayName("retrieve a musketeer by id")
  void retrieve_a_musketeer_by_id() {
    // When
    var maybeMusketeer = client.getMusketeer(1);

    // Then
    assertThat(maybeMusketeer).isPresent();
    assertThat(maybeMusketeer.get().name()).isNotBlank();
    assertThat(maybeMusketeer.get().rank()).isNotNull();
  }

  @Test
  @DisplayName("return empty when musketeer does not exist")
  void return_empty_when_musketeer_does_not_exist() {
    // When
    var maybeMusketeer = client.getMusketeer(999);

    // Then
    assertThat(maybeMusketeer).isEmpty();
  }

  @Test
  @DisplayName("enlist a new musketeer")
  void enlist_a_new_musketeer() {
    // Given
    var createMusketeer = new CreateMusketeer("d'Artagnan", "CADET", "Rapier");

    // When
    var musketeer = client.enlistMusketeer(createMusketeer);

    // Then
    assertThat(musketeer).isNotNull();
    assertThat(musketeer.id()).isPositive();
  }

  @Test
  @DisplayName("reject enlistment when rank is invalid")
  void reject_enlistment_when_rank_is_invalid() {
    // Given
    var createMusketeer = new CreateMusketeer("d'Artagnan", "KNIGHT", "Rapier");

    // When / Then
    assertThatThrownBy(() -> client.enlistMusketeer(createMusketeer))
            .isInstanceOf(MusketeerApiException.class)
            .extracting("statusCode")
            .isEqualTo(400);
  }

  // --- Missions ---
  @Test
  @DisplayName("retrieve all missions")
  void retrieve_all_missions() {
    // When
    var missions = client.listMissions();

    // Then
    assertThat(missions).isNotNull();
  }

  @Test
  @DisplayName("retrieve a mission by id")
  void retrieve_a_mission_by_id() {
    // When
    var maybeMission = client.getMission(1);

    // Then
    assertThat(maybeMission).isPresent();
    assertThat(maybeMission.get().title()).isNotBlank();
    assertThat(maybeMission.get().status()).isNotNull();
    assertThat(maybeMission.get().musketeers()).isNotNull();
  }

  @Test
  @DisplayName("return empty when mission does not exist")
  void return_empty_when_mission_does_not_exist() {
    // When
    var maybeMission = client.getMission(999);

    // Then
    assertThat(maybeMission).isEmpty();
  }

  @Test
  @DisplayName("create a new mission")
  void create_a_new_mission() {
    // Given
    var createMission = new CreateMission(
            "Rescue Constance",
            "Free Constance Bonacieux from the clutches of Milady de Winter",
            "PLANNED", List.of(4, 1));

    // When
    var mission = client.createMission(createMission);

    // Then
    assertThat(mission).isNotNull();
    assertThat(mission.id()).isPositive();
  }

  @Test
  @DisplayName("reject creation when mission status is invalid")
  void reject_creation_when_mission_status_is_invalid() {
    // Given
    var createMission = new CreateMission(
            "Rescue Constance",
            "Free Constance Bonacieux from the clutches of Milady de Winter",
            "UNKNOWN", List.of(4, 1));

    // When / Then
    assertThatThrownBy(() -> client.createMission(createMission))
            .isInstanceOf(MusketeerApiException.class)
            .extracting("statusCode")
            .isEqualTo(400);
  }

  // --- Musketeer missions ---
  @Test
  @DisplayName("retrieve musketeer missions filtered by status")
  void retrieve_musketeer_missions_filtered_by_status() {
    // When
    var missions = client.getMusketeerMissions(1, "COMPLETED");

    // Then
    assertThat(missions).isNotNull();
  }

  @Test
  @DisplayName("reject musketeer missions retrieval when status is invalid")
  void reject_musketeer_missions_retrieval_when_status_is_invalid() {
    // When / Then
    assertThatThrownBy(() -> client.getMusketeerMissions(1, "UNKNOWN"))
            .isInstanceOf(MusketeerApiException.class)
            .extracting("statusCode")
            .isEqualTo(400);
  }
}
