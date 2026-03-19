package tech.sabai.contracteer.examples.musketeer.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Musketeer(
        int id,
        @NotBlank String name,
        @NotNull Rank rank,
        @NotBlank String weapon
) {
}
