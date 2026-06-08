package tech.sabai.contracteer.examples.musketeer.domain;

import java.util.List;
import java.util.Optional;

public interface MissionRepository {
  Mission save(Mission mission);

  Optional<Mission> findById(int id);

  List<Mission> findAll();

  List<Mission> findByMusketeer(int musketeerId, MissionStatus status);

  void clear();
}