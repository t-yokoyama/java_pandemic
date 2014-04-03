import java.util.ArrayList;
import java.util.Collections;

import static constants.PandemicConstants.*;

public class InfectionManager {

	// Data members
	private ArrayList<City> d_infectionDeck;
	private ArrayList<City> d_discardPile;
	private int d_infectRateIndex;
	private int d_numOutbreaks;
	private int [] d_diseaseCubesLeft;
	private boolean [] d_cureFound;
	private City d_lastInfectCardDrawn;
	
	// Static variables
	private static int s_currentOutbreakSeed = -1;

	// Basic accessor methods
	public int getInfectRate() { return INFECT_RATE_MAP[d_infectRateIndex]; }
	public int getNumOutbreaks() { return d_numOutbreaks; }
	public City getLastInfectCardDrawn() { return d_lastInfectCardDrawn; }
	public ArrayList<City> getInfectionDeck() { return d_infectionDeck; }
	public ArrayList<City> getDiscardPile() { return d_discardPile; }
	public boolean isCureFound(int diseaseType) { return d_cureFound[diseaseType]; }
	
	// Basic modifier methods
	public void cureDisease(int diseaseType) { d_cureFound[diseaseType] = true; }

	public InfectionManager(ArrayList<City> cityList) {

		d_infectRateIndex = 0;
		d_numOutbreaks = 0;
		d_lastInfectCardDrawn = null;
		d_cureFound = new boolean[NUM_DISEASES];
		d_diseaseCubesLeft = new int [NUM_DISEASES];
		for (int i = 0; i < NUM_DISEASES; i++) {
			d_cureFound[i] = false;
			d_diseaseCubesLeft[i] = MAX_DISEASE_CUBES;
		}

		d_infectionDeck = new ArrayList<City>();
		d_discardPile = new ArrayList<City>();
			for (int i = 0; i < cityList.size(); i++) {
			d_infectionDeck.add(cityList.get(i));
		}
		
		Collections.shuffle(d_infectionDeck);
	}
	
	public void setupInitialInfection(PopupPanel popupPanel) {
		
		City city;
		
		for (int i = 0; i < 3; i++) {
			city = drawInfectionCard();
			infectCitySuppressOutput(city);
			infectCitySuppressOutput(city);
			infectCity(popupPanel, city, true);
		}

		for (int i = 0; i < 3; i++) {
			city = drawInfectionCard();
			infectCitySuppressOutput(city);
			infectCity(popupPanel, city, true);
		}
		
		for (int i = 0; i < 3; i++) {
			city = drawInfectionCard();
			infectCity(popupPanel, city, true);
		}

	}
	
	public void infectCity(PopupPanel popupPanel, City city, boolean primaryInfection) {

		String infectionSummary = "<b>" + city.getName() + "</b> is infected and ";
		
		// Infections of this type of disease no longer happen if the cure is found and the disease eradicated
		int diseaseType = city.getGroup();
		if (d_cureFound[diseaseType] && d_diseaseCubesLeft[diseaseType] == MAX_DISEASE_CUBES) {
			return;
		}
		
		if (city.getInfectLevel() < 3) {
			
			city.incrementInfectionLevel();
			d_diseaseCubesLeft[diseaseType]--;
			infectionSummary += "its infection level has risen to " + city.getInfectLevel() + ".";
			popupPanel.appendBodyText(infectionSummary);
		}
		else {
			
			d_numOutbreaks++;
			if (primaryInfection) {
				s_currentOutbreakSeed++;
			}
			city.setLastOutbreakSeed(s_currentOutbreakSeed);
			ArrayList<City> neighbors = city.getNeighbors();
			ArrayList<City> toInfect = new ArrayList<City>();
			for (int i = 0; i < neighbors.size(); i++) {
				City neighbor = neighbors.get(i);
				if (neighbor.getLastOutbreakSeed() != s_currentOutbreakSeed) {

					if (toInfect.size() == 0) {
						infectionSummary += "there is an OUTBREAK! The infection overflows into <b>" + neighbor.getName() + "</b>";
					}
					else {
						infectionSummary += ", <b>" + neighbor.getName() + "</b>";
					}
					toInfect.add(neighbor);
				}
			}
			
			// If any neighbors were hit, output the infection message for this outbreak and THEN
			// recursively infect those cities; this is necessary to get the messages in the correct order
			if (toInfect.size() > 0) {
				popupPanel.appendBodyText(infectionSummary);
				while (toInfect.size() > 0) {
					infectCity(popupPanel, toInfect.remove(0), false);
				}
			}
			
		}
	}
	
	public void infectCitySuppressOutput(City city) {
		
		int diseaseType = city.getGroup();
		if (d_cureFound[diseaseType] && d_diseaseCubesLeft[diseaseType] == MAX_DISEASE_CUBES) {
			return;
		}
		
		if (city.getInfectLevel() >= 3) {
			System.out.println("Fatal Error: infectCitySuppressOutput must not be called when there is a potential for outbreak.");
		}
			
		city.incrementInfectionLevel();
		d_diseaseCubesLeft[diseaseType]--;
	}
	
	public void infectCityEndOfTurn(PopupPanel popupPanel) {

		City card = drawInfectionCard();
		popupPanel.updateBodyText("The card is <b>" + card.getName() + "</b>.");
		infectCity(popupPanel, card, true);
	}
	
	public void executeEpidemic(PopupPanel popupPanel) {

		d_infectRateIndex++;
		popupPanel.updateBodyText("The infection rate has risen: " + INFECT_RATE_MAP[d_infectRateIndex] + " cards will be drawn from the infection deck every turn.");

		// Take the bottom card of the deck and raise it to maximum infection;
		// if the city already has any amount of infection, then regardless of how
		// infected it was, it goes to maximum infection and causes exactly one outbreak
		City card = d_infectionDeck.remove(d_infectionDeck.size() - 1);
		d_lastInfectCardDrawn = card;
		d_discardPile.add(card);
		popupPanel.appendBodyText("Drawing a card from the bottom of the infection deck: the card is " + card.getName() + ".");
		if (card.getInfectLevel() == 0) {
			
			// Manually increment the infection level twice and use the infectCity
			// interface for the third time to get the message
			infectCitySuppressOutput(card);
			infectCitySuppressOutput(card);
			infectCity(popupPanel, card, true);
		}
		else {
			while (card.getInfectLevel() < 3) {
				infectCitySuppressOutput(card);
			}
			infectCity(popupPanel, card, true);
		}
		
		// Take the infection discard pile, shuffle it, and place it back on top of the deck
		popupPanel.appendBodyText("The infection discard pile has been shuffled and placed back on top of the infection deck.");
		Collections.shuffle(d_discardPile);
		while (!d_discardPile.isEmpty()) {
			card = d_discardPile.remove(0);
			d_infectionDeck.add(0, card);
		}
	}
	
	public void treatCity(City city) {
		
		int diseaseType = city.getGroup();
		if (city.getInfectLevel() > 0) {
			city.decrementInfectionLevel();
			d_diseaseCubesLeft[diseaseType]++;
		}
	}
	
	public void disinfectCity(City city) {
		
		int diseaseType = city.getGroup();
		while (city.getInfectLevel() > 0) {
			city.decrementInfectionLevel();
			d_diseaseCubesLeft[diseaseType]++;
		}
	}

	private City drawInfectionCard() {
		
		City card = null;
		if (d_infectionDeck.size() > 0) {
			card = d_infectionDeck.remove(0);
			d_discardPile.add(card);
		}
		else {
			System.out.println("Fatal Error: No infection cards remaining.");
			System.exit(1);
		}
		d_lastInfectCardDrawn = card;
		return card;
	}
	
	public boolean outOfDiseaseCubes() {
		
		for (int i = 0; i < NUM_DISEASES; i++) {
			if (d_diseaseCubesLeft[i] < 0) {
				return true;
			}
		}
		
		return false;
	}

	public boolean allCuresDiscovered() {
		
		for (int i = 0; i < NUM_DISEASES; i++) {
			if (d_cureFound[i] == false) {
				return false;
			}
		}
		
		return true;
	}
	
	public String generateInfectRateString() {
		
		String temp = "<html> [ ";
		for (int i = 0; i < 7; i++) {
			if (d_infectRateIndex == i) {
				temp += "<font color='#AA0000'>" + INFECT_RATE_MAP[i] + "</font> ";
			}
			else temp += (INFECT_RATE_MAP[i] + " ");
		}
		temp += "] </html>";
		return temp;
	}

	public String generateOutbreakString() {
		
		String temp = "<html> [ ";
		for (int i = 0; i < 8; i++) {
			if (d_numOutbreaks == i) {
				temp += "<font color='#AA0000'>" + i + "</font> ";
			}
			else temp += (i + " ");
		}
		if (d_numOutbreaks > 7) {
			temp += "<font color='#AA0000'>X</font> ] </html>";
		}
		else {
			temp += "X ] </html>";
		}
		
		return temp;
	}

}
