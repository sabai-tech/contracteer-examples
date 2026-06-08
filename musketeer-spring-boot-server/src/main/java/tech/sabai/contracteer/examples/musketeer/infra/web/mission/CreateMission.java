package tech.sabai.contracteer.examples.musketeer.infra.web.mission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tech.sabai.contracteer.examples.musketeer.domain.Mission;
import tech.sabai.contracteer.examples.musketeer.domain.MissionStatus;

import java.util.List;

public record CreateMission(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull MissionStatus status,
        @NotNull List<Integer> musketeers
) {

  Mission toMission() {
    return new Mission(title, description, status, musketeers);
  }
}