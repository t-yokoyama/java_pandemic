import java.util.ArrayList;
import java.awt.Color;

import static constants.PandemicConstants.*;

public class Player {
	
	public enum Role { OPS_EXPERT, MEDIC, RESEARCHER, SCIENTIST }

	// Data members
	private String d_roleName;
	private Role d_role;
	private int d_index;
	private City d_city;
	private ArrayList<PlayerCard> d_hand;

	// Static variables
	private static int s_playerCount = 0;

	// Basic accessor methods
	public String getRoleName() { return d_roleName; }
	public Role getRole() { return d_role; }
	public int getIndex() { return d_index; }
	public City getCity() { return d_city; }
	public ArrayList<PlayerCard> getHand() { return d_hand; }
	public Color color() { return PLAYER_COLOR[d_index]; }
	public static int numPlayers() { return s_playerCount; } 

	// Basic modifier methods
	public void setCity(City city) { d_city = city; }
	
	public Player(Role role, String roleName, City startCity) {
		
		d_role = role;
		d_roleName = roleName;
		d_index = s_playerCount++;
		d_city = startCity;
		d_hand = new ArrayList<PlayerCard>();
		
		if (s_playerCount > 4) {
			System.out.println("Fatal Error: Exceeded max number of players.");
			System.exit(1);
		}
	}
	
	public static ArrayList<Player> generatePlayers(City startCity) {

		ArrayList<Player> playerList = new ArrayList<Player>();
		
		playerList.add(new Player(Role.OPS_EXPERT, "OpsExpert", startCity));
		playerList.add(new Player(Role.MEDIC, "Medic", startCity));
		playerList.add(new Player(Role.RESEARCHER, "Researcher", startCity));
		playerList.add(new Player(Role.SCIENTIST, "Scientist", startCity));
		
		return playerList;
	}
	
	public void addCardToHand(PlayerCard card) {
		d_hand.add(card);
	}
}

