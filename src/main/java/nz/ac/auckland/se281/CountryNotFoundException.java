package nz.ac.auckland.se281;

/**
 * Exception thrown when a specified country is not found in the graph. Used to signal invalid
 * country input or lookup failures.
 */
public class CountryNotFoundException extends Exception {

  /**
   * Constructs a new CountryNotFoundException with a detail message.
   *
   * @param message the detail message explaining which country was not found
   */
  public CountryNotFoundException(String message) {
    super(message);
  }
}
