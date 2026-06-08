package tech.sabai.contracteer.examples.musketeer.domain;

import java.util.List;
import java.util.Optional;

public interface MusketeerRepository {
  Musketeer save(Musketeer musketeer);

  Optional<Musketeer> findById(int id);

  List<Musketeer> findAll();

  void clear();
}