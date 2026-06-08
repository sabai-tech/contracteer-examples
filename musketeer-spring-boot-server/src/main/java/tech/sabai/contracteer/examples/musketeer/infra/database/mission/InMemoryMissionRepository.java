package tech.sabai.contracteer.examples.musketeer.infra.database.mission;

import org.springframework.stereotype.Repository;
import tech.sabai.contracteer.examples.musketeer.domain.Mission;
import tech.sabai.contracteer.examples.musketeer.domain.MissionRepository;
import tech.sabai.contracteer.examples.musketeer.domain.MissionStatus;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryMissionRepository implements MissionRepository {

  private final ConcurrentHashMap<Integer, Mission> store = new ConcurrentHashMap<>();
  private final AtomicInteger idGenerator = new AtomicInteger(0);

  @Override
  public Mission save(Mission mission) {
    var id = mission.id() == null ? idGenerator.incrementAndGet() : mission.id();
    idGenerator.updateAndGet(current -> Math.max(current, id));
    var persisted = new Mission(id, mission.title(), mission.description(), mission.status(), mission.musketeers());
    store.put(id, persisted);
    return persisted;
  }

  @Override
  public Optional<Mission> findById(int id) {
    return Optional.ofNullable(store.get(id));
  }

  @Override
  public List<Mission> findAll() {
    return List.copyOf(store.values());
  }

  @Override
  public List<Mission> findByMusketeer(int musketeerId, MissionStatus status) {
    return store.values().stream()
            .filter(mission -> mission.musketeers().contains(musketeerId))
            .filter(mission -> status == null || mission.status() == status)
            .toList();
  }

  @Override
  public void clear() {
    store.clear();
  }
}