package constants;

import java.awt.Color;
import java.awt.Font;

public class PandemicConstants {

	// Global game settings
	public static final int NUM_DISEASES = 4; // must match number of groups in cities.dat
	public static final String [] DISEASE_NAMES = { "BLUE", "GREEN", "BLACK", "RED" };
	public static final int MAX_DISEASE_CUBES = 24;
	public static final int [] INFECT_RATE_MAP = {2, 2, 2, 3, 3, 4, 4};
	public static final int NUM_EPIDEMIC_CARDS = 4;
	public static final int MAX_INFECT_DISCARD_PILE = 25;
	public static final int MAX_RESEARCH_STATIONS = 6;

	// File paths
	public static final String FILEDIR = "./external_files";
	public static final String CITIES_DAT_PATH = FILEDIR + "/cities.dat";
	public static final String WORLD_MAP_PATH = FILEDIR + "/worldmap.png";
	public static final String PLAYER_CARD_PATH = FILEDIR + "/player_card_center.png";
	public static final String EPIDEMIC_CARD_PATH = FILEDIR + "/epidemic_card.png";
	public static final String INFECT_CARD_PATH = FILEDIR + "/infection_card.png";
	public static final String DECK_TOGGLE_PATH = FILEDIR + "/deck_toggle.png";
	public static final String [] PLAYER_ICON_PATH = { FILEDIR + "/role_opsexpert.png",
													   FILEDIR + "/role_medic.png",
													   FILEDIR + "/role_researcher.png",
													   FILEDIR + "/role_scientist.png" };
	public static final String [] CURE_ICON_PATH = { FILEDIR + "/cure1.png", 
													 FILEDIR + "/cure2.png",
													 FILEDIR + "/cure3.png",
													 FILEDIR + "/cure4.png" };
	public static final String NOCURE_ICON_PATH = FILEDIR + "/nocure.png";
	public static final String INSTRUCTION_DISPLAY_PATH = FILEDIR + "/instructions.png";

	// GUI component dimensions
	public static final int MAP_WIDTH = 1024;
	public static final int MAP_HEIGHT = 640;
	public static final int MENU_WIDTH = 200;
	public static final int BUTTON_HEIGHT = 30;
	public static final int POPUP_WIDTH = 720;
	public static final int POPUP_HEIGHT = 360;
	public static final int POPUP_COLUMN_WIDTH = 120;
	public static final int PLAYER_CARD_WIDTH = 70;
	public static final int PLAYER_CARD_HEIGHT = 90;
	public static final int INFECT_CARD_WIDTH = 90;
	public static final int INFECT_CARD_HEIGHT = 70;
	public static final int PLAYER_ICON_SIZE = 40;
	public static final int CURE_ICON_SIZE = 30;

	// Map panel feature display parameters
	public static final int CITY_DOT_RADIUS = 8;
	public static final int CITY_NAME_OFFSET = CITY_DOT_RADIUS * 2 + 3;
	public static final int PLAYER_SIZE = CITY_DOT_RADIUS + 5;
	public static final int [] PLAYER_OFFSET_X = { -PLAYER_SIZE, 1, -PLAYER_SIZE, 1};
	public static final int [] PLAYER_OFFSET_Y = { -PLAYER_SIZE, -PLAYER_SIZE, 1, 1};
	
	// Display feature colors
	public static final Color [] PLAYER_COLOR = { Color.YELLOW,
												  new Color(165, 220, 170), // pastel green
												  new Color(250, 140, 180), // pink
												  new Color(110, 185, 215) // pastel blue
												};

	public static final Color [] CITY_COLOR = { new Color(40, 40, 255), // blue
												new Color(15, 135, 100), // green
												new Color(60, 60, 60), // black
												new Color(205, 20, 20) // red
											  };
	
	public static final Color CITY_DEFAULT_COLOR = Color.WHITE;
	public static final Color CITY_RESEARCH_STATION_COLOR = new Color(60, 215, 60);
	public static final Color CITY_SELECT_COLOR = Color.RED;
	public static final Color ACTION_BUTTON_MOUSE_ENTER_COLOR = Color.WHITE;
	public static final Color ACTION_BUTTON_MOUSE_EXIT_COLOR = new Color(185, 125, 40); // marigold
	public static final Color ACTION_BUTTON_FONT_COLOR = new Color(60, 60, 60); // dark gray
	public static final Color SPECIAL_BUTTON_MOUSE_ENTER_COLOR = Color.WHITE;
	public static final Color SPECIAL_BUTTON_MOUSE_EXIT_COLOR = new Color(40, 40, 40); // darker gray
	public static final Color SPECIAL_BUTTON_FONT_COLOR = Color.GRAY;
	public static final Color BUTTON_DISABLED_COLOR = Color.GRAY;
	
	// Game state fonts
	public static final Font GAME_STATE_PANEL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	public static final Font GAME_STATE_TRACKER_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

	// Disallow instantiation
	private PandemicConstants() {}
}
