package nz.ac.auckland.se281;

import java.util.List;

/**
 * The Country class represents a country in the map, including its name, continent, fuel cost, and
 * neighboring countries.
 */
public class Country {
  private String name;
  private String continent;
  private int fuelCost;
  private List<String> neighbors;

  /**
   * Constructs a Country object with the specified name, continent, fuel cost, and neighbors.
   *
   * @param name the name of the country
   * @param continent the continent the country belongs to
   * @param fuelCost the fuel cost to travel through this country
   * @param neighbors a list of neighboring country names
   */
  public Country(String name, String continent, int fuelCost, List<String> neighbors) {
    this.name = name;
    this.continent = continent;
    this.fuelCost = fuelCost;
    this.neighbors = neighbors;
  }

  /**
   * Returns the name of the country.
   *
   * @return the country name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the continent of the country.
   *
   * @return the continent name
   */
  public String getContinent() {
    return continent;
  }

  /**
   * Returns the fuel cost associated with this country.
   *
   * @return the fuel cost
   */
  public int getFuelCost() {
    return fuelCost;
  }

  /**
   * Returns a list of neighboring country names.
   *
   * @return the list of neighbors
   */
  public List<String> getNeighbors() {
    return neighbors;
  }
}
