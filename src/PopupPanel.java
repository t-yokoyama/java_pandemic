import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import static constants.PandemicConstants.*;

public class PopupPanel extends JPanel implements MouseListener {
	
	private static final long serialVersionUID = -2415005610927071534L;
	
	// Data members
	private PandemicGame d_game;
	private boolean d_enableClick;
	private JLabel d_setupLabel;
	private JLabel d_playerTurnLabel;
	private JLabel d_actionLabel;
	private JLabel d_drawLabel;
	private JLabel d_infectLabel;
	private JLabel d_discardLabel;
	private JLabel d_gameEndLabel;
	private JLabel d_textBox;
	private String d_headerText;
	private String d_bodyText;
	private PlayerCardPanel d_drawnPlayerCard;
	private InfectionCardPanel d_drawnInfectCard;

	// Basic modifier methods
	public void enableClicking() { d_enableClick = true; }
	public void disableClicking() { d_enableClick = false; }
	
	public PopupPanel(PandemicGame game) {

		d_game = game;
		d_enableClick = true;
		setLayout(null);
		setOpaque(true);
		setBackground(new Color(0, 0, 0, 180));
		addMouseListener(this);

		d_setupLabel = new JLabel("Initial Setup");
		d_setupLabel.setLocation(10, 10);
		d_setupLabel.setSize(POPUP_COLUMN_WIDTH, 15);

		d_playerTurnLabel = new JLabel("Player Turn");
		d_playerTurnLabel.setLocation(10, 30);
		d_playerTurnLabel.setSize(POPUP_COLUMN_WIDTH, 15);

		d_actionLabel = new JLabel("Action Phase");
		d_actionLabel.setLocation(20, 50);
		d_actionLabel.setSize(POPUP_COLUMN_WIDTH, 15);

		d_drawLabel = new JLabel("Draw Phase");
		d_drawLabel.setLocation(20, 70);
		d_drawLabel.setSize(POPUP_COLUMN_WIDTH, 15);
		
		d_infectLabel = new JLabel("Infection Phase");
		d_infectLabel.setLocation(20, 90);
		d_infectLabel.setSize(POPUP_COLUMN_WIDTH, 15);
		
		d_discardLabel = new JLabel("Discard Phase");
		d_discardLabel.setLocation(20, 110);
		d_discardLabel.setSize(POPUP_COLUMN_WIDTH, 15);

		d_gameEndLabel = new JLabel("Game End");
		d_gameEndLabel.setLocation(10, 130);
		d_gameEndLabel.setSize(POPUP_COLUMN_WIDTH, 15);

		d_drawnPlayerCard = new PlayerCardPanel(null);
		d_drawnPlayerCard.setLocation((POPUP_COLUMN_WIDTH - PLAYER_CARD_WIDTH) / 2, 160);
		d_drawnPlayerCard.setVisible(false);
		
		d_drawnInfectCard = new InfectionCardPanel(false);
		d_drawnInfectCard.setLocation((POPUP_COLUMN_WIDTH - INFECT_CARD_WIDTH) / 2, 270);
		d_drawnInfectCard.setVisible(false);

		d_textBox = new JLabel("<html>test test test</html>");
		d_textBox.setLocation(POPUP_COLUMN_WIDTH + 10, 10);
		d_textBox.setSize(POPUP_WIDTH - POPUP_COLUMN_WIDTH - 20, POPUP_HEIGHT - 20);
		d_textBox.setForeground(Color.WHITE);
		d_textBox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

		add(d_setupLabel);
		add(d_playerTurnLabel);
		add(d_actionLabel);
		add(d_drawLabel);
		add(d_infectLabel);
		add(d_discardLabel);
		add(d_gameEndLabel);
		add(d_drawnPlayerCard);
		add(d_drawnInfectCard);
		add(d_textBox);
	}

	public void updateHeaderText(String header) {

		d_headerText = "<p width=540>" + header + "</p>";
		updateText();
	}
	
	public void updateBodyText(String body) {
		
		d_bodyText = "<p width=540>" + body + "</p>";
		updateText();
	}
	
	public void appendBodyText(String body) {
		
		String line = "<p width=540>" + body + "</p>";
		d_bodyText += line;
		updateText();
	}
	
	private void updateText() {

		d_textBox.setText("<html>" + d_headerText + "<hr>" + d_bodyText + "</html>");
	}
	
	public void activateSetupLabel() {

		d_setupLabel.setForeground(Color.WHITE);
		d_playerTurnLabel.setForeground(Color.GRAY);
		d_actionLabel.setForeground(Color.GRAY);
		d_drawLabel.setForeground(Color.GRAY);
		d_infectLabel.setForeground(Color.GRAY);
		d_discardLabel.setForeground(Color.GRAY);
		d_gameEndLabel.setForeground(Color.GRAY);
	}

	public void activateActionLabel() {

		d_setupLabel.setForeground(Color.GRAY);
		d_playerTurnLabel.setForeground(Color.WHITE);
		d_actionLabel.setForeground(Color.WHITE);
		d_drawLabel.setForeground(Color.GRAY);
		d_infectLabel.setForeground(Color.GRAY);
		d_discardLabel.setForeground(Color.GRAY);
		d_gameEndLabel.setForeground(Color.GRAY);
	}
	
	public void activateDrawLabel() {

		d_setupLabel.setForeground(Color.GRAY);
		d_playerTurnLabel.setForeground(Color.WHITE);
		d_actionLabel.setForeground(Color.GRAY);
		d_drawLabel.setForeground(Color.WHITE);
		d_infectLabel.setForeground(Color.GRAY);
		d_discardLabel.setForeground(Color.GRAY);
		d_gameEndLabel.setForeground(Color.GRAY);
	}
	
	public void activateInfectLabel() {

		d_setupLabel.setForeground(Color.GRAY);
		d_playerTurnLabel.setForeground(Color.WHITE);
		d_actionLabel.setForeground(Color.GRAY);
		d_drawLabel.setForeground(Color.GRAY);
		d_infectLabel.setForeground(Color.WHITE);
		d_discardLabel.setForeground(Color.GRAY);
		d_gameEndLabel.setForeground(Color.GRAY);
	}
	
	public void activateDiscardLabel() {

		d_setupLabel.setForeground(Color.GRAY);
		d_playerTurnLabel.setForeground(Color.WHITE);
		d_actionLabel.setForeground(Color.GRAY);
		d_drawLabel.setForeground(Color.GRAY);
		d_infectLabel.setForeground(Color.GRAY);
		d_discardLabel.setForeground(Color.WHITE);
		d_gameEndLabel.setForeground(Color.GRAY);
	}

	public void activateGameEndLabel() {

		d_setupLabel.setForeground(Color.GRAY);
		d_playerTurnLabel.setForeground(Color.GRAY);
		d_actionLabel.setForeground(Color.GRAY);
		d_drawLabel.setForeground(Color.GRAY);
		d_infectLabel.setForeground(Color.GRAY);
		d_discardLabel.setForeground(Color.GRAY);
		d_gameEndLabel.setForeground(Color.WHITE);
	}

	public void showPlayerCard(PlayerCard card) {
	
		d_drawnPlayerCard.setVisible(true);
		d_drawnPlayerCard.setCard(card);
	}

	public void hidePlayerCard() {
		
		d_drawnPlayerCard.setVisible(false);
	}

	public void showInfectionCard(City card) {
		
		d_drawnInfectCard.setVisible(true);
		d_drawnInfectCard.setCity(card);
	}

	public void hideInfectionCard() {
		
		d_drawnInfectCard.setVisible(false);
	}

	public void mouseClicked(MouseEvent e) {
		
		if (d_enableClick) {
			d_game.popupClicked();
		}
	}
	
	public void mousePressed(MouseEvent e) { }

	public void mouseReleased(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) { }

}
