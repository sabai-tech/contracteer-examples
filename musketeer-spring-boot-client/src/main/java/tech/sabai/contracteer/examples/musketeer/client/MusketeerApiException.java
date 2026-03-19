package tech.sabai.contracteer.examples.musketeer.client;

public class MusketeerApiException extends RuntimeException {

  private final int statusCode;

  public MusketeerApiException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public int statusCode() {
    return statusCode;
  }
}
