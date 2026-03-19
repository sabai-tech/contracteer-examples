package tech.sabai.contracteer.examples.musketeer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tech.sabai.contracteer.examples.musketeer.domain.*;
import tech.sabai.contracteer.verifier.junit.ContracteerServerPort;
import tech.sabai.contracteer.verifier.junit.ContracteerTest;

import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static tech.sabai.contracteer.examples.musketeer.domain.Rank.MUSKETEER;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ContractTest {

  @ContracteerServerPort
  @LocalServerPort
  int port;

  @Autowired
  MusketeerRepository musketeerRepository;

  @Autowired
  MissionRepository missionRepository;

  @ContracteerTest(openApiDoc = "classpath:musketeer-api.yaml")
  void verifyContracts() {
    musketeerRepository.clear();
    missionRepository.clear();

    musketeerRepository.save(new Musketeer(1, "Athos", MUSKETEER, "Rapier"));
    musketeerRepository.save(new Musketeer(2, "Porthos", MUSKETEER, "Musket"));
    musketeerRepository.save(new Musketeer(3, "Aramis", MUSKETEER, "Rapier"));

    missionRepository.save(new Mission(1,
            "The Diamond Studs",
            "Retrieve the Queen's diamond studs from the Duke of Buckingham",
            MissionStatus.COMPLETED,
            List.of(1, 2, 3, 4)));

    missionRepository.save(new Mission(2,
            "The Siege of La Rochelle",
            "Defend the bastion Saint-Gervais during the siege",
            MissionStatus.COMPLETED,
            List.of(1, 2, 3, 4)));
  }
}
