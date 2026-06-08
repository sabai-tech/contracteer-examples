package tech.sabai.contracteer.examples.musketeer.infra.database.musketeer;

import org.springframework.stereotype.Repository;
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
  public Musketeer save(Musketeer musketeer) {
    var id = musketeer.id() == null ? idGenerator.incrementAndGet() : musketeer.id();
    idGenerator.updateAndGet(current -> Math.max(current, id));
    var persisted = new Musketeer(id, musketeer.name(), musketeer.rank(), musketeer.weapon());
    store.put(id, persisted);
    return persisted;
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