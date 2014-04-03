import java.util.ArrayList;
import java.awt.Color;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import static constants.PandemicConstants.*;

public class City {

	// Data members
	private String d_name;
	private int d_index;
	private int d_group;
	private int d_xpos;
	private int d_ypos;
	private int d_infectLevel;
	private int d_lastOutbreakSeed;
	private boolean d_researchStation;
	private ArrayList<City> d_neighbors;
	
	// Static variables
	public static int s_startCityIndex = -1;
	public static int s_numResearchStations = 0;

	// Basic accessor methods
	public String getName() { return d_name; }
	public int getIndex() { return d_index; }
	public int getGroup() { return d_group; }
	public int getX() { return d_xpos; }
	public int getY() { return d_ypos; }
	public int getInfectLevel() { return d_infectLevel; }
	public int getLastOutbreakSeed() { return d_lastOutbreakSeed; }
	public boolean hasResearchStation() { return d_researchStation; }
	public ArrayList<City> getNeighbors() { return d_neighbors; }
	public static int getStartCityIndex() { return s_startCityIndex; }
	public static int getNumResearchStations() { return s_numResearchStations; }
	public Color color() { return CITY_COLOR[d_group]; }

	public City(String name, int index, int group, int xpos, int ypos) {

		d_name = name;
		d_index = index;
		d_group = group;
		d_xpos = xpos;
		d_ypos = ypos;
		d_infectLevel = 0;
		d_lastOutbreakSeed = -1;
		d_researchStation = false;
		d_neighbors = new ArrayList<City>();
	}

	public static ArrayList<City> readCities() {

		ArrayList<City> cityList = new ArrayList<City>();

		int state = 0;
		int lineNo = 0;
		BufferedReader reader = null;
		String buffer = null;
		String[] tokens = null;
		final String delimiters = "[ \t]+";
		boolean errorFound = false;
		
		try {
			
			reader = new BufferedReader(new FileReader(CITIES_DAT_PATH));

			while (((buffer = reader.readLine()) != null) && (!errorFound)) {

				lineNo++;

				// skip whitespace
				buffer = buffer.trim();
				if (buffer.length() == 0)
					continue;
				
				// validate different syntax depending on the file 'section state'
				switch (state) {

				case 0:
					// state 0: unknown file
					if (buffer.equals("BEGIN_CITIES_FILE")) {
						state = 1;
					}
					else {
						System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): expected BEGIN_CITIES_FILE file header.");
						errorFound = true;
					}
					break;
					
				case 1:
					// state 1: cities data file confirmed
					if (buffer.equals("BEGIN_NODES")) {
						state = 2;
					}
					else {
						System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): expected BEGIN_NODES section header.");
						errorFound = true;
					}
					break;
					
				case 2:
					// state 2: nodes section opened
					if (buffer.equals("END_NODES")) {
						state = 3;
					}
					else {

						// parse the line that was read for proper city node syntax
						
						int index;
						int group;
						int xpos;
						int ypos;
						boolean nodeParseError = false;

						tokens = buffer.split(delimiters);
						if (tokens.length != 5) {
							nodeParseError = true;
						}
						else {

							try {
								index = Integer.parseInt(tokens[0]);
								group = Integer.parseInt(tokens[2]);
								xpos = Integer.parseInt(tokens[3]);
								ypos = Integer.parseInt(tokens[4]);
								
								if (index != cityList.size()) {
									System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): city index must begin at 0 and count up.");
									errorFound = true;
								}
								
								if (group < 0 || group > 3) {
									System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): group number must be between 0 and 3.");
									errorFound = true;
								}
								
								if (xpos < 0 || xpos > MAP_WIDTH) {
									System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): x coordinate must be between 0 and map width of " + MAP_WIDTH + ".");
									errorFound = true;
								}

								if (ypos < 0 || ypos > MAP_HEIGHT) {
									System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): y coordinate must be between 0 and map height of " + MAP_HEIGHT + ".");
									errorFound = true;
								}
								
								if (!errorFound) {

									// Parsed a city node correctly, construct and store it
									cityList.add(new City(tokens[1], index, group, xpos, ypos));
								}
							}
							catch (NumberFormatException e) {
								nodeParseError = true;
							}
						}

						if (nodeParseError) {						
							System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): expected node syntax ([index][cityname][group][x][y]), or END_NODES section footer.");
							errorFound = true;
						}
					}
					break;
					
				case 3:
					// state 3: nodes section closed
					if (buffer.equals("BEGIN_EDGES")) {
						state = 4;
					}
					else {
						System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): expected BEGIN_EDGES section header.");
						errorFound = true;
					}
					break;
					
				case 4:
					// state 4: edges section opened
					if (buffer.equals("END_EDGES")) {
						state = 5;
					}
					else {
						
						// parse the line that was read for proper city node syntax
						
						int index1;
						int index2;
						boolean edgeParseError = false;

						tokens = buffer.split(delimiters);
						if (tokens.length != 2) {
							edgeParseError = true;
						}
						else {

							try {
								index1 = Integer.parseInt(tokens[0]);
								index2 = Integer.parseInt(tokens[1]);
								
								if (index1 < 0 || index1 >= cityList.size()) {
									System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): edges must connect valid city indices.");
									errorFound = true;
								}
								
								if (index2 < 0 || index2 >= cityList.size()) {
									System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): edges must connect valid city indices.");
									errorFound = true;
								}
							
								if (!errorFound) {

									City city1 = cityList.get(index1);
									City city2 = cityList.get(index2);
									
									// Check for duplicate edges already defined
									if (city1.d_neighbors.contains(city2) || city2.d_neighbors.contains(city1)) {
										System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): duplicate edge previously defined.");
										errorFound = true;
									}
									else {
										
										// Parsed a city-to-city edge correctly, update the corresponding city objects
										city1.d_neighbors.add(city2);
										city2.d_neighbors.add(city1);
									}
								}
							}
							catch (NumberFormatException e) {
								edgeParseError = true;
							}
						}

						if (edgeParseError) {
							System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): expected edge syntax ([index1][index2]), or END_EDGES section footer.");
							errorFound = true;
						}

					}
					break;
					
				case 5:
					// state 5: edges section closed
					tokens = buffer.split(delimiters);
					if ((tokens.length == 2) && (tokens[0].equals("START_CITY:"))) {

						int startCityIndex;
						try {
							startCityIndex = Integer.parseInt(tokens[1]);
							if (startCityIndex < 0 || startCityIndex >= cityList.size()) {
								throw new NumberFormatException();
							}
							
							// Set the global start city index and give the city a research station
							s_startCityIndex = startCityIndex;
							cityList.get(startCityIndex).buildResearchStation();
							state = 6;
						}
						catch (NumberFormatException e) {
							System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): starting city must be a valid city index.");
							errorFound = true;
						}
					}
					else {
						System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): expected START_CITY: [city] field.");
						errorFound = true;
					}
					break;
					
				case 6:
					// state 6: starting city parsed
					if (buffer.equals("END_CITIES_FILE")) {
						state = 7;
					}
					else {
						System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): expected END_CITIES_FILE file footer.");
						errorFound = true;
					}
					break;

				case 7:
					// state 7: after cities data file syntax
					System.out.println("Syntax Error: " + CITIES_DAT_PATH + "(" + lineNo + "): unparseable syntax detected following the CITIES_FILE definition.");
					errorFound = true;
					break;
					
				default:
					// unknown state
					System.out.println("Fatal Error: " + CITIES_DAT_PATH + "(" + lineNo + "): undefined file state.");
					System.exit(1);
					break;
				}
			}
			
		}
		catch (IOException e) {
			System.out.println("Error: " + CITIES_DAT_PATH + " could not be opened.");
			errorFound = true;
		}
		finally {
			
			try {
				if (reader != null) {
					reader.close();
				}
			}
			catch (IOException e) {
				System.out.println("Error: " + CITIES_DAT_PATH + " could not be closed.");
				errorFound = true;
			}
		}
		
		if (errorFound) {
			
			// We cannot recover from an undefined or unparseable city data file
			System.exit(1);
		}
		
		return cityList;
	}
	
	public void incrementInfectionLevel() {

		if (++d_infectLevel > 3) {
			System.out.println("Fatal Error: Infection level of a city may not exceed 3.");
			System.exit(1);
		}
	}
	
	public void decrementInfectionLevel() {

		if (--d_infectLevel < 0) {
			System.out.println("Fatal Error: Infection level of a city may not fall below 0.");
			System.exit(1);
		}
	}
	
	public void setLastOutbreakSeed(int seed) {
		d_lastOutbreakSeed = seed;
	}
	
	public void buildResearchStation() {
		
		if (d_researchStation) {
			System.out.println("Fatal Error: Attempting to build a research station in a city that already has one.");
		}
		d_researchStation = true;
		s_numResearchStations++;
	}

	public void destroyResearchStation() {

		if (!d_researchStation) {
			System.out.println("Fatal Error: Attempting to destroy a research station in a city that has none.");
		}
		d_researchStation = false;
		s_numResearchStations--;
	}
}
