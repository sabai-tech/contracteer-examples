package tech.sabai.contracteer.examples.musketeer.client;

import java.util.List;

public record Mission(int id, String title, String description, String status, List<Integer> musketeers) {
}
