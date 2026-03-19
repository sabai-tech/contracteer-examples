package tech.sabai.contracteer.examples.musketeer.infra.web;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.sabai.contracteer.examples.musketeer.domain.CreateMission;
import tech.sabai.contracteer.examples.musketeer.domain.Mission;
import tech.sabai.contracteer.examples.musketeer.domain.MissionRepository;

import java.net.URI;
import java.util.List;

@RestController
public class MissionController {

  private final MissionRepository missionRepository;

  public MissionController(MissionRepository missionRepository) {
    this.missionRepository = missionRepository;
  }

  @GetMapping("/missions")
  public List<Mission> listMissions() {
    return missionRepository.findAll();
  }

  @GetMapping("/missions/{id}")
  public ResponseEntity<Mission> getMission(@PathVariable int id) {
    return missionRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/missions")
  public ResponseEntity<Void> createMission(@Valid @RequestBody CreateMission createMission) {
    var mission = missionRepository.create(createMission);
    return ResponseEntity
            .created(URI.create("/missions/" + mission.id()))
            .build();
  }
}
