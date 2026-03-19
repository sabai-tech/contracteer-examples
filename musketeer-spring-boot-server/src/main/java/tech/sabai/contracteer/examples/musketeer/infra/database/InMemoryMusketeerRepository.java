package tech.sabai.contracteer.examples.musketeer.infra.database;

import org.springframework.stereotype.Repository;
import tech.sabai.contracteer.examples.musketeer.domain.CreateMusketeer;
import tech.sabai.contracteer.examples.musketeer.domain.Musketeer;
import tech.sabai.contracteer.examples.musketeer.domain.MusketeerRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryMusketeerRepository implements MusketeerRepository {

  private final ConcurrentHashMap<Integer, Musketeer> store = new ConcurrentHashMap<>();
  private final AtomicInteger idGenerator = new AtomicInteger(0);

  @Override
  public void save(Musketeer musketeer) {
    store.put(musketeer.id(), musketeer);
    idGenerator.updateAndGet(current -> Math.max(current, musketeer.id()));
  }

  @Override
  public Musketeer create(CreateMusketeer createMusketeer) {
    var musketeer = new Musketeer(
            idGenerator.incrementAndGet(),
            createMusketeer.name(),
            createMusketeer.rank(),
            createMusketeer.weapon());
    store.put(musketeer.id(), musketeer);
    return musketeer;
  }

  @Override
  public Optional<Musketeer> findById(int id) {
    return Optional.ofNullable(store.get(id));
  }

  @Override
  public List<Musketeer> findAll() {
    return List.copyOf(store.values());
  }

  @Override
  public void clear() {
    store.clear();
  }
}
