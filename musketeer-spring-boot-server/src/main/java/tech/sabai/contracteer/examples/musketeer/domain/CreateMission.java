package tech.sabai.contracteer.examples.musketeer.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateMission(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull MissionStatus status,
        @NotNull List<Integer> musketeers
) {
}
