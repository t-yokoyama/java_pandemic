import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import static constants.PandemicConstants.*;

class GameStatePanel extends JPanel {
	
	private static final long serialVersionUID = -3840340824415955637L;

	// Data members
	private PandemicGame d_game;
	private JLabel d_turnLabel;
	private JLabel d_playerLabel;
	private JLabel d_moveLabel;
	private JLabel d_infectRateLabel;
	private JLabel d_outbreaksLabel;
	private DiscoveredCuresPanel d_discoveredCuresPanel;
	private JTextArea d_helpText;
	private ToggleDisplayPanel d_deckDisplayPanel;
	private ToggleDisplayPanel [] d_handDisplayPanel;
	private ButtonPanel d_helpButton;
	
	public GameStatePanel(PandemicGame game) {

		d_game = game;
		setLayout(null);
		setOpaque(true);
		setBackground(new Color(190, 175, 120));
		
		d_turnLabel = new JLabel();
		d_turnLabel.setLocation(10, 10);
		d_turnLabel.setSize(180, 15);
		
		d_playerLabel = new JLabel();
		d_playerLabel.setLocation(10, 25);
		d_playerLabel.setSize(180, 15);
		
		d_moveLabel = new JLabel();
		d_moveLabel.setLocation(10, 40);
		d_moveLabel.setSize(180, 15);

		JLabel infectRate = new JLabel("Infection Rate: ");
		infectRate.setLocation(10, 60);
		infectRate.setSize(180, 15);
		
		d_infectRateLabel = new JLabel();
		d_infectRateLabel.setLocation(40, 75);
		d_infectRateLabel.setSize(180, 15);
		
		JLabel outbreaks = new JLabel("Outbreaks: ");
		outbreaks.setLocation(10, 90);
		outbreaks.setSize(180, 15);

		d_outbreaksLabel = new JLabel();
		d_outbreaksLabel.setLocation(40, 105);
		d_outbreaksLabel.setSize(180, 15);

		JLabel curesDiscovered = new JLabel("Cures Discovered: ");
		curesDiscovered.setLocation(10, 120);
		curesDiscovered.setSize(180, 15);
		
		d_discoveredCuresPanel = new DiscoveredCuresPanel(d_game.getInfectionManager());
		d_discoveredCuresPanel.setLocation(40, 140);

		d_helpText = new JTextArea();
		d_helpText.setLocation(10, 190);
		d_helpText.setSize(176, 95);
		d_helpText.setLineWrap(true);
		d_helpText.setWrapStyleWord(true);
		d_helpText.setEditable(false);
		d_helpText.setBackground(new Color(190, 175, 120));

		d_deckDisplayPanel = new ToggleDisplayPanel(d_game, null, DECK_TOGGLE_PATH);
		d_deckDisplayPanel.setLocation(10, 334);
		d_deckDisplayPanel.setSize(176, 20);

		d_helpButton = new ButtonPanel(d_game, ButtonPanel.ButtonType.HELP, "Help");
		d_helpButton.setLocation(145, 10);
		d_helpButton.setSize(40, 25);
		
		d_turnLabel.setFont(GAME_STATE_PANEL_FONT);
		d_playerLabel.setFont(GAME_STATE_PANEL_FONT);
		d_moveLabel.setFont(GAME_STATE_PANEL_FONT);
		infectRate.setFont(GAME_STATE_PANEL_FONT);
		d_infectRateLabel.setFont(GAME_STATE_TRACKER_FONT);
		outbreaks.setFont(GAME_STATE_PANEL_FONT);
		d_outbreaksLabel.setFont(GAME_STATE_TRACKER_FONT);
		curesDiscovered.setFont(GAME_STATE_PANEL_FONT);
		d_helpText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

		add(d_turnLabel);
		add(d_playerLabel);
		add(d_moveLabel);
		add(infectRate);
		add(d_infectRateLabel);
		add(outbreaks);
		add(d_outbreaksLabel);
		add(curesDiscovered);
		add(d_discoveredCuresPanel);
		add(d_helpText);
		add(d_deckDisplayPanel);
		add(d_helpButton);

		d_handDisplayPanel = new ToggleDisplayPanel[Player.numPlayers()];
		for (int i = 0; i < Player.numPlayers(); i++) {
			d_handDisplayPanel[i] = new ToggleDisplayPanel(d_game, d_game.getPlayerList().get(i), PLAYER_ICON_PATH[i]);
			d_handDisplayPanel[i].setSize(PLAYER_ICON_SIZE + 4, PLAYER_ICON_SIZE + 4);
			add(d_handDisplayPanel[i]);
		}

		updateGameStateDisplay();
	}
	
	public void updateGameStateDisplay() {
		
		d_turnLabel.setText("<html>Turn #: <font color='#AA0000'><b>" + (d_game.getTurnNo()) + "</b></font></html>");
		d_playerLabel.setText("<html>Player: <font color='#AA0000'><b>" + d_game.activePlayer().getRoleName() + "</b></font></html>");
		d_moveLabel.setText("<html>Move #: <font color='#AA0000'><b>" + ((d_game.getMoveNo() - 1) % 4 + 1) + " / 4</b></font></html>");
		d_infectRateLabel.setText(d_game.getInfectionManager().generateInfectRateString());
		d_outbreaksLabel.setText(d_game.getInfectionManager().generateOutbreakString());
		
		int numPlayers = Player.numPlayers();
		for (int i = 0; i < numPlayers; i++) {
			int iAdjusted = (numPlayers + i - d_game.activePlayer().getIndex()) % numPlayers;
			d_handDisplayPanel[i].setLocation(10 + iAdjusted * (PLAYER_ICON_SIZE + 4), 290);
		}
		
		enableTogglePanels();
	}
	
	public void updateHelpText(String text) {
		
		d_helpText.setText(text);
	}

	public void enableTogglePanels() {

		for (int i = 0; i < Player.numPlayers(); i++) {
			d_handDisplayPanel[i].enableToggling();
		}
		d_deckDisplayPanel.enableToggling();
	}
	
	public void disableTogglePanels() {
		
		for (int i = 0; i < Player.numPlayers(); i++) {
			d_handDisplayPanel[i].disableToggling();
		}
		d_deckDisplayPanel.disableToggling();
	}
}
