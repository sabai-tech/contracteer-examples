package tech.sabai.contracteer.examples.musketeer.infra.web.musketeer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tech.sabai.contracteer.examples.musketeer.domain.Musketeer;
import tech.sabai.contracteer.examples.musketeer.domain.Rank;

public record CreateMusketeer(
        @NotBlank String name,
        @NotNull Rank rank,
        @NotBlank String weapon
) {

  Musketeer toMusketeer() {
    return new Musketeer(name, rank, weapon);
  }
}