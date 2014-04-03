import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLayeredPane;

import java.awt.Dimension;
import java.util.ArrayList;

import static constants.PandemicConstants.*;

/*
  TO DO LIST
 convert fatal errors to assert statements that can cause exit?
 add comments
 
 implement difficulty setting
 medic auto heals city when cure is discovered
 implement special action cards out of turn
 implement multiple disease types for a given city
 */

public class PandemicGame {

	public enum GameInputState { SETUP,
								 NEW_MOVE,
								 REDO_MOVE,
								 DRIVE_SELECTCITY,
								 DRIVE_CONFIRM,
								 DIRECTFLIGHT_SELECTCARD,
								 DIRECTFLIGHT_CONFIRM,
								 CHARTERFLIGHT_SELECTCARD,
								 CHARTERFLIGHT_SELECTCITY,
								 CHARTERFLIGHT_CONFIRM,
								 SHUTTLEFLIGHT_SELECTCITY,
								 SHUTTLEFLIGHT_CONFIRM,
								 TREATDISEASE_CONFIRM,
								 SHAREKNOWLEDGE_SELECTPLAYER,
								 SHAREKNOWLEDGE_SELECTCARD,
								 SHAREKNOWLEDGE_CONFIRM,
								 BUILDSTATION_SELECTCARD,
								 BUILDSTATION_SELECTCITY,
								 BUILDSTATION_CONFIRM,
								 DISCOVERCURE_SELECTCARDS,
								 DISCOVERCURE_CONFIRM,
								 PASS_CONFIRM,
								 DRAW_CONFIRM,
								 EPIDEMIC_CONFIRM,
								 INFECT_CONFIRM,
								 DISCARD_NONE,
								 DISCARD_SELECTCARDS,
								 ERROR_CONFIRM,
								 GAME_END }
	
	private ArrayList<City> d_cityList;
	private ArrayList<Player> d_playerList;
	private ArrayList<PlayerCard> d_playerDeck;
	private InfectionManager d_infectionManager;

	private GameInputState d_currentState;
	private GameInputState d_nextState;
	private int d_turnNo;
	private int d_moveNo;
	private int d_activePlayer;
	private int d_playerCardsDrawnThisTurn;
	private int d_infectCardsDrawnThisTurn;
	private City d_selectedCity;
	private PlayerCard d_selectedCard;
	private ArrayList<PlayerCard> d_selectedCards;
	private Player d_selectedPlayer;
	
	private JLayeredPane d_contentPane;
	private MapPanel d_mapPanel;
	private PopupPanel d_popupPanel;
	private GameStatePanel d_gameStatePanel;
	private ButtonPanel [] d_actionButton;
	private ButtonPanel d_confirmHandButton;
	private ButtonPanel d_cancelActionButton;
	private ButtonPanel [] d_playerButton;
	private boolean [] d_playerHandDisplay;
	private PlayerCardPanel [][] d_playerCardPanel;
	private JPanel [] d_handTrayPanel;
	private boolean d_deckDisplay;
	private DeckDisplayPanel d_deckDisplayPanel;
	private InstructionPanel d_instructionPanel;

	public ArrayList<Player> getPlayerList() { return d_playerList; }
	public MapPanel getMapPanel() { return d_mapPanel; }
	public InfectionManager getInfectionManager() { return d_infectionManager; }
	public int getMoveNo() { return d_moveNo; }
	public int getTurnNo() { return d_turnNo; }
	public Player activePlayer() { return d_playerList.get(d_activePlayer); }

	public static void main(String[] args) {

		PandemicGame game = new PandemicGame();
		
		// Generate a window to display the map and GUI
		JFrame gwindow = new JFrame("Pandemic Solitaire");
		gwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gwindow.setVisible(true);
		gwindow.setResizable(false);
		gwindow.setContentPane(game.d_contentPane);
		gwindow.pack();
	}

	public PandemicGame() {

		// Initialize the global game state variables
		d_moveNo = 0;
		d_turnNo = 1;
		d_activePlayer = 0;

		// Read in the city data from a flat text data file
		d_cityList = City.readCities();
		City startCity = d_cityList.get(City.getStartCityIndex());

		// Generate list of players
		d_playerList = Player.generatePlayers(startCity);
		d_playerHandDisplay = new boolean[Player.numPlayers()];
		d_deckDisplay = false;

		// Generate player cards based on the cities
		d_playerDeck = PlayerCard.generateDeck(d_cityList);
		PlayerCard.dealStartingHands(d_playerDeck, d_playerList);
		PlayerCard.seedEpidemicCards(d_playerDeck);
		
		// Set up the infection manager
		d_infectionManager = new InfectionManager(d_cityList);
		
		// Set up the content pane
		d_contentPane = new JLayeredPane();
		d_contentPane.setLayout(null);
		d_contentPane.setOpaque(true);
		d_contentPane.setPreferredSize(new Dimension(MAP_WIDTH + MENU_WIDTH, MAP_HEIGHT));

		// Read in the map image file and set up a link to the cities
		d_mapPanel = new MapPanel(this, d_cityList, d_playerList);
		d_mapPanel.setLocation(200,0);
		d_mapPanel.setSize(MAP_WIDTH, MAP_HEIGHT);
		d_contentPane.add(d_mapPanel, JLayeredPane.DEFAULT_LAYER);

		// Set up the user interface buttons

		PlayerCardPanel.readPlayerCardImages();
		InfectionCardPanel.readInfectCardImage();

		d_gameStatePanel = new GameStatePanel(this);
		d_gameStatePanel.setLocation(1, 1);
		d_gameStatePanel.setSize(MENU_WIDTH - 2, MAP_HEIGHT - 9 * BUTTON_HEIGHT - 2);
		d_contentPane.add(d_gameStatePanel, JLayeredPane.DEFAULT_LAYER);
		
		d_actionButton = new ButtonPanel[9];
		d_actionButton[0] = new ButtonPanel(this, ButtonPanel.ButtonType.ACTION_DRIVE, "Drive / Ferry");
		d_actionButton[1] = new ButtonPanel(this, ButtonPanel.ButtonType.ACTION_DIRECTFLIGHT, "Direct Flight");
		d_actionButton[2] = new ButtonPanel(this, ButtonPanel.ButtonType.ACTION_CHARTERFLIGHT, "Charter Flight");
		d_actionButton[3] = new ButtonPanel(this, ButtonPanel.ButtonType.ACTION_SHUTTLEFLIGHT, "Shuttle Flight");
		d_actionButton[4] = new ButtonPanel(this, ButtonPanel.ButtonType.ACTION_TREATDISEASE, "Treat Disease");
		d_actionButton[5] = new ButtonPanel(this, ButtonPanel.ButtonType.ACTION_SHAREKNOWLEDGE, "Share Knowledge");
		d_actionButton[6] = new ButtonPanel(this, ButtonPanel.ButtonType.ACTION_BUILDSTATION, "Build Research Station");
		d_actionButton[7] = new ButtonPanel(this, ButtonPanel.ButtonType.ACTION_DISCOVERCURE, "Discover A Cure");
		d_actionButton[8] = new ButtonPanel(this, ButtonPanel.ButtonType.ACTION_PASS, "Pass");
		for (int i = 0; i < 9; i++) {
			d_actionButton[i].setLocation(1, MAP_HEIGHT - (9 - i) * BUTTON_HEIGHT);
			d_actionButton[i].setSize(MENU_WIDTH - 2, BUTTON_HEIGHT - 1);
			d_contentPane.add(d_actionButton[i], JLayeredPane.DEFAULT_LAYER);
		}
		
		d_confirmHandButton = new ButtonPanel(this, ButtonPanel.ButtonType.CARDS_SELECTED, "OK");
		d_confirmHandButton.setLocation(MENU_WIDTH + MAP_WIDTH - 50 - 20, 20);
		d_confirmHandButton.setSize(50, 30);
		d_contentPane.add(d_confirmHandButton, JLayeredPane.POPUP_LAYER);
		d_confirmHandButton.setVisible(false);

		d_cancelActionButton = new ButtonPanel(this, ButtonPanel.ButtonType.CANCEL_ACTION, "CANCEL MOVE");
		d_cancelActionButton.setLocation((MENU_WIDTH + MAP_WIDTH - POPUP_WIDTH) / 2, (MAP_HEIGHT + POPUP_HEIGHT) / 2 + 20);
		d_cancelActionButton.setSize(100, 30);
		d_contentPane.add(d_cancelActionButton, JLayeredPane.POPUP_LAYER);
		d_cancelActionButton.setVisible(false);

		d_playerButton = new ButtonPanel[Player.numPlayers()];
		for (int i = 0; i < Player.numPlayers(); i++) {
			switch(i) {
			case 0:
				d_playerButton[i] = new ButtonPanel(this, ButtonPanel.ButtonType.PLAYER_1, d_playerList.get(i).getRoleName());
				break;
			case 1:
				d_playerButton[i] = new ButtonPanel(this, ButtonPanel.ButtonType.PLAYER_2, d_playerList.get(i).getRoleName());
				break;
			case 2:
				d_playerButton[i] = new ButtonPanel(this, ButtonPanel.ButtonType.PLAYER_3, d_playerList.get(i).getRoleName());
				break;
			case 3:
				d_playerButton[i] = new ButtonPanel(this, ButtonPanel.ButtonType.PLAYER_4, d_playerList.get(i).getRoleName());
				break;
			}
			d_playerButton[i].setSize(100, 30);			
			d_contentPane.add(d_playerButton[i], JLayeredPane.POPUP_LAYER);
			d_playerButton[i].setVisible(false);
		}
		
		// Allocate the card panels even though they will not be part of the default display
		d_playerCardPanel = new PlayerCardPanel[Player.numPlayers()][13];
		d_handTrayPanel = new JPanel[Player.numPlayers()];

		for (int i = 0; i < Player.numPlayers(); i++) {
			
			d_handTrayPanel[i] = new JPanel();
			d_handTrayPanel[i].setBackground(d_playerList.get(i).color());
			d_handTrayPanel[i].setSize(15 + 2 * (PLAYER_CARD_WIDTH + 5), 20);
			d_handTrayPanel[i].setVisible(false);
			d_contentPane.add(d_handTrayPanel[i], JLayeredPane.POPUP_LAYER);
			for (int j = 0; j < 13; j++) {
				
				d_playerCardPanel[i][j] = new PlayerCardPanel(this);
				d_playerCardPanel[i][j].setVisible(false);
				d_contentPane.add(d_playerCardPanel[i][j], JLayeredPane.POPUP_LAYER);
			}
		}
		
		updateHandPositionsForActivePlayer();

		d_deckDisplayPanel = new DeckDisplayPanel(d_playerDeck, d_infectionManager.getInfectionDeck(), d_infectionManager.getDiscardPile());
		d_deckDisplayPanel.setLocation(MENU_WIDTH,  MAP_HEIGHT - PLAYER_CARD_HEIGHT - 40);
		d_deckDisplayPanel.setSize(MAP_WIDTH, PLAYER_CARD_HEIGHT + 40);
		d_deckDisplayPanel.setVisible(false);
		d_contentPane.add(d_deckDisplayPanel, JLayeredPane.POPUP_LAYER);
		
		d_popupPanel = new PopupPanel(this);
		d_popupPanel.setLocation((MENU_WIDTH + MAP_WIDTH - POPUP_WIDTH) / 2, (MAP_HEIGHT - POPUP_HEIGHT) / 2);
		d_popupPanel.setSize(POPUP_WIDTH, POPUP_HEIGHT);
		d_popupPanel.setVisible(false);
		d_contentPane.add(d_popupPanel, JLayeredPane.MODAL_LAYER);
		
		d_instructionPanel = new InstructionPanel();
		d_instructionPanel.setLocation(0, 0);
		d_instructionPanel.setVisible(false);
		d_contentPane.add(d_instructionPanel, JLayeredPane.POPUP_LAYER);

		d_selectedCity = null;
		d_selectedCard = null;
		d_selectedCards = new ArrayList<PlayerCard>();
		d_selectedPlayer = null;
		d_nextState = GameInputState.SETUP;
		advanceGameState();
	}
	
	private void advanceGameState() {
		
		d_currentState = d_nextState;
		d_nextState = null;

		hideAndResetAllHands();
		
		d_deckDisplay = false;
		d_deckDisplayPanel.setVisible(false);
		
		// Disable city selection on the map panel
		d_mapPanel.disableCitySelection();

		// Disable the popup but enable popup clicking by default in case we are displaying it later
		d_popupPanel.setVisible(false);
		d_popupPanel.enableClicking();

		// Disable and/or hide all the buttons
		d_confirmHandButton.setVisible(false);
		d_cancelActionButton.setVisible(false);
		for (int i = 0; i < Player.numPlayers(); i++) {
			d_playerButton[i].setVisible(false);
		}
		for (int i = 0; i < 9; i++) {
			d_actionButton[i].deactivate();
		}

		d_gameStatePanel.updateHelpText("");

		switch (d_currentState) {
		
		case SETUP:
			d_popupPanel.activateSetupLabel();
			d_popupPanel.updateHeaderText("Initial Infection");
			d_popupPanel.updateBodyText("Drawing 9 cards from the infection deck to determine the initially infected cities:");
			d_infectionManager.setupInitialInfection(d_popupPanel);
			d_popupPanel.setVisible(true);
			d_nextState = GameInputState.NEW_MOVE;
			break;

		case NEW_MOVE:
			if (++d_moveNo == 5) {
			
				// Start of new player turn
				d_turnNo++;
				d_moveNo = 1;
				d_activePlayer = (d_activePlayer + 1) % 4;
				d_playerCardsDrawnThisTurn = 0;
				d_infectCardsDrawnThisTurn = 0;
				updateHandPositionsForActivePlayer();
			}
			// No break
		case REDO_MOVE:
			for (int i = 0; i < 9; i++) {
				d_actionButton[i].activate();
			}
			d_gameStatePanel.updateHelpText("Choose an action for move " + d_moveNo + " of the " + activePlayer().getRoleName() + "'s turn.");
			break;
			
		case DRIVE_SELECTCITY:
			ArrayList<City> neighbors = activePlayer().getCity().getNeighbors();
			for (int i = 0; i < neighbors.size(); i++) {
				d_mapPanel.enableCitySelection(neighbors.get(i));
			}
			d_gameStatePanel.updateHelpText("Select a neighboring city to drive/ferry to.");
			break;
			
		case DIRECTFLIGHT_SELECTCARD:
			enableHandSelection(activePlayer(), false);
			d_gameStatePanel.updateHelpText("Select a player card to take a direct flight to the corresponding city.");
			break;

		case CHARTERFLIGHT_SELECTCARD:
			enableHandSelection(activePlayer(), false);
			d_gameStatePanel.updateHelpText("Select the player card corresponding to the city the player is currently in to take a charter flight to any other city.");
			break;

		case CHARTERFLIGHT_SELECTCITY:
			for (int i = 0; i < d_cityList.size(); i++) {
				if (activePlayer().getCity().getIndex() != i) {
					d_mapPanel.enableCitySelection(d_cityList.get(i));
				}
			}
			d_gameStatePanel.updateHelpText("Select any other city to take a charter flight to.");
			break;
			
		case SHUTTLEFLIGHT_SELECTCITY:
			for (int i = 0; i < d_cityList.size(); i++) {
				City city = d_cityList.get(i);
				if (city.hasResearchStation() && activePlayer().getCity().getIndex() != i) {
					d_mapPanel.enableCitySelection(city);
				}
			}
			d_gameStatePanel.updateHelpText("Select any other city with a research station to take a shuttle flight to.");
			break;
			
		case SHAREKNOWLEDGE_SELECTPLAYER:
			City activePlayerCity = activePlayer().getCity();
			toggleHandDisplay(activePlayer());
			for (int i = 0; i < Player.numPlayers(); i++) {
				if (activePlayer().getIndex() == i){
					continue;
				}
				if (d_playerList.get(i).getCity().equals(activePlayerCity)) {
					toggleHandDisplay(d_playerList.get(i));
					d_playerButton[i].setVisible(true);
				}
			}
			d_gameStatePanel.updateHelpText("Select the player with whom to trade player cards.");
			break;
			
		case SHAREKNOWLEDGE_SELECTCARD:
			enableHandSelection(activePlayer(), false);
			enableHandSelection(d_selectedPlayer, false);
			d_gameStatePanel.updateHelpText("Select the player card to give to or receive from the other player.");
			break;
		
		case BUILDSTATION_SELECTCARD:
			enableHandSelection(activePlayer(), false);
			d_gameStatePanel.updateHelpText("Select the player card corresponding to the city the player is currently in to build a research station there.");
			break;
		
		case BUILDSTATION_SELECTCITY:
			for (int i = 0; i < d_cityList.size(); i++) {
				City city = d_cityList.get(i);
				if (city.hasResearchStation()) {
					d_mapPanel.enableCitySelection(city);
				}
			}
			d_gameStatePanel.updateHelpText("There can not be more than " + MAX_RESEARCH_STATIONS + " research stations. Select an existing research station to destroy in order to build the new one.");
			break;
		
		case DISCOVERCURE_SELECTCARDS:
			enableHandSelection(activePlayer(), true);
			d_gameStatePanel.updateHelpText("Select 5 cards of the same color in order to discover the cure for that disease*. *(The Scientist only needs 4 cards to discover a cure.)");
			break;
		
		case DRIVE_CONFIRM:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateActionLabel();
			d_cancelActionButton.setVisible(true);
			d_popupPanel.updateHeaderText(activePlayer().getRoleName() + " - Turn " + d_turnNo + " - Move " + d_moveNo + " of 4");
			d_popupPanel.updateBodyText("Drive / Ferry to <b>" + d_selectedCity.getName() + "</b>.");
			d_popupPanel.appendBodyText("Proceed with this move?");

			break;
		
		case DIRECTFLIGHT_CONFIRM:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateActionLabel();
			d_cancelActionButton.setVisible(true);
			d_popupPanel.updateHeaderText(activePlayer().getRoleName() + " - Turn " + d_turnNo + " - Move " + d_moveNo + " of 4");
			d_popupPanel.updateBodyText("Take a direct flight to <b>" + d_selectedCard.getCity().getName() + "</b>.");
			d_popupPanel.appendBodyText("This will expend the <b>" + d_selectedCard.getCity().getName() + "</b> card in the " + activePlayer().getRoleName() + "'s hand.");
			d_popupPanel.appendBodyText("Proceed with this move?");
			break;
		
		case CHARTERFLIGHT_CONFIRM:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateActionLabel();
			d_cancelActionButton.setVisible(true);
			d_popupPanel.updateHeaderText(activePlayer().getRoleName() + " - Turn " + d_turnNo + " - Move " + d_moveNo + " of 4");
			d_popupPanel.updateBodyText("Take a charter flight to <b>" + d_selectedCity.getName() + "</b>.");
			d_popupPanel.appendBodyText("This will expend the " + d_selectedCard.getCity().getName() + " card in the " + activePlayer().getRoleName() + "'s hand.");
			d_popupPanel.appendBodyText("Proceed with this move?");
			break;
			
		case SHUTTLEFLIGHT_CONFIRM:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateActionLabel();
			d_cancelActionButton.setVisible(true);
			d_popupPanel.updateHeaderText(activePlayer().getRoleName() + " - Turn " + d_turnNo + " - Move " + d_moveNo + " of 4");
			d_popupPanel.updateBodyText("Take a shuttle flight to <b>" + d_selectedCity.getName() + "</b>.");
			d_popupPanel.appendBodyText("Proceed with this move?");
			break;
		
		case TREATDISEASE_CONFIRM:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateActionLabel();
			d_cancelActionButton.setVisible(true);
			d_popupPanel.updateHeaderText(activePlayer().getRoleName() + " - Turn " + d_turnNo + " - Move " + d_moveNo + " of 4");
			City currentCity = activePlayer().getCity();
			d_popupPanel.updateBodyText("Treat the disease in <b>" + currentCity.getName() + "</b>.");
			if (d_infectionManager.isCureFound(currentCity.getGroup())) {
				d_popupPanel.appendBodyText("Because the cure for this disease has been discovered, this will remove all infection markers from the city.");
			}
			else if (activePlayer().getRole() == Player.Role.MEDIC) {
				d_popupPanel.appendBodyText("Using the Medic's special ability, this will remove all infection markers from the city.");
			}
			else {
				d_popupPanel.appendBodyText("This will remove one infection marker from the city.");
			}
			d_popupPanel.appendBodyText("Proceed with this move?");
			break;
		
		case SHAREKNOWLEDGE_CONFIRM:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateActionLabel();
			d_cancelActionButton.setVisible(true);
			d_popupPanel.updateHeaderText(activePlayer().getRoleName() + " - Turn " + d_turnNo + " - Move " + d_moveNo + " of 4");
			d_popupPanel.updateBodyText("Share knowledge with " + d_selectedPlayer.getRoleName() + ".");
			boolean giveCard = false;
			ArrayList<PlayerCard> activePlayerHand = activePlayer().getHand();
			for (int i = 0; i < activePlayerHand.size(); i++) {
				if (activePlayerHand.get(i).equals(d_selectedCard)) {
					giveCard = true;
					break;
				}
			}
			Player giver;
			Player receiver;
			if (giveCard) {
				giver = activePlayer();
				receiver = d_selectedPlayer;
			}
			else {
				giver = d_selectedPlayer;
				receiver = activePlayer();
			}
			if (d_selectedCard.getCity().equals(activePlayer().getCity())) {
				d_popupPanel.appendBodyText("This will transfer the <b>" + d_selectedCard.getCity().getName() + "</b> card from the " + giver.getRoleName() + "'s hand to the " + receiver.getRoleName() + "'s hand.");
			}
			else {
				d_popupPanel.appendBodyText("Using the Researcher's special ability, this will transfer the <b>" + d_selectedCard.getCity().getName() + "</b> card from the " + giver.getRoleName() + "'s hand to the " + receiver.getRoleName() + "'s hand.");
			}
			d_popupPanel.appendBodyText("Proceed with this move?");
			break;
		
		case BUILDSTATION_CONFIRM:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateActionLabel();
			d_cancelActionButton.setVisible(true);
			d_popupPanel.updateHeaderText(activePlayer().getRoleName() + " - Turn " + d_turnNo + " - Move " + d_moveNo + " of 4");
			d_popupPanel.updateBodyText("Build a research station in <b>" + activePlayer().getCity().getName() + "</b>.");
			if (activePlayer().getRole() == Player.Role.OPS_EXPERT) {
				d_popupPanel.appendBodyText("Using the Operations Expert's special ability, this action does not require a card.");
			}
			else {
				d_popupPanel.appendBodyText("This will expend the <b>" + d_selectedCard.getCity().getName() + "</b> card.");
			}
			d_popupPanel.appendBodyText("Proceed with this move?");
			break;
		
		case DISCOVERCURE_CONFIRM:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateActionLabel();
			d_cancelActionButton.setVisible(true);
			d_popupPanel.updateHeaderText(activePlayer().getRoleName() + " - Turn " + d_turnNo + " - Move " + d_moveNo + " of 4");
			d_popupPanel.updateBodyText("Discover a cure for the " + DISEASE_NAMES[d_selectedCards.get(0).getCity().getGroup()] + " disease.");
			String cards = "";
			for (int i = 0; i < d_selectedCards.size(); i++) {
				PlayerCard card = d_selectedCards.get(i);
				cards += "<b>";
				cards += card.getCity().getName();
				cards += "</b>";
				if (i < d_selectedCards.size() - 1) {
					cards += ", ";
				}
				if (i == d_selectedCards.size() - 2) {
					cards += " and ";
				}
			}
			if (activePlayer().getRole() == Player.Role.SCIENTIST) {
				d_popupPanel.appendBodyText("Using the Scientist's special ability, this will expend the " + cards + " cards.");
			}
			else {
				d_popupPanel.appendBodyText("This will expend the " + cards + " cards.");
			}
			d_popupPanel.appendBodyText("Proceed with this move?");
			break;
			
		case PASS_CONFIRM:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateActionLabel();
			d_cancelActionButton.setVisible(true);
			d_popupPanel.updateHeaderText(activePlayer().getRoleName() + " - Turn " + d_turnNo + " - Move " + d_moveNo + " of 4");
			d_popupPanel.updateBodyText("Pass the turn.");
			d_popupPanel.updateBodyText("This will forfeit all of the " + activePlayer().getRoleName() + "'s remaining moves this turn.");
			d_popupPanel.appendBodyText("Proceed with this move?");
			break;

		case DRAW_CONFIRM:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateDrawLabel();
			PlayerCard card = drawCard(activePlayer());
			if (card == null) {
				gameEndNoPlayerCards();
				return;
			}
			d_popupPanel.showPlayerCard(card);
			d_popupPanel.hideInfectionCard();
			d_popupPanel.updateHeaderText("Drawing a player card from the player deck (" + ++d_playerCardsDrawnThisTurn + " of 2)...");
			if (card.isEpidemic()) {
				d_popupPanel.updateBodyText("The card is an <b>EPIDEMIC</b> card!");
				d_nextState = GameInputState.EPIDEMIC_CONFIRM;
			}
			else {
				d_popupPanel.updateBodyText("The card is <b>" + card.getCity().getName() + "</b>. Added to the <b>" + activePlayer().getRoleName() + "</b>'s hand.");
				if (d_playerCardsDrawnThisTurn < 2) {
					d_nextState = GameInputState.DRAW_CONFIRM;
				}
				else {
					d_nextState = GameInputState.INFECT_CONFIRM;
				}
			}
			break;
			
		case EPIDEMIC_CONFIRM:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateDrawLabel();
			d_popupPanel.updateHeaderText("Resolving epidemic on the drawn card (" + d_playerCardsDrawnThisTurn + " of 2)...");
			d_infectionManager.executeEpidemic(d_popupPanel);
			d_popupPanel.showInfectionCard(d_infectionManager.getLastInfectCardDrawn());
			if (d_playerCardsDrawnThisTurn < 2) {
				d_nextState = GameInputState.DRAW_CONFIRM;
			}
			else {
				d_nextState = GameInputState.INFECT_CONFIRM;
			}
			break;
			
		case INFECT_CONFIRM:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateInfectLabel();
			d_popupPanel.updateHeaderText("Drawing a card from the infection deck (" + ++d_infectCardsDrawnThisTurn + " of " + d_infectionManager.getInfectRate() + ")...");
			d_infectionManager.infectCityEndOfTurn(d_popupPanel);
			d_popupPanel.hidePlayerCard();
			d_popupPanel.showInfectionCard(d_infectionManager.getLastInfectCardDrawn());
			if (d_infectCardsDrawnThisTurn < d_infectionManager.getInfectRate()) {
				d_nextState = GameInputState.INFECT_CONFIRM;
			}
			else {
				if (activePlayer().getHand().size() > 7) {
					d_nextState = GameInputState.DISCARD_SELECTCARDS;
				}
				else {
					d_nextState = GameInputState.DISCARD_NONE;
				}
			}
			break;

		case DISCARD_NONE:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateDiscardLabel();
			d_popupPanel.hideInfectionCard();
			d_popupPanel.updateHeaderText("Evaluating discard phase...");
			d_popupPanel.updateBodyText("The active player has less than equal to 7 cards in hand. No need to discard.");
			d_nextState = GameInputState.NEW_MOVE;
			break;
			
		case DISCARD_SELECTCARDS:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateDiscardLabel();
			d_popupPanel.hideInfectionCard();
			d_popupPanel.updateHeaderText("Evaluating discard phase...");
			d_popupPanel.updateBodyText("The active player has more than the maximum of 7 cards allowed in hand. Select player cards to discard down to 7.");
			d_popupPanel.disableClicking();
			enableHandSelection(activePlayer(), true);
			d_nextState = GameInputState.NEW_MOVE;
			break;
			
		case ERROR_CONFIRM:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateActionLabel();
			d_popupPanel.hidePlayerCard();
			d_popupPanel.hideInfectionCard();

			// It is the responsibility of whoever set the ERROR to update the
			// popup panel's header and body with the appropriate text
			
			d_nextState = GameInputState.REDO_MOVE;
			break;

		case GAME_END:
			d_popupPanel.setVisible(true);
			d_popupPanel.activateGameEndLabel();
			d_popupPanel.hidePlayerCard();
			d_popupPanel.hideInfectionCard();
			d_popupPanel.disableClicking();

		} // end switch
		
		d_gameStatePanel.updateGameStateDisplay();
		
		d_contentPane.repaint();
	}
	
	private PlayerCard drawCard(Player player) {

		if (d_playerDeck.isEmpty()) {
			return null;
		}
		
		PlayerCard card = d_playerDeck.remove(0);
		if (!card.isEpidemic()) {
			player.addCardToHand(card);
		}
		return card;
	}

	private void updateHandPositionsForActivePlayer() {

		int numPlayers = Player.numPlayers();
		for (int i = 0; i < numPlayers; i++) {
			int iAdjusted = (numPlayers + i - d_activePlayer) % numPlayers;
			d_handTrayPanel[i].setLocation(MENU_WIDTH + 10, PLAYER_CARD_HEIGHT + 10 + iAdjusted * (PLAYER_CARD_HEIGHT + 25));
			for (int j = 0; j < 13; j++) {
				d_playerCardPanel[i][j].setLocation(MENU_WIDTH + 20 + (j) * (PLAYER_CARD_WIDTH + 5), 20 + iAdjusted * (PLAYER_CARD_HEIGHT + 25));
			}
			
			// Also update the player button location in the player selection menu
			d_playerButton[i].setLocation(MENU_WIDTH + 40, 60 + iAdjusted * (PLAYER_CARD_HEIGHT + 25));
		}
	}
	
	public void toggleHandDisplay(Player player) {

		int playerIndex = player.getIndex();
		int handSize = player.getHand().size();
		if (!d_playerHandDisplay[playerIndex]) {
			
			// Show the hand
			d_handTrayPanel[playerIndex].setSize(15 + handSize * (PLAYER_CARD_WIDTH + 5), 20);
			d_handTrayPanel[playerIndex].setVisible(true);
			for (int i = 0; i < handSize; i++) {

				if (i > 13) {
					System.out.println("Fatal Error: Exceeded maximum hand capacity.");
				}
				d_playerCardPanel[playerIndex][i].setCard(player.getHand().get(i));
				d_playerCardPanel[playerIndex][i].setVisible(true);
			}
			d_contentPane.moveToBack(d_handTrayPanel[playerIndex]);
		}
		else {
			
			// Hide the hand
			d_handTrayPanel[playerIndex].setVisible(false);
			for (int i = 0; i < handSize; i++) {
				d_playerCardPanel[playerIndex][i].setVisible(false);
			}
		}

		d_playerHandDisplay[playerIndex] = !d_playerHandDisplay[playerIndex];
		d_contentPane.repaint();
	}
	
	public void toggleDeckDisplay() {
		
		if (!d_deckDisplay) {
			d_deckDisplayPanel.setVisible(true);
			d_deckDisplayPanel.updateDiscardPileDisplay();
		}
		else {
			d_deckDisplayPanel.setVisible(false);
		}
		d_deckDisplay = !d_deckDisplay;
		d_contentPane.repaint();
	}
	
	private void enableHandSelection(Player player, boolean multipleSelect) {

		// Switch on the active player's hand
		if (!d_playerHandDisplay[player.getIndex()]) {
			toggleHandDisplay(player);
		}

		int playerIndex = player.getIndex();
		int handSize = player.getHand().size();
		for (int i = 0; i < handSize; i++) {

			if (i > 13) {
				System.out.println("Fatal Error: Exceeded maximum hand capacity.");
			}
			if (multipleSelect) {
				d_playerCardPanel[playerIndex][i].enableMultipleSelection();
			}
			else {
				d_playerCardPanel[playerIndex][i].enableSingleSelection();
			}
		}

		if (multipleSelect) {
			d_confirmHandButton.setLocation(MENU_WIDTH + 30 + (player.getHand().size()) * (PLAYER_CARD_WIDTH + 5), 50);
			d_confirmHandButton.setVisible(true);
		}
	}
	
	private void hideAndResetAllHands() {

		for (int i = 0; i < Player.numPlayers(); i++) {

			d_playerHandDisplay[i] = false;
			d_handTrayPanel[i].setVisible(false);
			for (int j = 0; j < 13; j++) {
				
				d_playerCardPanel[i][j].reset();
				d_playerCardPanel[i][j].setVisible(false);
			}
		}
	}

	public void buttonSelected(ButtonPanel.ButtonType type) {
		
		switch (type) {
		
		case ACTION_DRIVE:
			d_nextState = GameInputState.DRIVE_SELECTCITY;
			break;

		case ACTION_DIRECTFLIGHT:
			d_nextState = GameInputState.DIRECTFLIGHT_SELECTCARD;
			break;
		
		case ACTION_CHARTERFLIGHT:
			d_nextState = GameInputState.CHARTERFLIGHT_SELECTCARD;
			break;
		
		case ACTION_SHUTTLEFLIGHT:
			if (!activePlayer().getCity().hasResearchStation()) {
				d_popupPanel.updateHeaderText("Shuttle Flight Error");
				d_popupPanel.updateBodyText("A shuttle flight can only depart from a city that has a research station.");
				d_nextState = GameInputState.ERROR_CONFIRM;
			}
			else if (City.getNumResearchStations() < 2) {
				d_popupPanel.updateHeaderText("Shuttle Flight Error");
				d_popupPanel.updateBodyText("There are no cities other than the one the player is in that have research stations.");
				d_nextState = GameInputState.ERROR_CONFIRM;
			}
			else {
				d_nextState = GameInputState.SHUTTLEFLIGHT_SELECTCITY;
			}
			break;
		
		case ACTION_TREATDISEASE:
			if (activePlayer().getCity().getInfectLevel() == 0) {
				d_popupPanel.updateHeaderText("Treat Disease Error");
				d_popupPanel.updateBodyText("Only a city that has been infected by disease may be treated.");
				d_nextState = GameInputState.ERROR_CONFIRM;
			}
			else {
				d_nextState = GameInputState.TREATDISEASE_CONFIRM;
			}
			break;
		
		case ACTION_SHAREKNOWLEDGE:
			City activePlayerCity = activePlayer().getCity();
			int numColocated = 0;
			for (int i = 0; i < Player.numPlayers(); i++) {
				if (activePlayer().getIndex() == i){
					continue;
				}
				if (d_playerList.get(i).getCity().equals(activePlayerCity)) {
					numColocated++;
					
					// Remember which player we are potentially trading cards with; if there are multiple
					// candidates then this will be overwritten each iteration through this loop but will
					// be overwritten again during the player select that triggers at the end of this
					d_selectedPlayer = d_playerList.get(i);
				}
			}
			if (numColocated == 0) {
				d_popupPanel.updateHeaderText("Share Knowledge Error");
				d_popupPanel.updateBodyText("There are no other players in the current city with whom to trade player cards.");
				d_nextState = GameInputState.ERROR_CONFIRM;
			}
			else if (numColocated > 1) {
				d_nextState = GameInputState.SHAREKNOWLEDGE_SELECTPLAYER;
			}
			else {
				d_nextState = GameInputState.SHAREKNOWLEDGE_SELECTCARD;
			}
			break;
		
		case ACTION_BUILDSTATION:
			if (activePlayer().getCity().hasResearchStation()) {
				d_popupPanel.updateHeaderText("Build Research Station Error");
				d_popupPanel.updateBodyText("The current city already has a research station.");
				d_nextState = GameInputState.ERROR_CONFIRM;
			}
			else if (activePlayer().getRole() == Player.Role.OPS_EXPERT) {
				if (City.getNumResearchStations() == MAX_RESEARCH_STATIONS) {
					d_nextState = GameInputState.BUILDSTATION_SELECTCITY;
				}
				else {
					d_nextState = GameInputState.BUILDSTATION_CONFIRM;
				}
			}
			else {
				d_nextState = GameInputState.BUILDSTATION_SELECTCARD;
			}
			break;
		
		case ACTION_DISCOVERCURE:
			if (!activePlayer().getCity().hasResearchStation()) {
				d_popupPanel.updateHeaderText("Discover Cure Error");
				d_popupPanel.updateBodyText("A cure may only be discovered at a research station.");
				d_nextState = GameInputState.ERROR_CONFIRM;
			}
			else {
				d_nextState = GameInputState.DISCOVERCURE_SELECTCARDS;
			}
			break;
		
		case ACTION_PASS:
			d_nextState = GameInputState.PASS_CONFIRM;
			break;
		
		case CARDS_SELECTED:
			cardsSelected();
			return;
		
		case CANCEL_ACTION:
			d_nextState = GameInputState.REDO_MOVE;
			break;

		case PLAYER_1:
			d_selectedPlayer = d_playerList.get(0);
			d_nextState = GameInputState.SHAREKNOWLEDGE_SELECTCARD;
			break;

		case PLAYER_2:
			d_selectedPlayer = d_playerList.get(1);
			d_nextState = GameInputState.SHAREKNOWLEDGE_SELECTCARD;
			break;

		case PLAYER_3:
			d_selectedPlayer = d_playerList.get(2);
			d_nextState = GameInputState.SHAREKNOWLEDGE_SELECTCARD;
			break;
		
		case PLAYER_4:
			d_selectedPlayer = d_playerList.get(3);
			d_nextState = GameInputState.SHAREKNOWLEDGE_SELECTCARD;
			break;

		case HELP:
			d_instructionPanel.setVisible(true);
			d_contentPane.moveToFront(d_instructionPanel);
			return; // do not advance game state
		}

		advanceGameState();
	}
	
	public void citySelected(City city) {

		d_selectedCity = city;

		switch (d_currentState) {
		
		case DRIVE_SELECTCITY:
			d_nextState = GameInputState.DRIVE_CONFIRM;
			break;
		case CHARTERFLIGHT_SELECTCITY:
			d_nextState = GameInputState.CHARTERFLIGHT_CONFIRM;
			break;
		case SHUTTLEFLIGHT_SELECTCITY:
			if (!city.hasResearchStation()) {
				d_popupPanel.updateHeaderText("Shuttle Flight Error");
				d_popupPanel.updateBodyText("A shuttle flight can only land in a city that has a research stations.");
				d_nextState = GameInputState.ERROR_CONFIRM;
			}
			else {
				d_nextState = GameInputState.SHUTTLEFLIGHT_CONFIRM;
			}
			break;
		case BUILDSTATION_SELECTCITY:
			d_nextState = GameInputState.BUILDSTATION_CONFIRM;
			break;
		default:
			System.out.println("Fatal Error: Invalid state in citySelected().");
			System.exit(1);
			break;
		}
		
		advanceGameState();
	}
	
	protected void cardSelected(PlayerCard card) {
		
		d_selectedCard = card;
		
		switch (d_currentState) {

		case DIRECTFLIGHT_SELECTCARD:
			d_nextState = GameInputState.DIRECTFLIGHT_CONFIRM;
			break;

		case CHARTERFLIGHT_SELECTCARD:
			if (activePlayer().getCity().equals(card.getCity())) {
				d_nextState = GameInputState.CHARTERFLIGHT_SELECTCITY;
			}
			else {
				d_popupPanel.updateHeaderText("Charter Flight Error");
				d_popupPanel.updateBodyText("The selected card must correspond to the city that the player is currently in.");
				d_nextState = GameInputState.ERROR_CONFIRM;
			}
			break;

		case SHAREKNOWLEDGE_SELECTCARD:
			if (!d_selectedCard.getCity().equals(activePlayer().getCity()) &&
				activePlayer().getRole() != Player.Role.RESEARCHER &&
				d_selectedPlayer.getRole() != Player.Role.RESEARCHER) {
				
				d_popupPanel.updateHeaderText("Share Knowledge Error");
				d_popupPanel.updateBodyText("Both players must be in the city corresponding to the player card being transferred*.");
				d_popupPanel.appendBodyText("*(The researcher may give or receive any card as long as both players are in the same city.)");
				d_nextState = GameInputState.ERROR_CONFIRM;
			}
			else {
				d_nextState = GameInputState.SHAREKNOWLEDGE_CONFIRM;
			}
			break;

		case BUILDSTATION_SELECTCARD:
			if (!activePlayer().getCity().equals(card.getCity())) {
				d_popupPanel.updateHeaderText("Build Research Station Error");
				d_popupPanel.updateBodyText("The selected card must correspond to the city that the player is currently in*.");
				d_popupPanel.appendBodyText("*(The OpsExpert may build a research station without expending a player card.)");
				d_nextState = GameInputState.ERROR_CONFIRM;
			}
			else if (City.getNumResearchStations() == MAX_RESEARCH_STATIONS) {
				d_nextState = GameInputState.BUILDSTATION_SELECTCITY;
			}
			else {
				d_nextState = GameInputState.BUILDSTATION_CONFIRM;
			}
			break;

		default:
			System.out.println("Fatal Error: Invalid state in cardSelected().");
			System.exit(1);
			break;
		}
		
		advanceGameState();
	}

	public void cardsSelected() {

		// Load the set of selected cards into the global storage variable
		d_selectedCards.clear();
		int handSize = activePlayer().getHand().size();
		for (int i = 0; i < handSize; i++) {
			if (d_playerCardPanel[d_activePlayer][i].isSelected()) {
				d_selectedCards.add(d_playerCardPanel[d_activePlayer][i].getCard());
			}
		}
		
		switch (d_currentState) {
		
		case DISCOVERCURE_SELECTCARDS:
			int cardsNeeded = 5;
			if (activePlayer().getRole() == Player.Role.SCIENTIST) {
				cardsNeeded = 4;
			}
			if (d_selectedCards.size() != cardsNeeded) {
				d_popupPanel.updateHeaderText("Discover Cure Error");
				d_popupPanel.updateBodyText("To discover a cure, the player must select 5 player cards*.");
				d_popupPanel.appendBodyText("*(The Scientist only needs 4 cards to discover a cure.)");
				d_nextState = GameInputState.ERROR_CONFIRM;
			}
			else {
				boolean sameType = true;
				int diseaseType = d_selectedCards.get(0).getColorIndex();
				for (int i = 1; i < d_selectedCards.size(); i++) {
					if (d_selectedCards.get(i).getColorIndex() != diseaseType) {
						sameType = false;
					}
				}
				if (!sameType) {
					d_popupPanel.updateHeaderText("Discover Cure Error");
					d_popupPanel.updateBodyText("To discover a cure, the player cards selected must all be of the same color.");
					d_nextState = GameInputState.ERROR_CONFIRM;
				}
				else {
					if (d_infectionManager.isCureFound(diseaseType)) {
						d_popupPanel.updateHeaderText("Discover Cure Error");
						d_popupPanel.updateBodyText("The cure to this disease has already been found.");
						d_nextState = GameInputState.ERROR_CONFIRM;
					}
					d_nextState = GameInputState.DISCOVERCURE_CONFIRM;
				}
			}
			break;

		case DISCARD_SELECTCARDS:

			if (d_selectedCards.size() == handSize - 7) {

				String cardNames = "";
				while (d_selectedCards.size() > 0) {
					PlayerCard card = d_selectedCards.remove(0);
					activePlayer().getHand().remove(card);
					cardNames += "<b>";
					cardNames += card.getCity().getName();
					cardNames += "</b>";
					if (d_selectedCards.size() > 0) {
						cardNames += ", ";
					}
					if (d_selectedCards.size() == 1) {
						cardNames += " and ";
					}

				}
				
				// In the DISCARD_SELECTCARDS state there is a user input transition from selecting
				// which cards to discard to clicking the popup to accept the message;
				// this is one of the few instances where the accepted user input changes without a
				// corresponding change in game input state 
				d_confirmHandButton.setVisible(false);
				hideAndResetAllHands();
				toggleHandDisplay(activePlayer());
				d_popupPanel.updateBodyText("Discarded " + cardNames);
				d_popupPanel.enableClicking();
			}
			else {
				hideAndResetAllHands();
				enableHandSelection(activePlayer(), true);
				d_popupPanel.appendBodyText("Select " + (activePlayer().getHand().size() - 7) + " card(s) to discard in order to be left with 7 cards in hand.");
			}
			d_contentPane.repaint();
			return; // do not advance game state

		default:
			System.out.println("Fatal Error: Invalid state in cardsSelected().");
			System.exit(1);
			break;
		}
		
		advanceGameState();
	}
	
	public void popupClicked() {
		
		switch (d_currentState) {

		// Just click through the confirmation message on these
		case SETUP:
		case DRAW_CONFIRM:
		case DISCARD_NONE:
		case DISCARD_SELECTCARDS: // becomes click-able after cards are discarded
		case ERROR_CONFIRM:
			advanceGameState();
			return;

		case EPIDEMIC_CONFIRM:
		case INFECT_CONFIRM:
			if (d_infectionManager.getNumOutbreaks() > 7) {
				gameEndEighthOutbreak();
			}
			else if (d_infectionManager.outOfDiseaseCubes()) {
				gameEndNoDiseaseCubes();
			}
			else {
				advanceGameState();
			}
			return;

		case DRIVE_CONFIRM:
			actionDrive();
			break;
		case DIRECTFLIGHT_CONFIRM:
			actionDirectFlight();
			break;
		case CHARTERFLIGHT_CONFIRM:
			actionCharterFlight();
			break;
		case SHUTTLEFLIGHT_CONFIRM:
			actionShuttleFlight();
			break;
		case TREATDISEASE_CONFIRM:
			actionTreatDisease();
			break;
		case SHAREKNOWLEDGE_CONFIRM:
			actionShareKnowledge();
			break;
		case BUILDSTATION_CONFIRM:
			actionBuildResearchStation();
			break;
		case DISCOVERCURE_CONFIRM:
			actionDiscoverCure();
			if (d_infectionManager.allCuresDiscovered()) {
				gameEndVictory();
				return;
			}
			break;
		case PASS_CONFIRM:
			d_moveNo = 4;
			break;
			
		default:
			System.out.println("Fatal Error: Invalid state in popupClick().");
			System.exit(1);
			break;
		}

		// Check to see if the end of turn popups need to be invoked
		if (d_moveNo == 4) {
			d_nextState = GameInputState.DRAW_CONFIRM;
		}
		else {
			d_nextState = GameInputState.NEW_MOVE;
		}
		
		advanceGameState();
	}
	
	public void describeActionButton(ButtonPanel.ButtonType type) {
		
		switch(type) {
		
		case ACTION_DRIVE:
			d_gameStatePanel.updateHelpText("Move to any neighboring city.");
			break;
	
		case ACTION_DIRECTFLIGHT:
			d_gameStatePanel.updateHelpText("Expend a player card to move directly to the corresponding city.");
			break;
		
		case ACTION_CHARTERFLIGHT:
			d_gameStatePanel.updateHelpText("Expend the player card of the current city to move directly to any other city.");
			break;
		
		case ACTION_SHUTTLEFLIGHT:
			d_gameStatePanel.updateHelpText("Move from one city that has a research station to any other city that has a research station.");
			break;
		
		case ACTION_TREATDISEASE:
			d_gameStatePanel.updateHelpText("Remove one infection marker from an infected city, or remove all markers from the city if the cure has been found. (The Medic removes all infection markers even without the cure.)");
			break;
		
		case ACTION_SHAREKNOWLEDGE:
			d_gameStatePanel.updateHelpText("Give or receive from any player in the current city the player card corresponding to that city. (The researcher can transfer any card with a player who is in the same city.)");
			break;
		
		case ACTION_BUILDSTATION:
			d_gameStatePanel.updateHelpText("Expend the player card of the current city to build a research station in the city. (The OpsExpert may build research stations without expending cards.)");
			break;
		
		case ACTION_DISCOVERCURE:
			d_gameStatePanel.updateHelpText("Expend 5 cards of the same color at a city that has a research station to discover the cure for the disease of that color. (The Scientist may discard just 4 cards of the same color to discover a cure.)");
			break;
	
		case ACTION_PASS:
			d_gameStatePanel.updateHelpText("Do nothing and advance to the next player's turn.");
			break;
			
		default:
			break;
		}
	}
	
	private void actionDrive() {
		
		activePlayer().setCity(d_selectedCity);
	}
	
	private void actionDirectFlight() {
		
		activePlayer().setCity(d_selectedCard.getCity());
		activePlayer().getHand().remove(d_selectedCard);
	}

	private void actionCharterFlight() {
		
		activePlayer().setCity(d_selectedCity);
		activePlayer().getHand().remove(d_selectedCard);
	}

	private void actionShuttleFlight() {
		
		activePlayer().setCity(d_selectedCity);
	}

	private void actionTreatDisease() {

		City city = activePlayer().getCity();
		int diseaseType = city.getGroup();
		if (activePlayer().getRole() == Player.Role.MEDIC || d_infectionManager.isCureFound(diseaseType)) {
			d_infectionManager.disinfectCity(city);
		}
		else {
			d_infectionManager.treatCity(city);
		}
	}

	private void actionShareKnowledge() {

		boolean giveCard = false;
		ArrayList<PlayerCard> activePlayerHand = activePlayer().getHand();

		for (int i = 0; i < activePlayerHand.size(); i++) {
			if (activePlayerHand.get(i).equals(d_selectedCard)) {
				giveCard = true;
				break;
			}
		}
		if (giveCard) {
			activePlayerHand.remove(d_selectedCard);
			d_selectedPlayer.getHand().add(d_selectedCard);
		}
		else {
			d_selectedPlayer.getHand().remove(d_selectedCard);
			activePlayerHand.add(d_selectedCard);
		}
	}

	private void actionBuildResearchStation() {
		
		if (City.getNumResearchStations() == MAX_RESEARCH_STATIONS) {
			d_selectedCity.destroyResearchStation();
		}
		activePlayer().getCity().buildResearchStation();
		if (activePlayer().getRole() != Player.Role.OPS_EXPERT) {
			activePlayer().getHand().remove(d_selectedCard);
		}
	}

	private void actionDiscoverCure() {

		d_infectionManager.cureDisease(d_selectedCards.get(0).getColorIndex());
		while (!d_selectedCards.isEmpty()) {
			PlayerCard card = d_selectedCards.remove(0);
			activePlayer().getHand().remove(card);
		}
	}
	
	public void gameEndNoDiseaseCubes() {

		d_popupPanel.updateHeaderText("Game Over");
		d_popupPanel.updateBodyText("One of the diseases has gotten so out of control that there are not enough infection markers to represent the spread of its infection.");
		d_nextState = GameInputState.GAME_END;
		advanceGameState();
	}
	
	public void gameEndNoPlayerCards() {
		
		d_popupPanel.updateHeaderText("Game Over");
		d_popupPanel.updateBodyText("Time has run out for humanity. The player card deck has run out of cards and there are no remaining cards available to draw for the turn.");
		d_nextState = GameInputState.GAME_END;
		advanceGameState();
	}
	
	public void gameEndEighthOutbreak() {
		
		d_popupPanel.updateHeaderText("Game Over");
		d_popupPanel.updateBodyText("The eighth outbreak has occurred. The spread of the diseases has become too much for humanity to contain.");
		d_nextState = GameInputState.GAME_END;
		advanceGameState();
	}

	public void gameEndVictory() {
		
		d_popupPanel.updateHeaderText("Victory!");
		d_popupPanel.updateBodyText("You have successfully discovered all of the cures and rescued humanity from the tide of infection! Congratulations!");
		d_nextState = GameInputState.GAME_END;
		advanceGameState();
	}
}

