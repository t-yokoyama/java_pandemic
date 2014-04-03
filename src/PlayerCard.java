import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static constants.PandemicConstants.*;

public class PlayerCard {

	// Data members
	private City d_city;
	private boolean d_isEpidemic;
	
	// Basic accessor methods
	public City getCity() { return d_city; }
	public boolean isEpidemic() { return d_isEpidemic; }

	public PlayerCard(City city, boolean isEpidemic) {

		d_city = city;
		d_isEpidemic = isEpidemic;

		if ((isEpidemic && city != null) || (!isEpidemic && city == null)) {
			System.out.println("Fatal Error: Invalid player card defined.");
			System.exit(1);
		}
	}
	
	public static ArrayList<PlayerCard> generateDeck(ArrayList<City> cityList) {
		
		// Generate one card corresponding to each city
		ArrayList<PlayerCard> deck = new ArrayList<PlayerCard>();
		for (int i = 0; i < cityList.size(); i++) {
			
			City city = cityList.get(i);
			deck.add(new PlayerCard(city, false));
		}
		
		// Shuffle the deck to randomize the order
		Collections.shuffle(deck);
		
		return deck;
	}
	
	public static void dealStartingHands(ArrayList<PlayerCard> deck, ArrayList<Player> players) {

		// 2 cards each for 4 players, 3 cards for 3 players, 4 cards for 2 players
		int startingHandSize = 6 - players.size();
		
		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			for (int j = 0; j < startingHandSize; j++) {
				PlayerCard card = deck.remove(0);
				player.addCardToHand(card);
			}
		}
	}
	
	public static void seedEpidemicCards(ArrayList<PlayerCard> deck) {
		
		// Slice the deck into a number of groups equal to numCards as evenly as possible
		int [] boundaryIndices = new int[NUM_EPIDEMIC_CARDS + 1];
		boundaryIndices[0] = 0;
		boundaryIndices[NUM_EPIDEMIC_CARDS] = deck.size();
		for (int i = 1; i < NUM_EPIDEMIC_CARDS; i++) {
			boundaryIndices[i] = deck.size() * i / NUM_EPIDEMIC_CARDS;
		}

		// Randomly insert an epidemic card somewhere within each grouping, starting from the bottom
		// of the deck because an insertion into the deck array will increment the card indices after it
		Random rand = new Random();
		for (int i = NUM_EPIDEMIC_CARDS; i > 0; i--) {

			int min = boundaryIndices[i - 1];
			int range = boundaryIndices[i] - boundaryIndices[i - 1];
			int randPos = min + rand.nextInt(range);

			deck.add(randPos, new PlayerCard(null, true));
		}
	}

	public int getColorIndex() {

		if (d_city != null) {
			return d_city.getGroup();
		}
			
		return -1;
	}
}
