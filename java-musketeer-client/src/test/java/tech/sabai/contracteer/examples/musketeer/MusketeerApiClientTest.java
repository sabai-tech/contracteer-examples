package tech.sabai.contracteer.examples.musketeer;

// ---------------------------------------------------------------------------
// Assertions verify response structure (not null, present, positive id)
// rather than specific values. The mock server returns scenario-matched
// or schema-generated responses — tests should not depend on example
// data from the specification.
// ---------------------------------------------------------------------------

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.sabai.contracteer.core.swagger.OpenApiLoader;
import tech.sabai.contracteer.examples.musketeer.client.CreateMission;
import tech.sabai.contracteer.examples.musketeer.client.CreateMusketeer;
import tech.sabai.contracteer.examples.musketeer.client.MusketeerApiClient;
import tech.sabai.contracteer.examples.musketeer.client.MusketeerApiException;

import java.util.List;
import tech.sabai.contracteer.mockserver.MockServer;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MusketeerApiClientTest {

  static MockServer mockServer;
  MusketeerApiClient client;

  @BeforeAll
  static void startMockServer() {
    var result = OpenApiLoader.loadOperations("classpath:musketeer-api.yaml");
    if (result.isFailure()) throw new IllegalStateException("Failed to load spec: " + result.errors());

    mockServer = new MockServer(requireNonNull(result.getValue()));
    mockServer.start();
  }

  @AfterAll
  static void stopMockServer() {
    mockServer.stop();
  }

  @BeforeEach
  void setUp() {
    client = new MusketeerApiClient("http://localhost:" + mockServer.port());
  }

  @Test
  @DisplayName("retrieve all musketeers")
  void retrieve_all_musketeers() {
    var musketeers = client.listMusketeers();
    assertThat(musketeers).isNotNull();
  }

  @Test
  @DisplayName("retrieve a musketeer by id")
  void retrieve_a_musketeer_by_id() {
    var maybeMusketeer = client.getMusketeer(1);
    assertThat(maybeMusketeer).isPresent();
  }

  @Test
  @DisplayName("enlist a new musketeer")
  void enlist_a_new_musketeer() {
    var createMusketeer = new CreateMusketeer("d'Artagnan", "CADET", "Rapier");
    var musketeer = client.enlistMusketeer(createMusketeer);
    assertThat(musketeer).isNotNull();
  }

  @Test
  @DisplayName("reject enlistment when rank is invalid")
  void reject_enlistment_when_rank_is_invalid() {
    var createMusketeer = new CreateMusketeer("d'Artagnan", "KNIGHT", "Rapier");
    assertThatThrownBy(() -> client.enlistMusketeer(createMusketeer))
            .isInstanceOf(MusketeerApiException.class)
            .extracting("statusCode")
            .isEqualTo(400);
  }

  // --- Missions ---
  @Test
  @DisplayName("retrieve all missions")
  void retrieve_all_missions() {
    var missions = client.listMissions();
    assertThat(missions).isNotNull();
  }

  @Test
  @DisplayName("retrieve a mission by id")
  void retrieve_a_mission_by_id() {
    var maybeMission = client.getMission(1);
    assertThat(maybeMission).isPresent();
    assertThat(maybeMission.get().title()).isNotBlank();
    assertThat(maybeMission.get().status()).isNotNull();
    assertThat(maybeMission.get().musketeers()).isNotNull();
  }

  @Test
  @DisplayName("return empty when mission does not exist")
  void return_empty_when_mission_does_not_exist() {
    var maybeMission = client.getMission(999);
    assertThat(maybeMission).isEmpty();
  }

  @Test
  @DisplayName("create a new mission")
  void create_a_new_mission() {
    var createMission = new CreateMission(
            "Rescue Constance",
            "Free Constance Bonacieux from the clutches of Milady de Winter",
            "PLANNED", List.of(4, 1));
    var mission = client.createMission(createMission);
    assertThat(mission).isNotNull();
    assertThat(mission.id()).isPositive();
  }

  @Test
  @DisplayName("reject creation when mission status is invalid")
  void reject_creation_when_mission_status_is_invalid() {
    var createMission = new CreateMission(
            "Rescue Constance",
            "Free Constance Bonacieux from the clutches of Milady de Winter",
            "UNKNOWN", List.of(4, 1));
    assertThatThrownBy(() -> client.createMission(createMission))
            .isInstanceOf(MusketeerApiException.class)
            .extracting("statusCode")
            .isEqualTo(400);
  }

  // --- Musketeer missions ---
  @Test
  @DisplayName("retrieve musketeer missions filtered by status")
  void retrieve_musketeer_missions_filtered_by_status() {
    var missions = client.getMusketeerMissions(1, "COMPLETED");
    assertThat(missions).isNotNull();
  }

  @Test
  @DisplayName("reject musketeer missions retrieval when status is invalid")
  void reject_musketeer_missions_retrieval_when_status_is_invalid() {
    assertThatThrownBy(() -> client.getMusketeerMissions(1, "UNKNOWN"))
            .isInstanceOf(MusketeerApiException.class)
            .extracting("statusCode")
            .isEqualTo(400);
  }
}
