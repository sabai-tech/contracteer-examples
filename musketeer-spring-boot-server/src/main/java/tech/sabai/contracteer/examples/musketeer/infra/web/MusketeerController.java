package tech.sabai.contracteer.examples.musketeer.infra.web;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.sabai.contracteer.examples.musketeer.domain.*;

import java.net.URI;
import java.util.List;

@RestController
public class MusketeerController {

  private final MusketeerRepository musketeerRepository;
  private final MissionRepository missionRepository;

  public MusketeerController(MusketeerRepository musketeerRepository,
                             MissionRepository missionRepository) {
    this.musketeerRepository = musketeerRepository;
    this.missionRepository = missionRepository;
  }

  @GetMapping("/musketeers")
  public List<Musketeer> listMusketeers() {
    return musketeerRepository.findAll();
  }

  @GetMapping("/musketeers/{id}")
  public ResponseEntity<Musketeer> getMusketeer(@PathVariable int id) {
    return musketeerRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/musketeers")
  public ResponseEntity<Void> createMusketeer(@Valid @RequestBody CreateMusketeer createMusketeer) {
    var musketeer = musketeerRepository.create(createMusketeer);
    return ResponseEntity
            .created(URI.create("/musketeers/" + musketeer.id()))
            .build();
  }

  @GetMapping("/musketeers/{id}/missions")
  public ResponseEntity<List<Mission>> getMusketeerMissions(@PathVariable int id,
                                                            @RequestParam(required = false) MissionStatus status) {
    return musketeerRepository.findById(id)
            .map(musketeer -> ResponseEntity.ok(missionRepository.findByMusketeer(id, status)))
            .orElse(ResponseEntity.notFound().build());
  }
}
