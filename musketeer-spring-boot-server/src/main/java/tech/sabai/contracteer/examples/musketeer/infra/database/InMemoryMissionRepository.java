package tech.sabai.contracteer.examples.musketeer.infra.database;

import org.springframework.stereotype.Repository;
import tech.sabai.contracteer.examples.musketeer.domain.CreateMission;
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
  public void save(Mission mission) {
    store.put(mission.id(), mission);
    idGenerator.updateAndGet(current -> Math.max(current, mission.id()));
  }

  @Override
  public Mission create(CreateMission createMission) {
    var mission = new Mission(
            idGenerator.incrementAndGet(),
            createMission.title(),
            createMission.description(),
            createMission.status(),
            createMission.musketeers());
    store.put(mission.id(), mission);
    return mission;
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
