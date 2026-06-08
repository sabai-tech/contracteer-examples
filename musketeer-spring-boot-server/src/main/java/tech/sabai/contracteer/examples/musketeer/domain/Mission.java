package tech.sabai.contracteer.examples.musketeer.domain;

import java.util.List;

public record Mission(
        Integer id,
        String title,
        String description,
        MissionStatus status,
        List<Integer> musketeers
) {
  public Mission(String title, String description, MissionStatus status, List<Integer> musketeers) {
    this(null, title, description, status, musketeers);
  }
}