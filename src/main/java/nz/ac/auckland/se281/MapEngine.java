package nz.ac.auckland.se281;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * The MapEngine class manages the map data and user interactions for querying country information
 * and finding routes between countries. It loads the map from data files, validates user input, and
 * provides methods to display country info and compute shortest routes with fuel calculations.
 */
public class MapEngine {

  /** The graph representing countries and their connections. */
  private Graph graph;

  /** Constructs a MapEngine and loads the map data into the graph. */
  public MapEngine() {
    graph = new Graph();
    loadMap();
  }

  /**
   * Loads country and adjacency data from files, builds the neighbor map, and populates the graph
   * with Country objects.
   */
  private void loadMap() {
    List<String> countriesData = Utils.readCountries();
    List<String> adjacenciesData = Utils.readAdjacencies();

    // Create map of countryName → List of neighbors
    Map<String, List<String>> neighborMap = new LinkedHashMap<>();
    for (String line : adjacenciesData) {
      String[] tokens = line.split(",");
      String country = tokens[0];
      List<String> neighbors = Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length));
      neighborMap.put(country, neighbors);
    }

    // Build graph: parse each country line
    for (String line : countriesData) {
      String[] tokens = line.split(",");
      String name = tokens[0];
      String continent = tokens[1];
      int fuelCost = Integer.parseInt(tokens[2]);

      List<String> neighbors = neighborMap.getOrDefault(name, new ArrayList<>());
      graph.addCountry(name, continent, fuelCost, neighbors);
    }
  }

  /**
   * Displays information about a specific country selected by the user.
   * This method prompts the user to enter a valid country name, retrieves the corresponding
   * {@link Country} object from the graph, and then gathers relevant information such as the
   * country's continent, fuel cost, and neighboring countries. The collected information is
   * formatted and displayed to the user using the {@link MessageCli#COUNTRY_INFO} message.
   */
  public void showInfoCountry() {
    // Prompt the user for a valid country name
    String countryName = promptValidCountryName();
    // Retrieve the Country object from the graph
    Country country = graph.getCountry(countryName);

    // Get the continent and fuel cost of the country
    String continent = country.getContinent();
    String fuelCostStr = String.valueOf(country.getFuelCost());
    // Get the list of neighboring countries
    List<String> neighbors = country.getNeighbors();

    // Format the neighbors list as a string
    String neighborStr = "[" + String.join(", ", neighbors) + "]";
    // Display the country information to the user
    MessageCli.COUNTRY_INFO.printMessage(countryName, continent, fuelCostStr, neighborStr);
  }

  /**
   * Prompts the user for a source and destination country, finds the shortest route, and displays
   * the route, total fuel cost, fuel per continent, and the continent with the highest fuel
   * consumption.
   */
  public void showRoute() {
    String source = promptValidCountryName();
    String destination = promptValidCountryName();

    if (source.equals(destination)) {
      MessageCli.NO_CROSSBORDER_TRAVEL.printMessage();
      return;
    }

    List<String> path = findShortestPath(source, destination);

    if (path == null) {
      MessageCli.NO_CROSSBORDER_TRAVEL.printMessage();
      return;
    }

    // Print route
    MessageCli.ROUTE_INFO.printMessage(path.toString());

    // Calculate fuel per continent
    int totalFuel = 0;
    Map<String, Integer> fuelPerContinent = new LinkedHashMap<>();
    for (int i = 1; i < path.size() - 1; i++) {
      String countryName = path.get(i);
      Country c = graph.getCountry(countryName);
      int fuel = c.getFuelCost();
      String continent = c.getContinent();
      totalFuel += fuel;
      fuelPerContinent.put(continent, fuelPerContinent.getOrDefault(continent, 0) + fuel);
    }

    MessageCli.FUEL_INFO.printMessage(String.valueOf(totalFuel));

    // Build continent visit summary
    List<String> continentSummary = new ArrayList<>();
    Set<String> seenContinents = new HashSet<>();
    for (String countryName : path) {
      String continent = graph.getCountry(countryName).getContinent();
      if (!seenContinents.contains(continent)) {
        seenContinents.add(continent);
        int fuel = fuelPerContinent.getOrDefault(continent, 0);
        continentSummary.add(continent + " (" + fuel + ")");
      }
    }

    MessageCli.CONTINENT_INFO.printMessage(continentSummary.toString());

    // Find most fuel-consuming continent
    String topContinent = null;
    int maxFuel = -1;
    for (String contEntry : continentSummary) {
      String contName = contEntry.split(" \\(")[0];
      int fuel = fuelPerContinent.getOrDefault(contName, 0);
      if (fuel > maxFuel) {
        topContinent = contEntry;
        maxFuel = fuel;
      }
    }

    MessageCli.FUEL_CONTINENT_INFO.printMessage(topContinent);
  }

  /**
   * Finds the shortest path between two countries using BFS.
   *
   * @param start the starting country name
   * @param end the destination country name
   * @return a list of country names representing the path, or null if no path exists
   */
  private List<String> findShortestPath(String start, String end) {
    // Queue for BFS traversal
    Queue<String> queue = new LinkedList<>();
    // Map to keep track of each node's parent for path reconstruction
    Map<String, String> parent = new HashMap<>();
    // Set to track visited countries
    Set<String> visited = new HashSet<>();

    // Initialize BFS with the start country
    queue.add(start);
    visited.add(start);

    // Perform BFS
    while (!queue.isEmpty()) {
      String current = queue.poll();
      // If destination is reached, exit loop
      if (current.equals(end)) {
        break;
      }

      // Explore all unvisited neighbors
      for (String neighbor : graph.getCountry(current).getNeighbors()) {
        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          parent.put(neighbor, current); // Record the path
          queue.add(neighbor);
        }
      }
    }

    // If end country was never reached, return null
    if (!parent.containsKey(end)) {
      return null;
    }

    // Reconstruct the path from end to start using the parent map
    LinkedList<String> path = new LinkedList<>();
    String step = end;
    path.addFirst(step);

    while (!step.equals(start)) {
      step = parent.get(step);
      path.addFirst(step);
    }

    return path;
  }

  /**
   * Prompts the user to enter a valid country name, repeatedly asking until a valid country is
   * entered. Returns the validated, capitalized country name.
   *
   * @return the validated country name
   */
  private String promptValidCountryName() {
    while (true) {
      // Prompt the user for input
      System.out.println("Insert the name of the country:");
      // Read the user's input from the scanner
      String input = Utils.scanner.nextLine();
      // Capitalize the first letter of each word to match country naming convention
      String capitalizedInput = Utils.capitalizeFirstLetterOfEachWord(input);

      try {
        // Validate if the entered country exists in the graph
        validateCountry(capitalizedInput);
        // If valid, return the formatted country name
        return capitalizedInput;
      } catch (CountryNotFoundException e) {
        // If invalid, notify the user and prompt again
        MessageCli.INVALID_COUNTRY.printMessage(capitalizedInput);
      }
    }
  }

  /**
   * Validates that the given country name exists in the graph.
   *
   * @param name the country name to validate
   * @throws CountryNotFoundException if the country does not exist
   */
  private void validateCountry(String name) throws CountryNotFoundException {
    // Check if the graph contains the specified country
    if (!graph.containsCountry(name)) {
      // Throw exception if the country is not found
      throw new CountryNotFoundException(name);
    }
  }
}
