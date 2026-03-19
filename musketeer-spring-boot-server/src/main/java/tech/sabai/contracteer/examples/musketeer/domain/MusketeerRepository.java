package tech.sabai.contracteer.examples.musketeer.domain;

import java.util.List;
import java.util.Optional;

public interface MusketeerRepository {
  void save(Musketeer musketeer);

  Musketeer create(CreateMusketeer createMusketeer);

  Optional<Musketeer> findById(int id);

  List<Musketeer> findAll();

  void clear();
}
