package nz.ac.auckland.se281;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Graph class represents a collection of countries and their relationships. It provides methods
 * to add countries, retrieve them, and check for their existence.
 */
public class Graph {
  /** Map storing country names as keys and Country objects as values. */
  private Map<String, Country> countries;

  /** Constructs an empty Graph. Uses LinkedHashMap to preserve insertion order. */
  public Graph() {
    countries = new LinkedHashMap<>(); // preserves insertion order
  }

  /**
   * Adds a country to the graph with the given properties.
   *
   * @param name the name of the country
   * @param continent the continent the country belongs to
   * @param fuelCost the fuel cost to travel through this country
   * @param neighbors a list of neighboring country names
   */
  public void addCountry(String name, String continent, int fuelCost, List<String> neighbors) {
    Country country = new Country(name, continent, fuelCost, neighbors);
    countries.put(name, country);
  }

  /**
   * Retrieves the Country object for the given country name.
   *
   * @param name the name of the country
   * @return the Country object, or null if not found
   */
  public Country getCountry(String name) {
    return countries.get(name);
  }

  /**
   * Checks if the graph contains a country with the given name.
   *
   * @param name the name of the country
   * @return true if the country exists, false otherwise
   */
  public boolean containsCountry(String name) {
    return countries.containsKey(name);
  }

  /**
   * Returns a collection of all Country objects in the graph.
   *
   * @return a collection of all countries
   */
  public Collection<Country> getAllCountries() {
    return countries.values();
  }
}
