package tech.sabai.contracteer.examples.musketeer.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tech.sabai.contracteer.examples.musketeer.client.*;

import java.util.List;

@Component
@Profile("!test")
public class MusketeerCli implements CommandLineRunner {

  private final MusketeerApiClient client;

  public MusketeerCli(MusketeerApiClient client) {
    this.client = client;
  }

  @Override
  public void run(String... args) {
    if (args.length == 0) {
      printUsage();
      return;
    }

    switch (args[0]) {
      case "list-musketeers" -> listMusketeers();
      case "get-musketeer" -> getMusketeer(args);
      case "enlist-musketeer" -> enlistMusketeer(args);
      case "list-missions" -> listMissions();
      case "get-mission" -> getMission(args);
      case "create-mission" -> createMission(args);
      case "get-musketeer-missions" -> getMusketeerMissions(args);
      default -> printUsage();
    }
  }

  private void listMusketeers() {
    System.out.println("Musketeers:");
    for (Musketeer m : client.listMusketeers()) {
      printMusketeer(m);
    }
  }

  private void getMusketeer(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: get-musketeer <id>");
      return;
    }
    int id = Integer.parseInt(args[1]);
    client.getMusketeer(id).ifPresentOrElse(
            this::printMusketeer,
            () -> System.out.println("Musketeer not found (id=" + id + ")")
    );
  }

  private void enlistMusketeer(String[] args) {
    if (args.length < 4) {
      System.out.println("Usage: enlist-musketeer <name> <rank> <weapon>");
      return;
    }
    var musketeer = client.enlistMusketeer(new CreateMusketeer(args[1], args[2], args[3]));
    System.out.println("Musketeer enlisted:");
    printMusketeer(musketeer);
  }

  private void listMissions() {
    System.out.println("Missions:");
    for (Mission m : client.listMissions()) {
      printMission(m);
    }
  }

  private void getMission(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: get-mission <id>");
      return;
    }
    int id = Integer.parseInt(args[1]);
    client.getMission(id).ifPresentOrElse(
            this::printMission,
            () -> System.out.println("Mission not found (id=" + id + ")")
    );
  }

  private void createMission(String[] args) {
    if (args.length < 5) {
      System.out.println("Usage: create-mission <title> <description> <status> <id1,id2,...>");
      return;
    }
    var musketeerIds = java.util.Arrays.stream(args[4].split(","))
            .map(String::trim)
            .map(Integer::parseInt)
            .toList();
    var mission = client.createMission(new CreateMission(args[1], args[2], args[3], musketeerIds));
    System.out.println("Mission created:");
    printMission(mission);
  }

  private void getMusketeerMissions(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: get-musketeer-missions <id> [status]");
      return;
    }
    int id = Integer.parseInt(args[1]);
    String status = args.length > 2 ? args[2] : null;
    System.out.println("Missions for musketeer " + id + ":");
    for (Mission m : client.getMusketeerMissions(id, status)) {
      printMission(m);
    }
  }

  private void printMusketeer(Musketeer m) {
    System.out.printf("  [%d] %s — %s (%s)%n", m.id(), m.name(), m.rank(), m.weapon());
  }

  private void printMission(Mission m) {
    var ids = m.musketeers().stream().map(String::valueOf).toList();
    System.out.printf("  [%d] %s — %s (musketeers: %s)%n",
            m.id(), m.title(), m.status(), String.join(", ", ids));
  }

  private void printUsage() {
    System.out.println("Usage: <command>");
    System.out.println("  list-musketeers                                           List all musketeers");
    System.out.println("  get-musketeer <id>                                        Get a musketeer by ID");
    System.out.println("  enlist-musketeer <name> <rank> <weapon>                   Enlist a new musketeer");
    System.out.println("  list-missions                                             List all missions");
    System.out.println("  get-mission <id>                                          Get a mission by ID");
    System.out.println("  create-mission <title> <desc> <status> <musketeers>       Create a new mission");
    System.out.println("  get-musketeer-missions <id> [status]                      Get missions for a musketeer");
  }
}
