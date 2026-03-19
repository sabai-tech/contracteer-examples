package tech.sabai.contracteer.examples.musketeer.domain;

import java.util.List;
import java.util.Optional;

public interface MissionRepository {
  void save(Mission mission);

  Mission create(CreateMission createMission);

  Optional<Mission> findById(int id);

  List<Mission> findAll();

  List<Mission> findByMusketeer(int musketeerId, MissionStatus status);

  void clear();
}
